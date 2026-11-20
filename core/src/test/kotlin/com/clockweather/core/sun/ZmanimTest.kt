package com.clockweather.core.sun

import java.time.LocalDate
import java.time.ZoneId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ZmanimTest {
    private val zone = ZoneId.of("Asia/Jerusalem")

    @Test fun nightfallIsAfterSunsetAndCandlesBefore() {
        val d = assertNotNull(Zmanim.of(LocalDate.of(2026, 12, 21), 32.09, 34.88, zone))
        val night = assertNotNull(d.nightfall)
        val gap = java.time.Duration.between(d.sunset, night).toMinutes()
        assertTrue(gap in 20..40, "gap $gap")
        assertEquals(18, java.time.Duration.between(d.candleLighting, d.sunset).toMinutes())
    }

    @Test fun candleLightingDays() {
        assertTrue(Zmanim.isCandleLightingDay(LocalDate.of(2026, 11, 20))) // Friday
        assertFalse(Zmanim.isCandleLightingDay(LocalDate.of(2026, 11, 18))) // Wednesday, nothing next day
        assertTrue(Zmanim.isCandleLightingDay(LocalDate.of(2026, 9, 11))) // eve of Rosh Hashana 5787 (12 Sep 2026)
    }

    @Test fun polarHasNoTimes() {
        assertNull(Zmanim.of(LocalDate.of(2026, 12, 21), 78.2, 15.6, ZoneId.of("Arctic/Longyearbyen")))
    }
}
