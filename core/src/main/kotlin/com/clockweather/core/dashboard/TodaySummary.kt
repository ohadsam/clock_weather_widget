package com.clockweather.core.dashboard

import com.clockweather.core.agenda.Agenda
import com.clockweather.core.agenda.AgendaEvent
import com.clockweather.core.agenda.SpecialEvents
import com.clockweather.core.hebrew.HebrewCalendar
import com.clockweather.core.hebrew.HolidayKind
import com.clockweather.core.hebrew.Holidays
import com.clockweather.core.hebrew.Omer
import com.clockweather.core.hebrew.WeeklyPortion
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * The "Today and next" dashboard card: everything coming up in one short list, most useful first (day's holiday / portion / Omer, weather,
 * alarm, the next events, running timers, close special events, candle lighting or sunset). Pure: all inputs are parameters; the app
 * only fills [Facts] from what it already has. At most [MAX_ROWS] rows so the card stays a glance.
 */
object TodaySummary {
    const val MAX_ROWS = 9
    data class Row(val icon: String, val text: String)

    data class Facts(
        val now: Instant, val zone: ZoneId, val hebrew: Boolean, val inIsrael: Boolean, val use24h: Boolean,
        val sunset: LocalTime?, val candleLighting: LocalTime?,
        val events: List<AgendaEvent>, val special: List<SpecialEvents.Item>, val timerLines: List<String>,
        val alarmText: String?, val weatherLine: String?,
    )

    fun rows(f: Facts): List<Row> {
        val local = f.now.atZone(f.zone)
        val day = local.toLocalDate()
        val tf = DateTimeFormatter.ofPattern(if (f.use24h) "HH:mm" else "h:mm a", Locale.ENGLISH)
        val out = ArrayList<Row>()

        // 1. the day itself: holiday, else weekly portion (Hebrew only, as in the date card), plus the Omer
        val holiday = Holidays.on(day, f.inIsrael).firstOrNull { it.kind != HolidayKind.SHABBAT }
        val parsha = if (holiday == null && f.hebrew) WeeklyPortion.label(day, f.inIsrael)?.let { "פרשת $it" } else null
        (holiday?.name(f.hebrew) ?: parsha)?.let { out += Row("✡", it) }
        Omer.day(HebrewCalendar.fromGregorian(day))?.let { out += Row("🌾", Omer.text(it, f.hebrew)) }

        f.weatherLine?.takeIf { it.isNotBlank() }?.let { out += Row("🌤", it) }
        f.alarmText?.takeIf { it.isNotBlank() }?.let { out += Row("⏰", it) }

        // 2. the next calendar events of today (not yet over), at most 3
        val nowMs = f.now.toEpochMilli()
        Agenda.forToday(f.events, f.now, f.zone).filter { it.endMs > nowMs || it.allDay }.take(3).forEach { e ->
            out += if (e.allDay) Row("📌", e.title) else Row("🕒", tf.format(Instant.ofEpochMilli(e.startMs).atZone(f.zone)) + " " + e.title)
        }

        f.timerLines.take(2).forEach { out += Row("⏳", it.removePrefix("⏳ ").removePrefix("⏱ ").trim()) }
        f.special.take(2).forEach { out += Row(it.emoji, it.title + " · " + SpecialEvents.whenText(it.daysLeft, f.hebrew)) }

        // 3. the next time marker of the evening
        val time = LocalTime.of(local.hour, local.minute)
        when {
            f.candleLighting != null && time.isBefore(f.candleLighting) -> out += Row("🕯", (if (f.hebrew) "הדלקת נרות " else "Candle lighting ") + tf.format(f.candleLighting))
            f.sunset != null && time.isBefore(f.sunset) -> out += Row("🌇", (if (f.hebrew) "שקיעה " else "Sunset ") + tf.format(f.sunset))
        }
        return out.take(MAX_ROWS)
    }
}
