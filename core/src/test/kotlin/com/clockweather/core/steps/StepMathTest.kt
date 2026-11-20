package com.clockweather.core.steps

import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class StepMathTest {
    private val d1 = LocalDate.of(2026, 11, 20)
    private val d2 = d1.plusDays(1)

    @Test fun firstReadingOfADayIsZeroThenCounts() {
        val a = StepMath.update(null, 50_000, d1)
        assertEquals(0, a.stepsToday)
        assertEquals(1200, StepMath.update(a.state, 51_200, d1).stepsToday)
    }

    @Test fun newDayStartsFromTheLastReading() {
        val a = StepMath.update(null, 1000, d1)
        val b = StepMath.update(a.state, 9000, d1)
        val c = StepMath.update(b.state, 9100, d2)
        assertEquals(8000, b.stepsToday)
        assertEquals(0, c.stepsToday)
        assertEquals(150, StepMath.update(c.state, 9250, d2).stepsToday)
    }

    @Test fun rebootRestartsTheCounter() {
        val a = StepMath.update(null, 1000, d1)
        val b = StepMath.update(a.state, 6000, d1)
        val c = StepMath.update(b.state, 300, d1) // rebooted: 300 steps since boot
        assertEquals(300, c.stepsToday)
        assertEquals(500, StepMath.update(c.state, 500, d1).stepsToday)
    }

    @Test fun encodeDecodeRoundTripAndGarbage() {
        val s = StepMath.State(d1, 5, 9)
        assertEquals(s, StepMath.decode(StepMath.encode(s)))
        assertNull(StepMath.decode("junk")); assertNull(StepMath.decode(null)); assertNull(StepMath.decode("x|1|2"))
    }
}
