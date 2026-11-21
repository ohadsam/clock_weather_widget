package com.clockweather.core.widget

/** The pieces a widget can show, in priority order (the first ones survive when the widget is small). */
enum class Section { CLOCK, WEATHER_PANEL, DATE_LINE, ALARM_CHIP, TIMERS, AGENDA_BANNER, WORLD_CLOCKS, FOOTER, STRIP }

/** [clockHeightDp] is the height of the flexible section [flex] (the clock, or when the clock is hidden the first remaining section). */
data class LayoutPlan(val sections: List<Section>, val clockHeightDp: Int, val widthDp: Int, val heightDp: Int, val flex: Section = Section.CLOCK) {
    operator fun contains(s: Section) = s in sections
}

/**
 * Chooses what fits in a widget of [widthDp] x [heightDp]. The clock always stays; extras are added by priority
 * while their minimum height still fits and the clock keeps at least [MIN_CLOCK_SHARE] of the height.
 * Pure and tested: the same plan drives the home-screen widget and the in-app preview (parity rule).
 */
object WidgetLayout {
    const val MIN_CLOCK_SHARE = 0.42f
    const val MIN_WIDTH_FOR_PANEL = 150
    const val MIN_WIDTH_FOR_WORLD = 220

    fun costDp(s: Section): Int = when (s) {
        Section.CLOCK -> 0
        Section.WEATHER_PANEL -> 58
        Section.DATE_LINE -> 22
        Section.ALARM_CHIP -> 24
        Section.TIMERS -> 24
        Section.AGENDA_BANNER -> 26
        Section.WORLD_CLOCKS -> 26
        Section.FOOTER -> 22
        Section.STRIP -> 0
    }

    fun plan(widthDp: Int, heightDp: Int, settings: WidgetSettings, kind: WidgetKind = WidgetKind.CLASSIC): LayoutPlan {
        val w = widthDp.coerceAtLeast(40)
        val h = heightDp.coerceAtLeast(40)
        if (kind == WidgetKind.STRIP) return LayoutPlan(listOf(Section.STRIP), h, w, h, Section.STRIP)
        val s = kind.shape(settings)
        val wanted = buildList {
            if (s.showWeather) add(Section.WEATHER_PANEL)
            if (s.showGregorianDate || s.showHebrewDate || s.showHolidays) add(Section.DATE_LINE)
            if (s.showAlarm) add(Section.ALARM_CHIP)
            if (s.showTimers && s.timerRowActive) add(Section.TIMERS)
            if (s.showAgenda) add(Section.AGENDA_BANNER)
            if (s.showWorldClocks && s.worldClocks.isNotEmpty()) add(Section.WORLD_CLOCKS)
            if (s.showSteps || s.showSunTimes) add(Section.FOOTER)
        }
        if (!s.showClock) return planWithoutClock(w, h, wanted)
        val chosen = mutableListOf(Section.CLOCK)
        var used = 0
        for (sec in wanted) {
            if (sec == Section.WEATHER_PANEL && w < MIN_WIDTH_FOR_PANEL) continue
            if (sec == Section.WORLD_CLOCKS && w < MIN_WIDTH_FOR_WORLD) continue
            val c = costDp(sec)
            if ((h - used - c) >= h * MIN_CLOCK_SHARE && (h - used - c) >= 56) { chosen += sec; used += c }
        }
        // Keep the visual order stable: clock, date, weather panel, alarm, agenda, world clocks, footer.
        val order = listOf(Section.CLOCK, Section.DATE_LINE, Section.WEATHER_PANEL, Section.ALARM_CHIP, Section.TIMERS, Section.AGENDA_BANNER, Section.WORLD_CLOCKS, Section.FOOTER)
        return LayoutPlan(chosen.sortedBy { order.indexOf(it) }, h - used, w, h)
    }

    private val FLEX_ORDER = listOf(Section.WEATHER_PANEL, Section.AGENDA_BANNER, Section.WORLD_CLOCKS, Section.DATE_LINE, Section.ALARM_CHIP, Section.TIMERS, Section.FOOTER)
    private val STACK_ORDER = listOf(Section.DATE_LINE, Section.WEATHER_PANEL, Section.ALARM_CHIP, Section.TIMERS, Section.AGENDA_BANNER, Section.WORLD_CLOCKS, Section.FOOTER)

    /** Widgets without a clock (weather / agenda types): the first wanted section grows to fill what is left. */
    private fun planWithoutClock(w: Int, h: Int, wanted: List<Section>): LayoutPlan {
        val chosen = ArrayList<Section>()
        var used = 0
        for (sec in wanted) {
            if (sec == Section.WEATHER_PANEL && w < MIN_WIDTH_FOR_PANEL) continue
            if (sec == Section.WORLD_CLOCKS && w < MIN_WIDTH_FOR_WORLD) continue
            val c = costDp(sec)
            if (used + c <= h) { chosen += sec; used += c }
        }
        if (chosen.isEmpty()) return LayoutPlan(listOf(Section.CLOCK), h, w, h, Section.CLOCK) // never an empty widget
        val flex = FLEX_ORDER.first { it in chosen }
        return LayoutPlan(chosen.sortedBy { STACK_ORDER.indexOf(it) }, h - (used - costDp(flex)), w, h, flex)
    }

    /** Size of one flip card so two cards (hours, minutes) plus the gap fit the clock area; aspect 0.8 (taller than wide). */
    fun cardSizeDp(clockWidthDp: Int, clockHeightDp: Int, userScalePct: Int): Pair<Int, Int> {
        val gap = 8
        val byWidth = (clockWidthDp - gap) / 2
        val byHeight = (clockHeightDp / 1.0).toInt()
        val w = minOf(byWidth, (byHeight * 0.8).toInt())
        val scaled = (w * userScalePct.coerceIn(50, 150) / 100).coerceIn(24, maxOf(24, byWidth))
        return scaled to (scaled * 1.25).toInt()
    }
}
