package com.clockweather.core.widget

import com.clockweather.core.clock.FlipMath
import com.clockweather.core.clock.TickMath
import com.clockweather.core.clock.WidgetHealth
import com.clockweather.core.clock.WorldClock
import java.time.Instant
import java.time.ZoneId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TapZonesAndHealthTest {
    @Test fun everyRowHasATargetAndTheClockIsOnTop() {
        val plan = WidgetLayout.plan(300, 220, WidgetSettings(showSteps = true))
        val rows = TapZones.rows(plan)
        assertEquals(TapZones.ROWS, rows.size)
        assertEquals(TapTarget.CLOCK, rows.first())
        assertTrue(plan.sections.map(TapZones::targetOf).toSet().all { it in rows.toSet() || plan.sections.size > 3 })
        assertTrue(TapTarget.WEATHER in rows)
    }

    @Test fun clockOnlyWidgetHasNoWeatherZone() {
        val rows = TapZones.rows(WidgetLayout.plan(200, 120, WidgetSettings(), WidgetKind.CLOCK))
        assertEquals(TapTarget.CLOCK, rows.first())
        assertTrue(TapTarget.WEATHER !in rows)
    }

    @Test fun healthVerdicts() {
        val now = 10_000_000L
        assertEquals(WidgetHealth.State.NO_WIDGET, WidgetHealth.assess(false, 0, now, true))
        assertEquals(WidgetHealth.State.STOPPED, WidgetHealth.assess(true, 0, now, true))
        assertEquals(WidgetHealth.State.STOPPED, WidgetHealth.assess(true, now - 4 * 60_000L, now, true))
        assertEquals(WidgetHealth.State.LATE, WidgetHealth.assess(true, now - 100_000L, now, true))
        assertEquals(WidgetHealth.State.NO_EXACT_ALARM, WidgetHealth.assess(true, now - 10_000L, now, false))
        assertEquals(WidgetHealth.State.OK, WidgetHealth.assess(true, now - 10_000L, now, true))
    }

    /** Israel ends summer time on 25 Oct 2026 at 02:00 -> 01:00; the clock must follow the zone rules with no special code. */
    @Test fun clockFollowsDaylightSavingChanges() {
        val z = ZoneId.of("Asia/Jerusalem")
        fun digitsAt(utc: String) = Instant.parse(utc).atZone(z).let { WorldClock.digits(it.hour, it.minute, true).toList() }
        assertEquals(listOf(0, 1, 3, 0), digitsAt("2026-10-24T22:30:00Z")) // 01:30 summer time (UTC+3)
        assertEquals(listOf(0, 1, 3, 0), digitsAt("2026-10-24T23:30:00Z")) // 01:30 again, now winter time (UTC+2)
        assertEquals(listOf(0, 2, 0, 0), digitsAt("2026-10-25T00:00:00Z")) // 02:00 winter
        // spring forward: 27 Mar 2026, 02:00 -> 03:00
        assertEquals(listOf(0, 1, 5, 9), digitsAt("2026-03-26T23:59:00Z"))
        assertEquals(listOf(0, 3, 0, 0), digitsAt("2026-03-27T00:00:00Z"))
    }

    @Test fun minuteTicksAreNeverSkippedAcrossTheChange() {
        var t = Instant.parse("2026-10-24T22:58:30Z").toEpochMilli()
        val seen = ArrayList<Long>()
        repeat(5) { t = TickMath.nextMinuteMs(t); seen += t }
        assertTrue(seen.zipWithNext().all { (a, b) -> b - a == 60_000L })
    }
}
