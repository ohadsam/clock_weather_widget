package com.clockweather.core.hebrew

import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class HebrewCalendarTest {
    private fun h(y: Int, m: Int, d: Int) = HebrewDate(y, m, d)

    @Test fun roshHashanaDatesMatchPublishedCalendars() {
        assertEquals(h(5784, 7, 1), HebrewCalendar.fromGregorian(LocalDate.of(2023, 9, 16)))
        assertEquals(h(5785, 7, 1), HebrewCalendar.fromGregorian(LocalDate.of(2024, 10, 3)))
        assertEquals(h(5786, 7, 1), HebrewCalendar.fromGregorian(LocalDate.of(2025, 9, 23)))
        assertEquals(h(5787, 7, 1), HebrewCalendar.fromGregorian(LocalDate.of(2026, 9, 12)))
    }

    @Test fun knownFestivalDates() {
        assertEquals(h(5784, 1, 15), HebrewCalendar.fromGregorian(LocalDate.of(2024, 4, 23))) // Pesach 2024
        assertEquals(h(5785, 12, 14), HebrewCalendar.fromGregorian(LocalDate.of(2025, 3, 14))) // Purim 2025
        assertEquals(h(5785, 3, 6), HebrewCalendar.fromGregorian(LocalDate.of(2025, 6, 2))) // Shavuot 2025
        assertEquals(h(5786, 7, 10), HebrewCalendar.fromGregorian(LocalDate.of(2025, 10, 2))) // Yom Kippur 2025
        assertEquals(h(5785, 9, 25), HebrewCalendar.fromGregorian(LocalDate.of(2024, 12, 26))) // Hanukkah 2024 first day
    }

    @Test fun leapYearPattern() {
        val leap = listOf(5784, 5787, 5790).all { HebrewCalendar.isLeap(it) }
        val plain = listOf(5785, 5786, 5788, 5789).none { HebrewCalendar.isLeap(it) }
        assertTrue(leap && plain)
        assertEquals(13, HebrewCalendar.lastMonthOfYear(5784))
    }

    @Test fun roundTripEveryDayOverThirtyYears() {
        var day = LocalDate.of(2000, 1, 1)
        val end = LocalDate.of(2030, 12, 31)
        var previous: HebrewDate? = null
        while (day <= end) {
            val hd = HebrewCalendar.fromGregorian(day)
            assertEquals(day, HebrewCalendar.toGregorian(hd), "round trip $day")
            assertTrue(hd.day in 1..HebrewCalendar.daysInMonth(hd.year, hd.month), "valid day $hd for $day")
            previous?.let { p -> assertTrue(hd.day == p.day + 1 || hd.day == 1, "continuous at $day") }
            previous = hd
            day = day.plusDays(1)
        }
    }

    @Test fun yearLengthsAreLegal() {
        for (y in 5700..5900) assertTrue(HebrewCalendar.daysInYear(y) in setOf(353, 354, 355, 383, 384, 385), "year $y")
    }

    @Test fun nineteenNovember2026IsNineKislev5787() {
        assertEquals(h(5787, 9, 9), HebrewCalendar.fromGregorian(LocalDate.of(2026, 11, 19)))
        assertEquals(385, HebrewCalendar.daysInYear(5787)) // 1 Tishri 5788 is 2 Oct 2027
    }

    @Test fun gematriaAndFormatting() {
        assertEquals("י״ט", Gematria.format(19))
        assertEquals("ג׳", Gematria.format(3))
        assertEquals("ט״ו", Gematria.format(15))
        assertEquals("ט״ז", Gematria.format(16))
        assertEquals("ל׳", Gematria.format(30))
        assertEquals("תשפ״ז", Gematria.year(5787))
        assertEquals("י׳ בכסלו תשפ״ז", HebrewDateFormat.full(h(5787, 9, 10), true))
        assertEquals("10 Kislev 5787", HebrewDateFormat.full(h(5787, 9, 10), false))
        assertEquals("אדר א׳", HebrewCalendar.monthName(5784, 12, true))
        assertEquals("אדר", HebrewCalendar.monthName(5785, 12, true))
    }

    @Test fun hebrewDayRollsAtSunsetOnlyWhenAsked() {
        val day = LocalDate.of(2026, 11, 19)
        val sunset = java.time.LocalTime.of(16, 40)
        val evening = java.time.LocalTime.of(17, 5)
        assertEquals(9, HebrewDateFormat.hebrewDateAt(day, evening, sunset, false).day)
        assertEquals(10, HebrewDateFormat.hebrewDateAt(day, evening, sunset, true).day)
        assertEquals(9, HebrewDateFormat.hebrewDateAt(day, java.time.LocalTime.of(12, 0), sunset, true).day)
        assertEquals(9, HebrewDateFormat.hebrewDateAt(day, evening, null, true).day)
    }
}

