package com.clockweather.core.widget

/** What a tap on a part of the widget should open. The app maps each to a screen or to the matching other app. */
enum class TapTarget { CLOCK, DATE, WEATHER, ALARM, TIMERS, AGENDA, WORLD, SUN_STEPS }

/**
 * Splits the widget height into [ROWS] equal invisible tap rows and says which section each row belongs to, from the same
 * [LayoutPlan] the renderer paints (sections are stacked top to bottom with [WidgetLayout.costDp], the flexible one takes the rest).
 * Pure and tested, so taps always match what is drawn.
 */
object TapZones {
    const val ROWS = 20

    fun targetOf(s: Section): TapTarget = when (s) {
        Section.CLOCK, Section.STRIP -> TapTarget.CLOCK
        Section.DATE_LINE -> TapTarget.DATE
        Section.WEATHER_PANEL -> TapTarget.WEATHER
        Section.ALARM_CHIP -> TapTarget.ALARM
        Section.TIMERS -> TapTarget.TIMERS
        Section.AGENDA_BANNER -> TapTarget.AGENDA
        Section.WORLD_CLOCKS -> TapTarget.WORLD
        Section.FOOTER -> TapTarget.SUN_STEPS
    }

    fun rows(plan: LayoutPlan): List<TapTarget> {
        val heights = plan.sections.map { if (it == plan.flex) plan.clockHeightDp else WidgetLayout.costDp(it) }
        val total = heights.sum().coerceAtLeast(1)
        return List(ROWS) { i ->
            val y = (i + 0.5f) / ROWS * total
            var acc = 0
            var idx = plan.sections.lastIndex
            for ((k, h) in heights.withIndex()) { acc += h; if (y < acc) { idx = k; break } }
            targetOf(plan.sections[idx.coerceAtLeast(0)])
        }
    }
}
