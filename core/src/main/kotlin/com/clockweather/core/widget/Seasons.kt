package com.clockweather.core.widget

import com.clockweather.core.hebrew.Holidays
import java.time.LocalDate

/** Festive looks that switch on by themselves on the holiday itself (opt-in: [WidgetSettings.seasonalTheme]). */
enum class Season(val paletteId: String, val he: String, val en: String, val ornament: String) {
    HANUKKAH("s_hanukkah", "חנוכה", "Hanukkah", "hanukkah"), PESACH("s_pesach", "פסח", "Passover", "pesach"), ROSH_HASHANA("s_rosh_hashana", "ראש השנה", "Rosh Hashana", "rosh_hashana"),
    SUKKOT("s_sukkot", "סוכות", "Sukkot", "sukkot"), PURIM("s_purim", "פורים", "Purim", "purim"), TU_BISHVAT("s_tu_bishvat", "ט״ו בשבט", "Tu BiShvat", "tu_bishvat"),
    SHAVUOT("s_shavuot", "שבועות", "Shavuot", "shavuot"), INDEPENDENCE("s_independence", "יום העצמאות", "Independence Day", "independence"), LAG_BAOMER("s_lag_baomer", "ל״ג בעומר", "Lag BaOmer", "lag_baomer");

    companion object {
        /** A holiday starts at sunset: in the evening after [sunset] the next civil day is the one that counts. */
        fun effectiveDay(date: LocalDate, time: java.time.LocalTime, sunset: java.time.LocalTime?): LocalDate =
            if (sunset != null && time >= sunset) date.plusDays(1) else date

        /** The art for [day]: the ornament of the season, and on Hanukkah the picture with that day's candles (`hanukkah_1` .. `hanukkah_8`). */
        fun ornamentKey(day: LocalDate, inIsrael: Boolean): String? {
            val season = of(day, inIsrael) ?: return null
            if (season != HANUKKAH) return season.ornament
            val n = Holidays.on(day, inIsrael).firstOrNull { it.id == "hanukkah" }?.dayOfHoliday
            return if (n != null) "hanukkah_$n" else season.ornament
        }

        fun of(day: LocalDate, inIsrael: Boolean): Season? = Holidays.on(day, inIsrael).firstNotNullOfOrNull { h ->
            when (h.id) {
                "hanukkah" -> HANUKKAH
                "pesach", "chol_hamoed_pesach", "erev_pesach", "pesach_vii", "pesach_viii" -> PESACH
                "rosh_hashana" -> ROSH_HASHANA
                "sukkot", "chol_hamoed_sukkot", "hoshana_raba", "shmini_atzeret", "simchat_torah" -> SUKKOT
                "purim", "shushan_purim" -> PURIM
                "tu_bishvat" -> TU_BISHVAT
                "shavuot" -> SHAVUOT
                "yom_haatzmaut" -> INDEPENDENCE
                "lag_baomer" -> LAG_BAOMER
                else -> null
            }
        }
    }
}

/** Which palette the widget really uses: night mode first, then a holiday look, then the owner's choice. Pure. */
object Appearance {
    fun paletteId(s: WidgetSettings, day: LocalDate, isNight: Boolean): String = when {
        s.autoDayNight && isNight && Palettes.byId(s.nightPaletteId) != null -> s.nightPaletteId
        s.seasonalTheme -> Season.of(day, s.inIsrael)?.paletteId ?: s.paletteId
        else -> s.paletteId
    }

    /** Night = before sunrise or from sunset on; without sun times, 20:00-06:00. */
    fun isNight(hour: Int, minute: Int, sunriseMin: Int?, sunsetMin: Int?): Boolean {
        val t = hour * 60 + minute
        return if (sunriseMin != null && sunsetMin != null) t < sunriseMin || t >= sunsetMin else hour >= 20 || hour < 6
    }
}
