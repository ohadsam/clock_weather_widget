package com.clockweather.core.hebrew

import java.time.DayOfWeek
import java.time.LocalDate

/**
 * Jewish holidays and special days for one Gregorian day, computed offline from the Hebrew date.
 * Rules follow the common Israeli calendar (as Hebcal): the owner still has to confirm them (REQUESTS R-DOM-1).
 * Pure: no clock, no Android.
 */
enum class HolidayKind { MAJOR, MINOR, FAST, MODERN, ROSH_CHODESH, SHABBAT }

data class Holiday(
    val id: String,
    val kind: HolidayKind,
    val he: String,
    val en: String,
    /** 1-based day inside a multi-day holiday (Hanukkah day 3), else null. */
    val dayOfHoliday: Int? = null,
    /** Day off work / school in Israel. */
    val dayOff: Boolean = false,
) {
    fun name(hebrew: Boolean): String {
        val base = if (hebrew) he else en
        return if (dayOfHoliday != null && id == "hanukkah") (if (hebrew) "$base – נר ${Gematria.format(dayOfHoliday)}" else "$base – day $dayOfHoliday") else base
    }
}

object Holidays {
    /** Everything special on [day]; [inIsrael] false gives the diaspora second days. Ordered most important first. */
    fun on(day: LocalDate, inIsrael: Boolean = true): List<Holiday> {
        val h = HebrewCalendar.fromGregorian(day)
        val dow = day.dayOfWeek
        val out = ArrayList<Holiday>()
        val m = h.month
        val d = h.day

        val sink = Sink(out)
        fun add(id: String, kind: HolidayKind, he: String, en: String, n: Int? = null, off: Boolean = false) = sink.add(id, kind, he, en, n, off)

        when (m) {
            HebrewCalendar.TISHRI -> when {
                d == 1 || d == 2 -> add("rosh_hashana", HolidayKind.MAJOR, "ראש השנה", "Rosh Hashana", d, true)
                d == 3 && dow != DayOfWeek.SATURDAY || d == 4 && dow == DayOfWeek.SUNDAY ->
                    add("tzom_gedalia", HolidayKind.FAST, "צום גדליה", "Fast of Gedaliah")
                d == 10 -> add("yom_kippur", HolidayKind.MAJOR, "יום כיפור", "Yom Kippur", null, true)
                d == 15 -> add("sukkot", HolidayKind.MAJOR, "סוכות", "Sukkot", 1, true)
                d == 16 && !inIsrael -> add("sukkot", HolidayKind.MAJOR, "סוכות – יום ב׳", "Sukkot – day 2", 2, true)
                d in 16..20 -> add("chol_hamoed_sukkot", HolidayKind.MINOR, "חול המועד סוכות", "Chol HaMoed Sukkot")
                d == 21 -> add("hoshana_raba", HolidayKind.MINOR, "הושענא רבה", "Hoshana Raba")
                d == 22 -> add("shmini_atzeret", HolidayKind.MAJOR, if (inIsrael) "שמיני עצרת – שמחת תורה" else "שמיני עצרת", if (inIsrael) "Shemini Atzeret – Simchat Torah" else "Shemini Atzeret", null, true)
                d == 23 && !inIsrael -> add("simchat_torah", HolidayKind.MAJOR, "שמחת תורה", "Simchat Torah", null, true)
            }
            HebrewCalendar.KISLEV, HebrewCalendar.TEVET -> {
                val hanukkahDay = hanukkahDay(h)
                if (hanukkahDay != null) add("hanukkah", HolidayKind.MAJOR, "חנוכה", "Hanukkah", hanukkahDay)
                if (m == HebrewCalendar.TEVET && d == 10) add("asara_btevet", HolidayKind.FAST, "עשרה בטבת", "Fast of 10 Tevet")
            }
            HebrewCalendar.SHVAT -> if (d == 15) add("tu_bishvat", HolidayKind.MINOR, "ט״ו בשבט", "Tu BiShvat")
            HebrewCalendar.ADAR, HebrewCalendar.ADAR_II -> addAdar(h, sink)
            HebrewCalendar.NISAN -> addNisan(h, inIsrael, sink)
            HebrewCalendar.IYAR -> addIyar(h, sink)
            HebrewCalendar.SIVAN -> when {
                d == 6 -> add("shavuot", HolidayKind.MAJOR, "שבועות", "Shavuot", 1, true)
                d == 7 && !inIsrael -> add("shavuot", HolidayKind.MAJOR, "שבועות – יום ב׳", "Shavuot – day 2", 2, true)
            }
            HebrewCalendar.TAMMUZ -> {
                val fastDay = if (dayOfWeekOf(h.year, HebrewCalendar.TAMMUZ, 17) == DayOfWeek.SATURDAY) 18 else 17
                if (d == fastDay) add("shiva_asar_btammuz", HolidayKind.FAST, "י״ז בתמוז", "Fast of 17 Tammuz")
            }
            HebrewCalendar.AV -> {
                val tisha = if (dayOfWeekOf(h.year, HebrewCalendar.AV, 9) == DayOfWeek.SATURDAY) 10 else 9
                if (d == tisha) add("tisha_bav", HolidayKind.FAST, "תשעה באב", "Tisha B'Av")
                if (d == 15) add("tu_bav", HolidayKind.MINOR, "ט״ו באב", "Tu B'Av")
            }
        }

        // Rosh Chodesh: the 30th of a month is day 1 of the next month's, the 1st (not Tishri) is its day 2 or only day.
        val roshChodeshMonth = when {
            d == 30 && m != HebrewCalendar.ELUL -> nextMonthOf(h)
            d == 1 && m != HebrewCalendar.TISHRI -> m
            else -> null
        }
        if (roshChodeshMonth != null) {
            out += Holiday(
                "rosh_chodesh", HolidayKind.ROSH_CHODESH,
                "ראש חודש ${HebrewCalendar.monthName(h.year, roshChodeshMonth, true)}",
                "Rosh Chodesh ${HebrewCalendar.monthName(h.year, roshChodeshMonth, false)}",
                dayOfHoliday = if (d == 30) 1 else null,
            )
        }
        if (dow == DayOfWeek.SATURDAY) out += Holiday("shabbat", HolidayKind.SHABBAT, "שבת", "Shabbat", dayOff = true)
        return out.sortedBy { it.kind.ordinal }
    }

