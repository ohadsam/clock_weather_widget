package com.clockweather.core.hebrew

import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

/** The lines of a small date card (the Wear OS tile): weekday + date, Hebrew date, then the holiday or the weekly portion. Pure: day, place flag and language are parameters. */
object DateCardText {
    data class Card(val gregorian: String, val hebrewDate: String, val extra: String?)

    fun of(day: LocalDate, inIsrael: Boolean, hebrew: Boolean): Card {
        val loc = if (hebrew) Locale("he") else Locale.ENGLISH
        val weekday = day.dayOfWeek.getDisplayName(TextStyle.FULL, loc)
        val greg = "$weekday, ${day.dayOfMonth}.${day.monthValue}"
        val hd = HebrewCalendar.fromGregorian(day)
        val holiday = Holidays.on(day, inIsrael).firstOrNull { it.kind != HolidayKind.SHABBAT }?.name(hebrew)
        val parsha = if (holiday == null) WeeklyPortion.label(day, inIsrael) else null
        return Card(greg, HebrewDateFormat.full(hd, hebrew, withYear = false), holiday ?: parsha)
    }

    /** Milliseconds until the next local midnight (the card only changes daily). */
    fun untilMidnightMs(nowMs: Long, zone: java.time.ZoneId): Long {
        val now = java.time.Instant.ofEpochMilli(nowMs).atZone(zone)
        return java.time.Duration.between(now, now.toLocalDate().plusDays(1).atStartOfDay(zone)).toMillis().coerceAtLeast(60_000L)
    }
}
