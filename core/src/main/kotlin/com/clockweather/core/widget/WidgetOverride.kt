package com.clockweather.core.widget

import kotlinx.serialization.Serializable

/**
 * What ONE placed widget changes compared with the global settings. A null field means "use the global value", so a widget the owner
 * never customised follows every later change of the global settings. Only appearance and "what to show" can differ per widget.
 */
@Serializable
data class WidgetOverride(
    val clockStyle: ClockStyle? = null,
    val paletteId: String? = null,
    val pocketMetal: String? = null,
    val backgroundAlphaPct: Int? = null,
    val panelAlphaPct: Int? = null,
    val scalePct: Int? = null,
    val use24h: Boolean? = null,
    val showClock: Boolean? = null,
    val showWeather: Boolean? = null,
    val showGregorianDate: Boolean? = null,
    val showHebrewDate: Boolean? = null,
    val showHolidays: Boolean? = null,
    val showAlarm: Boolean? = null,
    val showAgenda: Boolean? = null,
    val showWorldClocks: Boolean? = null,
    val showSunTimes: Boolean? = null,
    val showTimers: Boolean? = null,
    val showSettingsButton: Boolean? = null,
) {
    val isEmpty: Boolean get() = this == WidgetOverride()

    fun applyTo(s: WidgetSettings): WidgetSettings = s.copy(
        clockStyle = clockStyle ?: s.clockStyle,
        paletteId = paletteId?.takeIf { Palettes.byId(it) != null } ?: s.paletteId,
        pocketMetal = pocketMetal ?: s.pocketMetal,
        backgroundAlphaPct = (backgroundAlphaPct ?: s.backgroundAlphaPct).coerceIn(0, 100),
        panelAlphaPct = (panelAlphaPct ?: s.panelAlphaPct).coerceIn(0, 100),
        scalePct = (scalePct ?: s.scalePct).coerceIn(50, 150),
        use24h = use24h ?: s.use24h,
        showClock = showClock ?: s.showClock,
        showWeather = showWeather ?: s.showWeather,
        showGregorianDate = showGregorianDate ?: s.showGregorianDate,
        showHebrewDate = showHebrewDate ?: s.showHebrewDate,
        showHolidays = showHolidays ?: s.showHolidays,
        showAlarm = showAlarm ?: s.showAlarm,
        showAgenda = showAgenda ?: s.showAgenda,
        showWorldClocks = showWorldClocks ?: s.showWorldClocks,
        showSunTimes = showSunTimes ?: s.showSunTimes,
        showTimers = showTimers ?: s.showTimers,
        showSettingsButton = showSettingsButton ?: s.showSettingsButton,
    )
}
