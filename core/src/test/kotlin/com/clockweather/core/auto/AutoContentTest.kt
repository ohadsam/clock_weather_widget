package com.clockweather.core.auto

import com.clockweather.core.agenda.AgendaEvent
import com.clockweather.core.alarm.NextAlarm
import com.clockweather.core.dashboard.DashTile
import com.clockweather.core.widget.WidgetSettings
import java.time.Instant
import java.time.ZoneId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AutoContentTest {
    private val zone = ZoneId.of("Asia/Jerusalem")
    private val noon = Instant.parse("2026-11-18T10:00:00Z") // Wednesday 12:00 local
    private fun facts(events: List<AgendaEvent> = emptyList(), alarm: NextAlarm? = null, now: Instant = noon, places: Int = 1) =
        Facts(now, zone, events, alarm, null, null, places, 0)
    private fun ev(startMin: Long) = AgendaEvent(1, "Meeting", noon.toEpochMilli() + startMin * 60_000L, noon.toEpochMilli() + (startMin + 30) * 60_000L, false)

    @Test fun emptyThingsAreDroppedAndUserOffStaysOff() {
        val s = WidgetSettings(showAgenda = true, showAlarm = true, showWeather = true, showSteps = true, showWorldClocks = true)
        val a = AutoContent.apply(s, facts())
        assertFalse(a.showAgenda); assertFalse(a.showAlarm); assertFalse(a.showWeather); assertFalse(a.showSteps); assertFalse(a.showWorldClocks)
        val off = AutoContent.apply(WidgetSettings(showAgenda = false), facts(listOf(ev(30))))
        assertFalse(off.showAgenda) // the user's switch is the upper limit
    }

    @Test fun eventsAndNearAlarmAreShown() {
        val s = WidgetSettings(showAgenda = true, showAlarm = true)
        val a = AutoContent.apply(s, facts(listOf(ev(30)), NextAlarm(noon.toEpochMilli() + 3 * 3_600_000L, null)))
        assertTrue(a.showAgenda && a.showAlarm)
        val far = AutoContent.apply(s, facts(alarm = NextAlarm(noon.toEpochMilli() + 20 * 3_600_000L, null)))
        assertFalse(far.showAlarm)
    }

    @Test fun sunTimesOnlyAtUsefulHours() {
        val s = WidgetSettings(showSunTimes = true)
        assertFalse(AutoContent.apply(s, facts()).showSunTimes) // midday, not Friday
        assertTrue(AutoContent.apply(s, facts(now = Instant.parse("2026-11-18T15:00:00Z"))).showSunTimes) // 17:00
    }

    @Test fun dashboardPutsWhatMattersFirstAndDropsEmpty() {
        val all = DashTile.values().toList()
        val out = AutoDashboard.arrange(all, WidgetSettings(), facts(listOf(ev(30)), NextAlarm(noon.toEpochMilli() + 2 * 3_600_000L, null)))
        assertEquals(DashTile.WIDGET, out.first())
        assertEquals(listOf(DashTile.ALARM, DashTile.AGENDA), out.drop(1).take(2))
        assertFalse(DashTile.STEPS in out); assertFalse(DashTile.WORLD in out); assertFalse(DashTile.PLACES in out)
        val quiet = AutoDashboard.arrange(all, WidgetSettings(), facts())
        assertFalse(DashTile.AGENDA in quiet); assertFalse(DashTile.ALARM in quiet)
    }
}
