package com.clockweather.core.weather

import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DayChartTest {
    private val base = LocalDateTime.of(2026, 11, 20, 0, 0)
    private fun hour(h: Int, t: Double, rain: Int?, hum: Int?) = HourPoint(base.plusHours(h.toLong()), t, rain, null, hum, Sky.CLEAR, true)
    private val hours = listOf(hour(0, 14.0, 0, 70), hour(6, 12.0, 20, 80), hour(12, 22.0, 60, 40), hour(18, 18.0, null, null))

    @Test fun temperatureAxisFramesTheDataAndRainHumidityUseZeroToHundred() {
        val t = DayChart.series(hours, Metric.TEMP)
        assertEquals(11.0, t.min); assertEquals(23.0, t.max)
        assertEquals(1f / 12f, t.fraction(1)!!, 0.001f); assertTrue(t.fraction(2)!! in 0.9f..1f)
        val r = DayChart.series(hours, Metric.RAIN); assertEquals(0.0, r.min); assertEquals(100.0, r.max); assertEquals(0.6f, r.fraction(2)!!, 0.001f)
        assertNull(r.fraction(3)); assertNull(DayChart.series(hours, Metric.HUMIDITY).fraction(3))
        assertEquals(listOf(0, 6, 12, 18), t.hours)
    }

    @Test fun fahrenheitConvertsValues() {
        assertEquals(57.2, DayChart.series(hours, Metric.TEMP, true).values[0]!!, 0.001)
    }

    @Test fun flatAndEmptySeriesAreSafe() {
        val flat = DayChart.series(listOf(hour(0, 20.0, 0, 50), hour(1, 20.0, 0, 50)), Metric.TEMP)
        assertTrue(flat.fraction(0)!! in 0f..1f)
        val empty = DayChart.series(emptyList(), Metric.TEMP)
        assertTrue(empty.values.isEmpty() && empty.max > empty.min)
    }

    @Test fun nowIndexAndLabels() {
        assertEquals(2, DayChart.nowIndex(hours, base.plusHours(12).plusMinutes(30)))
        assertNull(DayChart.nowIndex(hours, base.plusDays(1)))
        assertEquals("22°", DayChart.label(Metric.TEMP, 21.6)); assertEquals("60%", DayChart.label(Metric.RAIN, 60.0)); assertEquals("–", DayChart.label(Metric.RAIN, null))
    }
}
