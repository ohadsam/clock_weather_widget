package com.clockweather.core.hebrew

/**
 * Which of the 13 approved badge drawables (`hol_<key>`) illustrates a holiday id from [Holidays]. Days without a fitting
 * celebratory picture (memorial days, Yom HaShoah, Tu B'Av, Yom Yerushalayim) deliberately have none: the widget shows text only.
 */
object HolidayBadge {
    val ART_KEYS = setOf(
        "shabbat", "rosh_chodesh", "rosh_hashana", "yom_kippur", "sukkot", "hanukkah", "tu_bishvat",
        "purim", "pesach", "yom_haatzmaut", "lag_baomer", "shavuot", "fast_day",
    )

    /** Ids that intentionally have no badge. A new holiday id must be added either to [drawableKey] or here (a test enforces it). */
    val NO_BADGE = setOf("yom_hashoah", "yom_hazikaron", "yom_yerushalayim", "tu_bav")

    fun drawableKey(holidayId: String): String? = when (holidayId) {
        "shabbat" -> "shabbat"
        "rosh_chodesh" -> "rosh_chodesh"
        "rosh_hashana" -> "rosh_hashana"
        "yom_kippur" -> "yom_kippur"
        "sukkot", "chol_hamoed_sukkot", "hoshana_raba", "shmini_atzeret", "simchat_torah" -> "sukkot"
        "hanukkah" -> "hanukkah"
        "tu_bishvat" -> "tu_bishvat"
        "purim", "purim_katan", "shushan_purim", "shushan_purim_katan" -> "purim"
        "pesach", "chol_hamoed_pesach", "pesach_vii", "pesach_viii", "erev_pesach" -> "pesach"
        "yom_haatzmaut" -> "yom_haatzmaut"
        "lag_baomer" -> "lag_baomer"
        "shavuot" -> "shavuot"
        "tzom_gedalia", "asara_btevet", "taanit_esther", "shiva_asar_btammuz", "tisha_bav" -> "fast_day"
        else -> null
    }
}
