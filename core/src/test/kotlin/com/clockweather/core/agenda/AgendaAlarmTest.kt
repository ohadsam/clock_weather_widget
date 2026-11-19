package com.clockweather.core.agenda

import com.clockweather.core.alarm.AlarmIndicator
import com.clockweather.core.alarm.NextAlarm
import com.clockweather.core.text.Untrusted
import java.time.Instant
import java.time.ZoneId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AgendaAlarmTest {
    private val zone = ZoneId.of("Asia/Jerusalem")
    private val now = Instant.parse("2026-11-19T08:00:00Z") // 10:00 local
    private fun ms(iso: String) = Instant.parse(iso).toEpochMilli()
    private fun ev(id: Long, title: String, s: String, e: String, allDay: Boolean = false) = AgendaEvent(id, title, ms(s), ms(e), allDay)

    private val events = listOf(
        ev(1, "Past", "2026-11-19T05:00:00Z", "2026-11-19T06:00:00Z"),
        ev(2, "Later", "2026-11-19T12:00:00Z", "2026-11-19T13:00:00Z"),
        ev(3, "Now meeting", "2026-11-19T07:30:00Z", "2026-11-19T08:30:00Z"),
        ev(4, "Tomorrow", "2026-11-20T09:00:00Z", "2026-11-20T10:00:00Z"),
        ev(5, "Holiday‮", "2026-11-19T00:00:00Z", "2026-11-20T00:00:00Z", true),
        ev(6, "Soon", "2026-11-19T08:25:00Z", "2026-11-19T09:00:00Z"),
    )

    @Test fun todayKeepsOngoingFirstThenByStart() {
        val today = Agenda.forToday(events, now, zone)
        assertEquals(listOf("Holiday", "Now meeting", "Soon", "Later"), today.map { it.title })
        assertEquals(listOf("Now meeting", "Soon", "Later"), Agenda.forToday(events, now, zone, includeAllDay = false).map { it.title })
    }

    @Test fun bannerRotatesDeterministically() {
        val today = Agenda.forToday(events, now, zone)
        val seen = (0 until 4).map { Agenda.current(today, Instant.ofEpochSecond(now.epochSecond + it * 8L), 8)!!.title }
        assertEquals(today.map { it.title }.toSet(), seen.toSet())
        assertNull(Agenda.current(emptyList(), now, 8))
        assertEquals(3, Agenda.moreCount(today).coerceAtMost(3))
    }

    @Test fun bannerTexts() {
        val t = Agenda.forToday(events, now, zone).associateBy { it.id }
        assertEquals("All day · Holiday", Agenda.bannerText(t[5]!!, now, zone, true, false))
        assertEquals("Now · Now meeting", Agenda.bannerText(t[3]!!, now, zone, true, false))
        assertEquals("In 25 min · Soon", Agenda.bannerText(t[6]!!, now, zone, true, false))
        assertEquals("בעוד 25 ד׳ · Soon", Agenda.bannerText(t[6]!!, now, zone, true, true))
        assertEquals("14:00 · Later", Agenda.bannerText(t[2]!!, now, zone, true, false))
    }

    @Test fun untrustedTextIsCleaned() {
        assertEquals("a b", Untrusted.clean("a\u0000\n  b‮", 20))
        assertEquals("abc", Untrusted.clean("abcdef", 3))
        assertEquals("", Untrusted.clean("‏‪", 5))
    }

    @Test fun alarmIndicatorFiltersAndLabels() {
        val smart = NextAlarm(ms("2026-11-20T04:30:00Z"), "com.smartring.app") // tomorrow 06:30 local
        assertTrue(AlarmIndicator.visible(smart, now, 24, ""))
        assertTrue(AlarmIndicator.visible(smart, now, 24, "com.smartring.app"))
        assertFalse(AlarmIndicator.visible(smart, now, 24, "com.google.android.deskclock"))
        assertFalse(AlarmIndicator.visible(smart, now, 12, ""))
        assertTrue(AlarmIndicator.visible(smart, now, 0, ""))
        assertFalse(AlarmIndicator.visible(null, now, 24, ""))
        assertFalse(AlarmIndicator.visible(smart.copy(triggerAtMs = ms("2026-11-19T07:00:00Z")), now, 24, ""))
        assertEquals("מחר 06:30", AlarmIndicator.label(smart, now, zone, true, true))
        assertEquals("Tomorrow 06:30", AlarmIndicator.label(smart, now, zone, true, false))
        assertEquals("12:30", AlarmIndicator.label(NextAlarm(ms("2026-11-19T10:30:00Z"), null), now, zone, true, false))
        assertEquals("שבת 06:30", AlarmIndicator.label(NextAlarm(ms("2026-11-21T04:30:00Z"), null), now, zone, true, true))
    }
}
