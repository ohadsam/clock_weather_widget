package com.clockweather.core.widget

import kotlin.test.Test
import kotlin.test.assertTrue

class PocketWatchGeometryTest {
    private fun frac(v: Float) = v in 0.0f..1.0f

    @Test fun generatedFractionsAreSane() {
        val g = PocketWatchGeometry
        assertTrue(frac(g.HINGE_Y) && frac(g.DIAL_CX) && frac(g.DIAL_CY) && frac(g.DIAL_R))
        assertTrue(g.DIAL_CX in 0.4f..0.6f, "dial is horizontally centred")
        assertTrue(g.DIAL_CY > g.HINGE_Y, "dial is below the hinge")
        assertTrue(g.DIAL_R in 0.25f..0.5f)
        for (p in listOf(g.HOUR_PIVOT_X, g.HOUR_PIVOT_Y, g.MINUTE_PIVOT_X, g.MINUTE_PIVOT_Y, g.SECOND_PIVOT_X, g.SECOND_PIVOT_Y)) assertTrue(frac(p))
        assertTrue(g.HOUR_PIVOT_Y > 0.5f && g.MINUTE_PIVOT_Y > 0.5f, "hands extend above their pivot")
    }
}