class HolidaysTest {
    private fun ids(d: LocalDate, israel: Boolean = true) = Holidays.on(d, israel).map { it.id }

    @Test fun majorHolidays2025And2026() {
        assertTrue("purim" in ids(LocalDate.of(2025, 3, 14)))
        assertTrue("yom_kippur" in ids(LocalDate.of(2025, 10, 2)))
        assertTrue("pesach" in ids(LocalDate.of(2026, 4, 2)))
        assertTrue("rosh_hashana" in ids(LocalDate.of(2026, 9, 12)))
        assertTrue("shavuot" in ids(LocalDate.of(2026, 5, 22)))
    }

    @Test fun diasporaGetsSecondDays() {
        assertNull(Holidays.on(LocalDate.of(2025, 10, 8), true).firstOrNull { it.id == "sukkot" && it.dayOfHoliday == 2 })
        assertTrue(Holidays.on(LocalDate.of(2025, 10, 8), false).any { it.id == "sukkot" && it.dayOfHoliday == 2 })
    }

    @Test fun hanukkahHasEightConsecutiveDays() {
        val first = LocalDate.of(2024, 12, 26)
        for (i in 0..7) assertEquals(i + 1, Holidays.on(first.plusDays(i.toLong())).first { it.id == "hanukkah" }.dayOfHoliday, "day ${i + 1}")
        assertTrue(Holidays.on(first.plusDays(8)).none { it.id == "hanukkah" })
        assertTrue(Holidays.on(first.minusDays(1)).none { it.id == "hanukkah" })
    }

    @Test fun hanukkahEveryYearIsEightDays() {
        for (year in 2020..2035) {
            var day = LocalDate.of(year, 11, 20)
            var count = 0
            while (day < LocalDate.of(year + 1, 1, 15)) { if (Holidays.on(day).any { it.id == "hanukkah" }) count++; day = day.plusDays(1) }
            assertEquals(8, count, "Hanukkah $year")
        }
    }

    @Test fun shabbatAndRoshChodesh() {
        assertTrue("shabbat" in ids(LocalDate.of(2026, 11, 21)))
        assertEquals("Rosh Chodesh Kislev", Holidays.on(LocalDate.of(2026, 11, 10)).first { it.id == "rosh_chodesh" }.en) // 30 Cheshvan = day 1
        assertEquals("Rosh Chodesh Kislev", Holidays.on(LocalDate.of(2026, 11, 11)).first { it.id == "rosh_chodesh" }.en) // 1 Kislev = day 2
        assertTrue("rosh_chodesh" !in ids(LocalDate.of(2026, 11, 12)))
    }

    @Test fun fastsNeverFallOnShabbat() {
        for (year in 2020..2040) {
            var day = LocalDate.of(year, 1, 1)
            while (day.year == year) {
                if (day.dayOfWeek == java.time.DayOfWeek.SATURDAY) {
                    val fasts = Holidays.on(day).filter { it.kind == HolidayKind.FAST && it.id != "yom_kippur" }
                    assertTrue(fasts.isEmpty(), "fast on Shabbat $day")
                }
                day = day.plusDays(1)
            }
        }
    }

    @Test fun independenceDayNeverOnFridayOrShabbat() {
        for (year in 2020..2045) {
            var day = LocalDate.of(year, 4, 1)
            while (day.monthValue < 6) {
                if (Holidays.on(day).any { it.id == "yom_haatzmaut" }) {
                    assertTrue(day.dayOfWeek != java.time.DayOfWeek.FRIDAY && day.dayOfWeek != java.time.DayOfWeek.SATURDAY, "Yom HaAtzmaut $day")
                }
                day = day.plusDays(1)
            }
        }
    }
}
