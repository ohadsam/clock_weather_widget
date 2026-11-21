package com.clockweather.core.weather

import java.time.LocalDateTime

enum class Metric { TEMP, RAIN, HUMIDITY, UV, WIND }

/** One metric over the hours of one day, ready for a chart: values may have gaps (null), [min]/[max] frame the axis. */
data class ChartSeries(val metric: Metric, val hours: List<Int>, val values: List<Double?>, val min: Double, val max: Double) {
    /** 0..1 position of value [i] between the axis ends (0.5 when the series is flat), null for a gap. */
    fun fraction(i: Int): Float? {
        val v = values.getOrNull(i) ?: return null
        return if (max - min < 1e-9) 0.5f else ((v - min) / (max - min)).toFloat().coerceIn(0f, 1f)
    }
}

/** Builds chart series from the hourly forecast. Pure: nothing here draws. */
object DayChart {
    fun series(hours: List<HourPoint>, metric: Metric, fahrenheit: Boolean = false): ChartSeries {
        val vals: List<Double?> = hours.map { h ->
            when (metric) {
                Metric.TEMP -> if (fahrenheit) h.tempC * 9.0 / 5.0 + 32.0 else h.tempC
                Metric.RAIN -> h.rainChancePct?.toDouble()
                Metric.HUMIDITY -> h.humidityPct?.toDouble()
                Metric.UV -> h.uvIndex
                Metric.WIND -> h.windKmh
            }
        }
        val present = vals.filterNotNull()
        val (lo, hi) = when (metric) {
            Metric.TEMP -> if (present.isEmpty()) 0.0 to 1.0 else Math.floor(present.min() - 1) to Math.ceil(present.max() + 1)
            Metric.RAIN, Metric.HUMIDITY -> 0.0 to 100.0
            Metric.UV -> 0.0 to maxOf(6.0, Math.ceil((present.maxOrNull() ?: 0.0) + 1))
            Metric.WIND -> 0.0 to maxOf(20.0, Math.ceil(((present.maxOrNull() ?: 0.0) + 5) / 5) * 5)
        }
        return ChartSeries(metric, hours.map { it.time.hour }, vals, lo, hi)
    }

    /** Index of the hour that contains [now] (null when the day does not include it). */
    fun nowIndex(hours: List<HourPoint>, now: LocalDateTime): Int? =
        hours.indexOfFirst { it.time.toLocalDate() == now.toLocalDate() && it.time.hour == now.hour }.takeIf { it >= 0 }

    /** Value text for the label above a point: `24°`, `60%`. */
    fun label(metric: Metric, value: Double?): String = when {
        value == null -> "–"
        metric == Metric.TEMP -> "${Math.round(value)}°"
        metric == Metric.UV || metric == Metric.WIND -> "${Math.round(value)}"
        else -> "${Math.round(value)}%"
    }
}
