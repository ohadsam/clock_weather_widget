package com.clockweather.core.sun

import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SunTimesTest {
    private fun near(a: LocalTime, b: LocalTime, minutes: Int = 5) =
        assertTrue(abs(java.time.Duration.between(a, b).toMinutes()) <= minutes, "$a vs $b")

    @Test fun petahTikvaSummerAndWinter() {
        val zone = ZoneId.of("Asia/Jerusalem")
        val summer = assertNotNull(SunTimes.compute(LocalDate.of(2026, 6, 21), 32.09, 34.88, zone))
        near(summer.sunrise, LocalTime.of(5, 34), 3); near(summer.sunset, LocalTime.of(19, 49), 3) // independent NOAA implementation
        val winter = assertNotNull(SunTimes.compute(LocalDate.of(2026, 12, 21), 32.09, 34.88, zone))
        near(winter.sunrise, LocalTime.of(6, 37), 3); near(winter.sunset, LocalTime.of(16, 39), 3)
    }

    @Test fun polarNightHasNoSunrise() {
        assertNull(SunTimes.compute(LocalDate.of(2026, 12, 21), 78.2, 15.6, ZoneId.of("Arctic/Longyearbyen")))
    }
}
