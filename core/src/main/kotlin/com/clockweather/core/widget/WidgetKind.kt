package com.clockweather.core.widget

/**
 * The different widgets offered in the launcher's widget picker. Each kind forces some "show" options on or off on top of
 * the user's settings, so one settings screen drives them all (per-widget settings come later, see PLAN batch 6).
 */
enum class WidgetKind(val id: String, val he: String, val en: String) {
    /** The full dashboard: clock + date + weather + alarm + events + clocks + footer, as much as fits. */
    CLASSIC("classic", "לוח מלא", "Full dashboard"),
    /** Only the clock (any style) with the date line. */
    CLOCK("clock", "שעון בלבד", "Clock only"),
    /** Weather first: big weather panel, date, sunrise/sunset. */
    WEATHER("weather", "מזג אוויר", "Weather"),
    /** Today's events first (a list when tall), date and next alarm. */
    AGENDA("agenda", "יומן ושעון מעורר", "Agenda and alarm"),
    /** One wide thin row: time, date and temperature. */
    STRIP("strip", "פס צר", "Slim strip");

    fun shape(s: WidgetSettings): WidgetSettings = when (this) {
        CLASSIC -> s
        CLOCK -> s.copy(showClock = true, showWeather = false, showAgenda = false, showWorldClocks = false, showAlarm = false, showSteps = false, showSunTimes = false)
        WEATHER -> s.copy(showClock = false, showWeather = true, showAgenda = false, showWorldClocks = false, showAlarm = false)
        AGENDA -> s.copy(showClock = false, showWeather = false, showAgenda = true, showWorldClocks = false, showSunTimes = false, showSteps = false)
        STRIP -> s
    }
}

/** One-tap looks. They change only the look, never places, clocks, alarm app or calendars. */
enum class Preset(val id: String, val he: String, val en: String) {
    CLASSIC_HTC("classic_htc", "קלאסי", "Classic"),
    FROSTED("frosted", "זכוכית חלבית", "Frosted glass"),
    NIGHT_STAND("night_stand", "שולחן ליל", "Night stand"),
    MINIMAL("minimal", "מינימלי", "Minimal"),
    SUNSET_ANALOG("sunset_analog", "שקיעה אנלוגי", "Sunset analog"),
    SHABBAT_CALM("shabbat_calm", "שבת רגועה", "Shabbat calm"),
    POCKET_GOLD("pocket_gold", "שעון כיס זהב", "Gold pocket watch");

    fun apply(s: WidgetSettings): WidgetSettings = when (this) {
        CLASSIC_HTC -> s.copy(clockStyle = ClockStyle.FLIP, paletteId = "classic_white", backgroundAlphaPct = 0, panelAlphaPct = 75, cornerRadiusDp = 18, scalePct = 100, calmAnimations = false)
        FROSTED -> s.copy(clockStyle = ClockStyle.DIGITAL, paletteId = "ocean", backgroundAlphaPct = 35, panelAlphaPct = 45, cornerRadiusDp = 28, scalePct = 100, calmAnimations = false)
        NIGHT_STAND -> s.copy(clockStyle = ClockStyle.FLIP, paletteId = "midnight", backgroundAlphaPct = 80, panelAlphaPct = 90, cornerRadiusDp = 24, scalePct = 110, calmAnimations = false)
        MINIMAL -> s.copy(clockStyle = ClockStyle.MINIMAL, paletteId = "graphite", backgroundAlphaPct = 0, panelAlphaPct = 0, cornerRadiusDp = 12, scalePct = 90, showWorldClocks = false, showSunTimes = false, showAgenda = false)
        SUNSET_ANALOG -> s.copy(clockStyle = ClockStyle.ANALOG, paletteId = "sunset", backgroundAlphaPct = 15, panelAlphaPct = 60, cornerRadiusDp = 30, scalePct = 100, calmAnimations = false)
        POCKET_GOLD -> s.copy(clockStyle = ClockStyle.POCKET, paletteId = "sand", backgroundAlphaPct = 0, panelAlphaPct = 70, cornerRadiusDp = 20, scalePct = 100, calmAnimations = false)
        SHABBAT_CALM -> s.copy(clockStyle = ClockStyle.DIGITAL, paletteId = "sand", backgroundAlphaPct = 20, panelAlphaPct = 70, cornerRadiusDp = 20, scalePct = 100, calmAnimations = true, showHolidays = true, showHebrewDate = true, showSunTimes = true)
    }
}
