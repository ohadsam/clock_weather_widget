package com.clockweather.core.timers

import com.clockweather.core.weather.HourPoint
import com.clockweather.core.weather.RainAlert
import com.clockweather.core.weather.Sky
import com.clockweather.core.widget.QuietHours
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TimersTest {
    @Test fun stopwatchCountsAcrossPausesAndSurvivesEncoding() {
        var s = Stopwatch().start(1_000)
        assertEquals(4_000, s.elapsed(5_000))
        s = s.pause(5_000)
        assertEquals(4_000, s.elapsed(99_000))
        s = Stopwatch.decode(s.start(100_000).encode())
        assertEquals(9_000, s.elapsed(105_000))
        assertFalse(s.reset().active)
        assertEquals(Stopwatch(), Stopwatch.decode("garbage"))
    }

    @Test fun countdownRunsPausesFinishesAndResets() {
        var c = Countdown().withDuration(60_000)
        assertEquals(60_000, c.remaining(0)); assertTrue(c.active)
        c = c.start(10_000)
        assertEquals(40_000, c.remaining(30_000))
        c = c.pause(30_000)
        assertEquals(40_000, c.remaining(500_000))
        c = c.start(500_000)
        assertEquals(540_000, c.endsAtMs)
        assertTrue(c.isDue(540_000)); assertFalse(c.isDue(539_999))
        c = c.finish()
        assertTrue(c.finished); assertEquals(0, c.remaining(1_000_000))
        assertEquals(60_000, c.reset().remaining(0))
        c = Countdown.decode(c.start(0).encode())
        assertTrue(c.running)
        assertFalse(Countdown().start(0).running) // no duration, nothing to start
    }

    @Test fun durationCannotChangeWhileRunning() {
        val c = Countdown().withDuration(60_000).start(0)
        assertEquals(60_000, c.withDuration(5_000).totalMs)
    }

    @Test fun formatting() {
        assertEquals("1:05", TimerFormat.clock(65_000)); assertEquals("1:02:03", TimerFormat.clock(3_723_000))
        assertEquals("⏱ 12 min", TimerFormat.widgetStopwatch(12 * 60_000 + 59_000, false))
        assertEquals("⏱ 1:05 שע׳", TimerFormat.widgetStopwatch(65 * 60_000L, true))
        val c = Countdown().withDuration(10 * 60_000L).start(0)
        assertEquals("⏳ 10 min", TimerFormat.widgetCountdown(c, 0, false))
        assertEquals("⏳ 1 min", TimerFormat.widgetCountdown(c, 9 * 60_000L + 10_000, false))
        assertEquals("⏳ הסתיים", TimerFormat.widgetCountdown(c.finish(), 0, true))
        assertTrue(TimerFormat.widgetCountdown(c.pause(60_000), 60_000, false).endsWith("(paused)"))
    }

    private val zone = ZoneId.of("Asia/Jerusalem")
    private val now = Instant.parse("2026-11-20T08:20:00Z") // 10:20 local
    private fun hour(h: Int, chance: Int, sky: Sky = Sky.CLEAR) = HourPoint(LocalDateTime.of(2026, 11, 20, h, 0), 15.0, chance, null, 60, sky, true)

    @Test fun rainAlertFiresOnlyForRainThatIsAboutToStart() {
        val dry = listOf(hour(9, 5), hour(10, 5), hour(11, 20), hour(12, 70, Sky.RAIN), hour(13, 80, Sky.RAIN))
        val a = assertNotNull(RainAlert.next(dry, now, zone, 50))
        assertEquals(100, a.inMinutes) // 12:00 is 100 minutes after 10:20
        assertNull(RainAlert.next(listOf(hour(10, 80, Sky.RAIN), hour(11, 80, Sky.RAIN)), now, zone, 50)) // already raining
        assertNull(RainAlert.next(listOf(hour(10, 5), hour(14, 90, Sky.RAIN)), now, zone, 50)) // too far ahead
        assertTrue(RainAlert.mayNotify(0, 7 * 3_600_000L)); assertFalse(RainAlert.mayNotify(0, 5 * 3_600_000L))
    }

    @Test fun quietHoursWrapPastMidnight() {
        assertTrue(QuietHours.isQuiet(true, 23, 7, LocalTime.of(23, 30)))
        assertTrue(QuietHours.isQuiet(true, 23, 7, LocalTime.of(3, 0)))
        assertFalse(QuietHours.isQuiet(true, 23, 7, LocalTime.of(7, 0)))
        assertFalse(QuietHours.isQuiet(false, 23, 7, LocalTime.of(3, 0)))
        assertTrue(QuietHours.isQuiet(true, 13, 15, LocalTime.of(14, 0)))
        assertFalse(QuietHours.isQuiet(true, 8, 8, LocalTime.of(8, 0)))
    }
}
