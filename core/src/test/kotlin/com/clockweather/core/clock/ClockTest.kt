package com.clockweather.core.clock

import java.time.Instant
import java.time.ZoneId
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ClockTest {
    private val main = ZoneId.of("Asia/Jerusalem")

    @Test fun worldClockShowsOtherZoneAndDayShift() {
        val now = Instant.parse("2026-11-19T23:30:00Z") // Jerusalem 01:30 on the 20th, New York 18:30 on the 19th
        val ny = WorldClock.line(now, main, ClockZone("America/New_York", "NYC"), true)!!
        assertEquals("18:30", ny.time)
        assertEquals(-1, ny.dayShift)
        assertEquals("אתמול", WorldClock.shiftText(ny.dayShift, true))
        assertEquals("", WorldClock.shiftText(0, false))
    }

    @Test fun twelveHourFormat() {
        val l = WorldClock.line(Instant.parse("2026-11-19T15:05:00Z"), main, ClockZone("America/New_York", "NYC"), false)!!
        assertEquals("10:05 AM", l.time)
    }

    @Test fun badZoneIsDropped() {
        assertNull(WorldClock.line(Instant.now(), main, ClockZone("Not/AZone", "x"), true))
        assertTrue(WorldClock.POPULAR.all { WorldClock.validZone(it) != null })
    }

    @Test fun digitsFor24And12Hour() {
        assertContentEquals(intArrayOf(0, 9, 0, 5), WorldClock.digits(9, 5, true))
        assertContentEquals(intArrayOf(1, 2, 3, 0), WorldClock.digits(0, 30, false))
        assertContentEquals(intArrayOf(0, 3, 5, 9), WorldClock.digits(15, 59, false))
    }

    @Test fun flipMathOnlyChangedDigitsAnimateAndCurveIsMonotone() {
        val ch = FlipMath.changed(intArrayOf(1, 2, 5, 9), intArrayOf(1, 3, 0, 0))
        assertContentEquals(booleanArrayOf(false, true, true, true), ch)
        assertEquals(1f, FlipMath.oldTopScale(0f)); assertEquals(0f, FlipMath.oldTopScale(1f))
        assertEquals(0f, FlipMath.newBottomScale(0f)); assertEquals(1f, FlipMath.newBottomScale(1f), 0.001f)
        var prev = 2f
        for (p in FlipMath.burst(20)) { val s = FlipMath.oldTopScale(p); assertTrue(s <= prev); prev = s }
        assertEquals(1f, FlipMath.burst().last())
    }
}
