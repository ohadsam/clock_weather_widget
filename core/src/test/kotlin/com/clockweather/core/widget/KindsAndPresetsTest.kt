package com.clockweather.core.widget

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class KindsAndPresetsTest {
    private val everything = WidgetSettings(showSteps = true, worldClocks = listOf(ClockZoneSetting("America/New_York", "NYC")))

    @Test fun clockKindShowsOnlyTheClockAndDate() {
        val p = WidgetLayout.plan(260, 200, everything, WidgetKind.CLOCK)
        assertTrue(Section.CLOCK in p)
        assertFalse(Section.WEATHER_PANEL in p || Section.AGENDA_BANNER in p || Section.ALARM_CHIP in p || Section.WORLD_CLOCKS in p || Section.FOOTER in p)
    }

    @Test fun weatherKindHasNoClockAndTheWeatherPanelFillsTheSpace() {
        val p = WidgetLayout.plan(280, 160, everything, WidgetKind.WEATHER)
        assertFalse(Section.CLOCK in p)
        assertEquals(Section.WEATHER_PANEL, p.flex)
        assertTrue(p.clockHeightDp > WidgetLayout.costDp(Section.WEATHER_PANEL))
        val total = p.sections.sumOf { if (it == p.flex) p.clockHeightDp else WidgetLayout.costDp(it) }
        assertTrue(total <= 160, "stack $total must fit 160")
    }

    @Test fun agendaKindLeadsWithAgenda() {
        val p = WidgetLayout.plan(280, 140, everything, WidgetKind.AGENDA)
        assertEquals(Section.AGENDA_BANNER, p.flex)
        assertFalse(Section.CLOCK in p || Section.WEATHER_PANEL in p)
    }

    @Test fun stripIsOneSectionAtAnySize() {
        for ((w, h) in listOf(250 to 50, 40 to 40, 800 to 120)) assertEquals(listOf(Section.STRIP), WidgetLayout.plan(w, h, everything, WidgetKind.STRIP).sections)
    }

    @Test fun nothingWantedStillGivesAClock() {
        val none = WidgetSettings(showClock = false, showWeather = false, showAgenda = false, showAlarm = false, showGregorianDate = false, showHebrewDate = false, showHolidays = false, showSunTimes = false, showWorldClocks = false)
        assertEquals(listOf(Section.CLOCK), WidgetLayout.plan(200, 100, none, WidgetKind.CLASSIC).sections)
    }

    @Test fun classicKindIsUnchangedBehaviour() {
        assertEquals(WidgetLayout.plan(320, 360, everything).sections, WidgetLayout.plan(320, 360, everything, WidgetKind.CLASSIC).sections)
    }

    @Test fun kindIdsAreUnique() = assertEquals(WidgetKind.values().size, WidgetKind.values().map { it.id }.toSet().size)

    @Test fun presetsChangeOnlyTheLookAndStayValid() {
        val user = WidgetSettings(places = listOf(PlaceSetting("a", "Petah Tikva", 32.0, 34.8)), alarmPackage = "com.smartring.app", agendaCalendarIds = listOf(5L), worldClocks = listOf(ClockZoneSetting("Asia/Tokyo", "Tokyo")))
        for (preset in Preset.values()) {
            val s = preset.apply(user).sanitized()
            assertEquals(user.places, s.places, preset.id); assertEquals("com.smartring.app", s.alarmPackage); assertEquals(listOf(5L), s.agendaCalendarIds)
            assertTrue(Palettes.byId(s.paletteId) != null, "${preset.id} palette")
        }
        assertEquals(ClockStyle.ANALOG, Preset.SUNSET_ANALOG.apply(user).clockStyle)
        assertEquals(0, Preset.CLASSIC_HTC.apply(user).backgroundAlphaPct)
        assertEquals(Preset.values().size, Preset.values().map { it.id }.toSet().size)
    }

    @Test fun hiddenClockSurvivesJson() {
        val s = WidgetSettings(showClock = false)
        assertFalse(WidgetSettings.decode(WidgetSettings.encode(s)).showClock)
    }
}
