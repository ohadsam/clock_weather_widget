package com.clockweather.core.hebrew

import java.time.LocalDate
import java.time.LocalTime

/** Text for a Hebrew date, plus the "after nightfall the Hebrew day has already changed" rule. */
object HebrewDateFormat {
    /** `י״ט בכסלו תשפ״ז` (he) or `19 Kislev 5787` (en). */
    fun full(date: HebrewDate, hebrew: Boolean, withYear: Boolean = true): String =
        if (hebrew) {
            val core = "${Gematria.format(date.day)} ב${date.monthName(true)}"
            if (withYear) "$core ${Gematria.year(date.year)}" else core
        } else {
            val core = "${date.day} ${date.monthName(false)}"
            if (withYear) "$core ${date.year}" else core
        }

    /** Short form for tight widgets: `י״ט כסלו` / `19 Kislev`. */
    fun short(date: HebrewDate, hebrew: Boolean): String =
        if (hebrew) "${Gematria.format(date.day)} ${date.monthName(true)}" else "${date.day} ${date.monthName(false)}"

    /**
     * The Hebrew day starts at nightfall. When [rollAtSunset] is on and local [time] is at or after [sunset],
     * the Hebrew date shown is the next day's. [sunset] null (unknown) disables the roll.
     */
    fun hebrewDateAt(day: LocalDate, time: LocalTime, sunset: LocalTime?, rollAtSunset: Boolean): HebrewDate {
        val base = HebrewCalendar.fromGregorian(day)
        return if (rollAtSunset && sunset != null && !time.isBefore(sunset)) HebrewCalendar.fromGregorian(day.plusDays(1)) else base
    }
}
