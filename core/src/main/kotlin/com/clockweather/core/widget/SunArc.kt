package com.clockweather.core.widget

import java.time.LocalTime

/** The daylight arc on a 12-hour analog dial: from sunrise clockwise to sunset, in clock degrees (0 = 12 o'clock). Pure. */
object SunArc {
    data class Arc(val startDeg: Float, val sweepDeg: Float)

    private fun deg(t: LocalTime): Float = ((t.hour % 12) + t.minute / 60f) * 30f

    /** Null when the times are missing or make no sense (polar day / night, sunset before sunrise). */
    fun of(sunrise: LocalTime?, sunset: LocalTime?): Arc? {
        if (sunrise == null || sunset == null || !sunset.isAfter(sunrise)) return null
        val sweep = ((sunset.toSecondOfDay() - sunrise.toSecondOfDay()) / 3600f * 30f).coerceIn(10f, 350f) // 1 hour = 30 degrees
        return Arc(deg(sunrise), sweep)
    }

    /** True from sunrise to sunset (the hands are on the lit part of the dial). */
    fun isDay(now: LocalTime, sunrise: LocalTime?, sunset: LocalTime?): Boolean = sunrise != null && sunset != null && !now.isBefore(sunrise) && now.isBefore(sunset)
}
