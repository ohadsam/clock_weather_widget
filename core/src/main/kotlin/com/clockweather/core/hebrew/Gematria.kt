package com.clockweather.core.hebrew

/** Hebrew numerals (gematria) for days of the month and years, with geresh / gershayim. Pure text helper. */
object Gematria {
    private val ONES = arrayOf("", "א", "ב", "ג", "ד", "ה", "ו", "ז", "ח", "ט")
    private val TENS = arrayOf("", "י", "כ", "ל", "מ", "נ", "ס", "ע", "פ", "צ")
    private val HUNDREDS = arrayOf("", "ק", "ר", "ש", "ת")

    /** 1..999 as letters without punctuation (15 and 16 are written ט״ו / ט״ז). */
    private fun letters(n: Int): String {
        require(n in 1..999) { "gematria $n" }
        var rest = n
        val sb = StringBuilder()
        while (rest >= 400) { sb.append("ת"); rest -= 400 }
        if (rest >= 100) { sb.append(HUNDREDS[rest / 100]); rest %= 100 }
        when (rest) {
            15 -> sb.append("טו")
            16 -> sb.append("טז")
            else -> { sb.append(TENS[rest / 10]); sb.append(ONES[rest % 10]) }
        }
        return sb.toString()
    }

    /** `י״ט`, `ג׳`, `ל׳`: a lone letter gets a geresh, otherwise a gershayim before the last letter. */
    fun format(n: Int): String {
        val s = letters(n)
        return if (s.length == 1) "$s׳" else s.dropLast(1) + "״" + s.last()
    }

    /** Year without the thousands: 5787 -> תשפ״ז. */
    fun year(year: Int): String = format(year % 1000)
}
