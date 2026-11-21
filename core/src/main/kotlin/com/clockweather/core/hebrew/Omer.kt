package com.clockweather.core.hebrew

/** The Omer count (1..49) for a Hebrew date: 16 Nisan is day 1, 5 Sivan is day 49. Pure. */
object Omer {
    fun day(h: HebrewDate): Int? {
        val start = HebrewDate(h.year, HebrewCalendar.NISAN, 16).toGregorian().toEpochDay()
        val n = h.toGregorian().toEpochDay() - start + 1
        return if (n in 1..49) n.toInt() else null
    }

    fun text(n: Int, hebrew: Boolean): String {
        if (!hebrew) return "Omer: day $n"
        val weeks = n / 7; val days = n % 7
        val tail = when {
            weeks == 0 -> ""
            days == 0 -> " (${if (weeks == 1) "שבוע אחד" else "$weeks שבועות"})"
            else -> " (${if (weeks == 1) "שבוע אחד" else "$weeks שבועות"} ו${if (days == 1) "יום אחד" else "$days ימים"})"
        }
        return "העומר: ${Gematria.format(n)}$tail"
    }
}
