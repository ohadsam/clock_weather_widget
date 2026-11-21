package com.clockweather.core.widget

import com.clockweather.core.hebrew.Holidays
import java.time.LocalDate

/** Festive looks that switch on by themselves on the holiday itself (opt-in: [WidgetSettings.seasonalTheme]). */
enum class Season(val paletteId: String, val he: String, val en: String) {
    HANUKKAH("s_hanukkah", "חנוכה", "Hanukkah"), PESACH("s_pesach", "פסח", "Passover"), ROSH_HASHANA("s_rosh_hashana", "ראש השנה", "Rosh Hashana"),
    SUKKOT("s_sukkot", "סוכות", "Sukkot"), PURIM("s_purim", "פורים", "Purim"), TU_BISHVAT("s_tu_bishvat", "ט״ו בשבט", "Tu BiShvat"),
    SHAVUOT("s_shavuot", "שבועות", "Shavuot"), INDEPENDENCE("s_independence", "יום העצמאות", "Independence Day"), LAG_BAOMER("s_lag_baomer", "ל״ג בעומר", "Lag BaOmer");

    companion object {
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