    private fun dayOfWeekOf(year: Int, month: Int, day: Int): DayOfWeek =
        HebrewCalendar.toGregorian(HebrewDate(year, month, day)).dayOfWeek

    private fun nextMonthOf(h: HebrewDate): Int = when (h.month) {
        HebrewCalendar.ELUL -> HebrewCalendar.TISHRI
        HebrewCalendar.ADAR -> if (h.isLeapYear) HebrewCalendar.ADAR_II else HebrewCalendar.NISAN
        HebrewCalendar.ADAR_II -> HebrewCalendar.NISAN
        else -> h.month + 1
    }

    /** 1..8 when [h] is a Hanukkah day (25 Kislev onward, 8 days; the end depends on the length of Kislev). */
    fun hanukkahDay(h: HebrewDate): Int? {
        if (h.month == HebrewCalendar.KISLEV && h.day >= 25) return h.day - 24
        if (h.month == HebrewCalendar.TEVET) {
            val kislevLen = HebrewCalendar.daysInMonth(h.year, HebrewCalendar.KISLEV)
            val n = kislevLen - 24 + h.day
            if (n in 1..8) return n
        }
        return null
    }

    private class Sink(private val out: MutableList<Holiday>) {
        fun add(id: String, kind: HolidayKind, he: String, en: String, n: Int? = null, off: Boolean = false) {
            out += Holiday(id, kind, he, en, n, off)
        }
    }

    private fun addAdar(h: HebrewDate, sink: Sink) {
        val leap = h.isLeapYear
        val purimMonth = if (leap) HebrewCalendar.ADAR_II else HebrewCalendar.ADAR
        if (leap && h.month == HebrewCalendar.ADAR) {
            if (h.day == 14) sink.add("purim_katan", HolidayKind.MINOR, "פורים קטן", "Purim Katan", null, false)
            if (h.day == 15) sink.add("shushan_purim_katan", HolidayKind.MINOR, "שושן פורים קטן", "Shushan Purim Katan", null, false)
            return
        }
        if (h.month != purimMonth) return
        val purimDow = dayOfWeekOf(h.year, purimMonth, 14)
        val esther = if (purimDow == DayOfWeek.SUNDAY) 11 else 13 // 13 Adar falls on Shabbat when Purim is Sunday
        when (h.day) {
            esther -> sink.add("taanit_esther", HolidayKind.FAST, "תענית אסתר", "Fast of Esther", null, false)
            14 -> sink.add("purim", HolidayKind.MAJOR, "פורים", "Purim", null, false)
            15 -> sink.add("shushan_purim", HolidayKind.MINOR, "שושן פורים", "Shushan Purim", null, false)
        }
    }

    private fun addNisan(h: HebrewDate, inIsrael: Boolean, sink: Sink) {
        val d = h.day
        when {
            d == 15 -> sink.add("pesach", HolidayKind.MAJOR, "פסח", "Pesach", 1, true)
            d == 16 && !inIsrael -> sink.add("pesach", HolidayKind.MAJOR, "פסח – יום ב׳", "Pesach – day 2", 2, true)
            d in 16..20 -> sink.add("chol_hamoed_pesach", HolidayKind.MINOR, "חול המועד פסח", "Chol HaMoed Pesach")
            d == 21 -> sink.add("pesach_vii", HolidayKind.MAJOR, "שביעי של פסח", "Seventh day of Pesach", null, true)
            d == 22 && !inIsrael -> sink.add("pesach_viii", HolidayKind.MAJOR, "אחרון של פסח", "Last day of Pesach", null, true)
            d == 14 -> sink.add("erev_pesach", HolidayKind.MINOR, "ערב פסח", "Erev Pesach")
        }
        val shoah = dayOfWeekOf(h.year, HebrewCalendar.NISAN, 27).let {
            when (it) { DayOfWeek.FRIDAY -> 26; DayOfWeek.SUNDAY -> 28; else -> 27 }
        }
        if (d == shoah) sink.add("yom_hashoah", HolidayKind.MODERN, "יום השואה", "Yom HaShoah", null, false)
    }

    private fun addIyar(h: HebrewDate, sink: Sink) {
        val atzmaut = when (dayOfWeekOf(h.year, HebrewCalendar.IYAR, 5)) {
            DayOfWeek.FRIDAY -> 4 // never on Friday or Shabbat
            DayOfWeek.SATURDAY -> 3
            DayOfWeek.MONDAY -> 6
            else -> 5
        }
        val zikaron = atzmaut - 1
        when (h.day) {
            zikaron -> sink.add("yom_hazikaron", HolidayKind.MODERN, "יום הזיכרון", "Yom HaZikaron", null, false)
            atzmaut -> sink.add("yom_haatzmaut", HolidayKind.MODERN, "יום העצמאות", "Yom HaAtzmaut", null, true)
            18 -> sink.add("lag_baomer", HolidayKind.MINOR, "ל״ג בעומר", "Lag BaOmer", null, false)
            28 -> sink.add("yom_yerushalayim", HolidayKind.MODERN, "יום ירושלים", "Yom Yerushalayim", null, false)
        }
    }
}
