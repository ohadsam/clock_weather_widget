package com.clockweather.core.alarm

import java.time.DayOfWeek
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/** The next alarm as the system reports it (AlarmManager.getNextAlarmClock) plus which app set it. */
data class NextAlarm(val triggerAtMs: Long, val ownerPackage: String?)

object AlarmIndicator {
    /** [preferredPackage] empty means "any alarm app". */
    fun matches(alarm: NextAlarm, preferredPackage: String): Boolean =
        preferredPackage.isEmpty() || alarm.ownerPackage == preferredPackage

    /** Hidden when the alarm is further away than [withinHours] (0 = always show). */
    fun visible(alarm: NextAlarm?, now: Instant, withinHours: Int, preferredPackage: String): Boolean {
        alarm ?: return false
        if (!matches(alarm, preferredPackage)) return false
        if (alarm.triggerAtMs <= now.toEpochMilli()) return false
        return withinHours == 0 || alarm.triggerAtMs - now.toEpochMilli() <= withinHours * 3_600_000L
    }

    /** `07:00`, `Tomorrow 07:00`, `Tue 07:00`. */
    fun label(alarm: NextAlarm, now: Instant, zone: ZoneId, use24h: Boolean, hebrew: Boolean): String {
        val at = Instant.ofEpochMilli(alarm.triggerAtMs).atZone(zone)
        val time = DateTimeFormatter.ofPattern(if (use24h) "HH:mm" else "h:mm a", Locale.ENGLISH).format(at)
        val today = now.atZone(zone).toLocalDate()
        return when (val days = java.time.temporal.ChronoUnit.DAYS.between(today, at.toLocalDate())) {
            0L -> time
            1L -> (if (hebrew) "מחר " else "Tomorrow ") + time
            else -> if (days in 2..6) dayName(at.dayOfWeek, hebrew) + " " + time else at.toLocalDate().toString() + " " + time
        }
    }

    private fun dayName(d: DayOfWeek, hebrew: Boolean): String = if (hebrew) when (d) {
        DayOfWeek.SUNDAY -> "ראשון"; DayOfWeek.MONDAY -> "שני"; DayOfWeek.TUESDAY -> "שלישי"; DayOfWeek.WEDNESDAY -> "רביעי"
        DayOfWeek.THURSDAY -> "חמישי"; DayOfWeek.FRIDAY -> "שישי"; DayOfWeek.SATURDAY -> "שבת"
    } else d.getDisplayName(java.time.format.TextStyle.SHORT, Locale.ENGLISH)
}
