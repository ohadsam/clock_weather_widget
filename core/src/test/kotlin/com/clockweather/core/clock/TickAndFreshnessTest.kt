package com.clockweather.core.clock

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TickAndFreshnessTest {
    @Test fun nextMinuteIsStrictlyAfterAndAligned() {
        for (now in listOf(0L, 1L, 59_999L, 60_000L, 60_001L, 1_763_600_000_123L, -1L, -60_000L)) {
            val n = TickMath.nextMinuteMs(now)
            assertTrue(n > now, "after $now")
            assertEquals(0L, Math.floorMod(n, 60_000L), "aligned $now")
            assertTrue(n - now in 1..60_000, "within a minute $now")
        }
        assertEquals(60_000L, TickMath.nextMinuteMs(0L))
        assertEquals(120_000L, TickMath.nextMinuteMs(60_000L)) // exactly on a boundary -> the following one
    }

    @Test fun everyMinuteOfADayIsReachedExactlyOnce() {
        var t = 1_763_600_000_000L
        val seen = HashSet<Long>()
        repeat(1440) { t = TickMath.nextMinuteMs(t); assertTrue(seen.add(t)) }
        assertEquals(1440L * 60_000L, t - TickMath.nextMinuteMs(1_763_600_000_000L) + 60_000L * 1)
    }

    @Test fun freshnessLevelsAndLabels() {
        assertEquals(Freshness.Level.FRESH, Freshness.level(89))
        assertEquals(Freshness.Level.STALE, Freshness.level(90))
        assertEquals(Freshness.Level.STALE, Freshness.level(359))
        assertEquals(Freshness.Level.OLD, Freshness.level(360))
        assertEquals("לפני 45 ד׳", Freshness.label(45, true))
        assertEquals("3 h ago", Freshness.label(180, false))
        assertEquals("לפני 2 ימים", Freshness.label(60 * 24 * 2 + 5, true))
        assertEquals(0L, Freshness.ageMinutes(1000, 900)) // clock set back: never negative
        assertEquals(5L, Freshness.ageMinutes(1000, 1300))
    }
}
