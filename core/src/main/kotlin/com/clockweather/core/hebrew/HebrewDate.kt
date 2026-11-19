package com.clockweather.core.hebrew

import java.time.DayOfWeek
import java.time.LocalDate

/**
 * Hebrew calendar arithmetic (fixed-day algorithm of Dershowitz and Reingold, "Calendrical Calculations").
 * Pure and offline: the widget shows the Hebrew date without any network. Must not touch Android or the clock.
 *
 * Months use the book's numbering: Nisan = 1 ... Tishri = 7 ... Adar = 12, Adar II = 13 (leap years only).
 * The Hebrew year starts at Tishri, so within one year month 7 comes first.
 */
data class HebrewDate(val year: Int, val month: Int, val day: Int) {
    val isLeapYear: Boolean get() = HebrewCalendar.isLeap(year)

    /** Name of the month: in a leap year month 12 is "Adar I" and month 13 "Adar II". */
    fun monthName(hebrew: Boolean): String = HebrewCalendar.monthName(year, month, hebrew)

    fun toGregorian(): LocalDate = HebrewCalendar.toGregorian(this)
}

object HebrewCalendar {
    const val NISAN = 1
    const val IYAR = 2
    const val SIVAN = 3
    const val TAMMUZ = 4
    const val AV = 5
    const val ELUL = 6
    const val TISHRI = 7
    const val CHESHVAN = 8
    const val KISLEV = 9
    const val TEVET = 10
    const val SHVAT = 11
    const val ADAR = 12
    const val ADAR_II = 13

    private const val HEBREW_EPOCH = -1373427L // fixed day number of 1 Tishri, year 1
    private const val GREGORIAN_EPOCH_OFFSET = 719163L // fixed day of 1970-01-01

    fun isLeap(year: Int): Boolean = Math.floorMod(7 * year + 1, 19) < 7

    fun lastMonthOfYear(year: Int): Int = if (isLeap(year)) ADAR_II else ADAR

    private fun elapsedDays(year: Int): Long {
        val monthsElapsed = Math.floorDiv(235L * year - 234, 19L)
        val partsElapsed = 12084L + 13753L * monthsElapsed
        val days = 29L * monthsElapsed + partsElapsed / 25920L
        return if (Math.floorMod(3 * (days + 1), 7L) < 3) days + 1 else days
    }

    private fun yearLengthCorrection(year: Int): Long {
        val ny0 = elapsedDays(year - 1)
        val ny1 = elapsedDays(year)
        val ny2 = elapsedDays(year + 1)
        return when {
            ny2 - ny1 == 356L -> 2L
            ny1 - ny0 == 382L -> 1L
            else -> 0L
        }
    }

    private fun newYear(year: Int): Long = HEBREW_EPOCH + elapsedDays(year) + yearLengthCorrection(year)

    fun daysInYear(year: Int): Int = (newYear(year + 1) - newYear(year)).toInt()

    fun daysInMonth(year: Int, month: Int): Int {
        val len = daysInYear(year)
        return when {
            month == IYAR || month == TAMMUZ || month == ELUL || month == TEVET || month == ADAR_II -> 29
            month == ADAR && !isLeap(year) -> 29
            month == CHESHVAN && len !in intArrayOf(355, 385) -> 29
            month == KISLEV && len in intArrayOf(353, 383) -> 29
            else -> 30
        }
    }

    private fun fixedFrom(year: Int, month: Int, day: Int): Long {
        var total = newYear(year) + day - 1
        if (month < TISHRI) {
            for (m in TISHRI..lastMonthOfYear(year)) total += daysInMonth(year, m)
            for (m in NISAN until month) total += daysInMonth(year, m)
        } else {
            for (m in TISHRI until month) total += daysInMonth(year, m)
        }
        return total
    }

    fun fromGregorian(date: LocalDate): HebrewDate {
        val fixed = date.toEpochDay() + GREGORIAN_EPOCH_OFFSET
        var year = ((fixed - HEBREW_EPOCH) * 98496L / 35975351L).toInt() // never above the real year
        while (newYear(year + 1) <= fixed) year++
        var month = if (fixed < fixedFrom(year, NISAN, 1)) TISHRI else NISAN
        while (fixed > fixedFrom(year, month, daysInMonth(year, month))) month++
        return HebrewDate(year, month, (fixed - fixedFrom(year, month, 1) + 1).toInt())
    }

    fun toGregorian(date: HebrewDate): LocalDate =
        LocalDate.ofEpochDay(fixedFrom(date.year, date.month, date.day) - GREGORIAN_EPOCH_OFFSET)

    fun dayOfWeek(date: HebrewDate): DayOfWeek = toGregorian(date).dayOfWeek

    private val HE_MONTHS = arrayOf("", "ניסן", "אייר", "סיוון", "תמוז", "אב", "אלול", "תשרי", "חשוון", "כסלו", "טבת", "שבט", "אדר", "אדר ב׳")
    private val EN_MONTHS = arrayOf("", "Nisan", "Iyar", "Sivan", "Tammuz", "Av", "Elul", "Tishri", "Cheshvan", "Kislev", "Tevet", "Shevat", "Adar", "Adar II")

    fun monthName(year: Int, month: Int, hebrew: Boolean): String {
        require(month in 1..13) { "month $month" }
        if (month == ADAR && isLeap(year)) return if (hebrew) "אדר א׳" else "Adar I"
        return if (hebrew) HE_MONTHS[month] else EN_MONTHS[month]
    }
}
