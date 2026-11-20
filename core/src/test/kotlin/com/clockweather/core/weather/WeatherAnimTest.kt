package com.clockweather.core.weather

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class WeatherAnimTest {
    @Test fun everyLoopHasTwelveValidFrames() {
        for ((key, seq) in WeatherAnimTable.SEQUENCES) {
            assertEquals(WeatherAnim.FRAMES, seq.size, key)
            val max = seq.max()
            assertTrue(seq.all { it in 1..max }, key)
            assertEquals((1..max).toSet(), seq.toSet(), "$key uses every unique frame")
        }
    }

    @Test fun keysFollowTheAssetContract() {
        assertEquals("clear_day", WeatherAnim.key(Sky.CLEAR, true))
        assertEquals("clear_night", WeatherAnim.key(Sky.CLEAR, false))
        assertEquals("rain_day", WeatherAnim.key(Sky.RAIN, false)) // no night art for rain
        assertEquals("partly_cloudy_night", WeatherAnim.key(Sky.PARTLY_CLOUDY, false))
    }

    @Test fun timeMapsToFramesAndWraps() {
        val key = WeatherAnimTable.SEQUENCES.keys.first()
        val seq = WeatherAnimTable.SEQUENCES.getValue(key)
        assertEquals(seq[0], WeatherAnim.frameAt(key, 0))
        assertEquals(seq[1], WeatherAnim.frameAt(key, 125))
        assertEquals(seq[0], WeatherAnim.frameAt(key, WeatherAnim.LOOP_MS))
        assertEquals(seq[11], WeatherAnim.frameAt(key, WeatherAnim.LOOP_MS - 1))
        assertNull(WeatherAnim.frameAt("no_such_loop", 0))
    }

    @Test fun stillSkiesWithoutMotionAreNotAnimated() {
        assertTrue(!WeatherAnim.has("clear_day"), "clear_day has no visible motion in the delivered frames")
    }
}
