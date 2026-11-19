package com.clockweather.core.clock

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/** A second clock under the main one: a label and a time zone. */
data class ClockZone(val id: String, val label: String)

data class WorldClockLine(val label: String, val time: String, val dayShift: Int, val isNight: Boolean)

object WorldClock {
    /** Zones offered in Settings -> Clocks (user can also type a zone id); validated, bad ids are dropped. */
    val POPULAR = listOf(
        "Asia/Jerusalem", "America/New_York", "America/Los_Angeles", "Europe/London", "Europe/Paris",
        "Europe/Moscow", "Asia/Dubai", "Asia/Kolkata", "Asia/Bangkok", "Asia/Tokyo", "Australia/Sydney", "America/Sao_Paulo",
    )

    fun validZone(id: String): ZoneId? = runCatching { ZoneId.of(id) }.getOrNull()

    /** `dayShift` is -1 / 0 / +1 relative to the main clock's zone so the UI can add "yesterday / tomorrow". */
    fun line(now: Instant, main: ZoneId, zone: ClockZone, use24h: Boolean, locale: Locale = Locale.ENGLISH): WorldClockLine? {
        val z = validZone(zone.id) ?: return null
        val local = now.atZone(z)
        val shift = java.time.temporal.ChronoUnit.DAYS.between(now.atZone(main).toLocalDate(), local.toLocalDate()).toInt().coerceIn(-1, 1)
        val pattern = if (use24h) "HH:mm" else "h:mm a"
        val hour = local.hour
        return WorldClockLine(zone.label, DateTimeFormatter.ofPattern(pattern, locale).format(local), shift, hour < 6 || hour >= 20)
    }

    fun shiftText(shift: Int, hebrew: Boolean): String = when (shift) {
        -1 -> if (hebrew) "אתמול" else "yesterday"
        1 -> if (hebrew) "מחר" else "tomorrow"
        else -> ""
    }

    /** Main clock digits as the four flip cards need them: "09:05" -> [0,9,0,5]; 12-hour mode drops the leading zero card. */
    fun digits(hour: Int, minute: Int, use24h: Boolean): IntArray {
        val h = if (use24h) hour else (if (hour % 12 == 0) 12 else hour % 12)
        return intArrayOf(h / 10, h % 10, minute / 10, minute % 10)
    }
}
