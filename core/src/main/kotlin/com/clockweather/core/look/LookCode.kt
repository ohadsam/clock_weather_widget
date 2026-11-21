package com.clockweather.core.look

import com.clockweather.core.widget.ClockStyle
import com.clockweather.core.widget.WidgetSettings
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.util.Base64

/**
 * Share-my-look code and full backup. A look is only appearance and "what to show" - never places, calendars, packages or any personal data.
 * Both are plain text (`CW1:...` / `CWB1:...`) that can be sent in any chat and pasted back. Decoding never throws.
 */
object LookCode {
    @Serializable
    private data class Look(
        val clockStyle: ClockStyle, val paletteId: String, val pocketMetal: String,
        val backgroundAlphaPct: Int, val panelAlphaPct: Int, val cornerRadiusDp: Int, val scalePct: Int,
        val use24h: Boolean, val showAmPm: Boolean,
        val showClock: Boolean, val showWeather: Boolean, val showHiLo: Boolean, val showOutlook: Boolean,
        val showGregorianDate: Boolean, val showHebrewDate: Boolean, val showHolidays: Boolean, val showAlarm: Boolean,
        val showAgenda: Boolean, val showWorldClocks: Boolean, val showSunTimes: Boolean, val showNightfall: Boolean, val showTimers: Boolean,
        val calmAnimations: Boolean, val animateWeather: Boolean, val autoContent: Boolean,
        val seasonalTheme: Boolean, val autoDayNight: Boolean, val nightPaletteId: String,
    )

    private const val LOOK = "CW1:"
    private const val BACKUP = "CWB1:"
    private val json = Json { ignoreUnknownKeys = true; coerceInputValues = true; encodeDefaults = true }
    private fun b64(s: String) = Base64.getUrlEncoder().withoutPadding().encodeToString(s.toByteArray(Charsets.UTF_8))
    private fun unb64(s: String) = String(Base64.getUrlDecoder().decode(s.trim()), Charsets.UTF_8)

    fun encodeLook(s: WidgetSettings): String = LOOK + b64(json.encodeToString(Look.serializer(), s.run {
        Look(clockStyle, paletteId, pocketMetal, backgroundAlphaPct, panelAlphaPct, cornerRadiusDp, scalePct, use24h, showAmPm, showClock, showWeather, showHiLo, showOutlook,
            showGregorianDate, showHebrewDate, showHolidays, showAlarm, showAgenda, showWorldClocks, showSunTimes, showNightfall, showTimers, calmAnimations, animateWeather, autoContent,
            seasonalTheme, autoDayNight, nightPaletteId)
    }))

    /** Applies a pasted look code on top of [base]; null when the text is not a valid code. Everything personal in [base] is kept. */
    fun applyLook(base: WidgetSettings, code: String): WidgetSettings? {
        val t = code.trim()
        if (!t.startsWith(LOOK) || t.length > 4000) return null
        val l = runCatching { json.decodeFromString(Look.serializer(), unb64(t.removePrefix(LOOK))) }.getOrNull() ?: return null
        return base.copy(
            clockStyle = l.clockStyle, paletteId = l.paletteId, pocketMetal = l.pocketMetal, backgroundAlphaPct = l.backgroundAlphaPct, panelAlphaPct = l.panelAlphaPct,
            cornerRadiusDp = l.cornerRadiusDp, scalePct = l.scalePct, use24h = l.use24h, showAmPm = l.showAmPm, showClock = l.showClock, showWeather = l.showWeather,
            showHiLo = l.showHiLo, showOutlook = l.showOutlook, showGregorianDate = l.showGregorianDate, showHebrewDate = l.showHebrewDate, showHolidays = l.showHolidays,
            showAlarm = l.showAlarm, showAgenda = l.showAgenda, showWorldClocks = l.showWorldClocks, showSunTimes = l.showSunTimes, showNightfall = l.showNightfall,
            showTimers = l.showTimers, calmAnimations = l.calmAnimations, animateWeather = l.animateWeather, autoContent = l.autoContent,
            seasonalTheme = l.seasonalTheme, autoDayNight = l.autoDayNight, nightPaletteId = l.nightPaletteId,
        ).sanitized()
    }

    fun encodeBackup(s: WidgetSettings): String = BACKUP + b64(WidgetSettings.encode(s))

    fun decodeBackup(text: String): WidgetSettings? {
        val t = text.trim()
        if (!t.startsWith(BACKUP) || t.length > 100_000) return null
        val raw = runCatching { unb64(t.removePrefix(BACKUP)) }.getOrNull() ?: return null
        return runCatching { WidgetSettings.decode(raw) }.getOrNull()
    }
}
