package com.clockweather.core.widget

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class WidgetTest {
    private val full = WidgetSettings(showSteps = true, worldClocks = listOf(ClockZoneSetting("America/New_York", "NYC")))

    @Test fun smallWidgetKeepsOnlyTheClock() {
        val p = WidgetLayout.plan(110, 70, full)
        assertEquals(listOf(Section.CLOCK), p.sections)
    }

    @Test fun largerWidgetsAddSectionsByPriority() {
        val medium = WidgetLayout.plan(260, 150, full)
        assertTrue(Section.WEATHER_PANEL in medium)
        val large = WidgetLayout.plan(320, 360, full.copy(timerRowActive = true))
        assertTrue(large.sections.containsAll(Section.values().filter { it != Section.STRIP }))
        assertFalse(Section.TIMERS in WidgetLayout.plan(320, 360, full)) // no row unless a timer is active
        assertTrue(large.clockHeightDp >= 360 * 0.42f)
        assertEquals(Section.CLOCK, large.sections.first())
    }

    @Test fun narrowWidgetNeverGetsPanelOrWorldClocks() {
        val p = WidgetLayout.plan(120, 400, full)
        assertFalse(Section.WEATHER_PANEL in p || Section.WORLD_CLOCKS in p)
    }

    @Test fun disabledSectionsAreNeverPlanned() {
        val p = WidgetLayout.plan(320, 360, WidgetSettings(showWeather = false, showAgenda = false, showAlarm = false))
        assertFalse(Section.WEATHER_PANEL in p || Section.AGENDA_BANNER in p || Section.ALARM_CHIP in p)
    }

    @Test fun tinyOrZeroSizesDoNotCrash() {
        assertEquals(listOf(Section.CLOCK), WidgetLayout.plan(0, 0, full).sections)
        val (w, h) = WidgetLayout.cardSizeDp(10, 10, 50)
        assertTrue(w >= 24 && h > w)
    }

    @Test fun cardsScaleWithUserScaleAndStayInBounds() {
        val (w100, _) = WidgetLayout.cardSizeDp(300, 200, 100)
        val (w50, _) = WidgetLayout.cardSizeDp(300, 200, 50)
        val (w150, _) = WidgetLayout.cardSizeDp(300, 200, 150)
        assertTrue(w50 < w100 && w100 <= w150 && w150 <= 146)
    }

    @Test fun sanitizeRepairsEverything() {
        val bad = WidgetSettings(
            paletteId = "nope", backgroundAlphaPct = 500, scalePct = 3, cornerRadiusDp = -4, language = "xx", alarmShowWithinHours = 999,
            agendaRotateSeconds = 0,
            places = List(9) { PlaceSetting("p$it", "Place $it", 10.0, 20.0) } + PlaceSetting("bad", "Bad", 999.0, 0.0) + PlaceSetting("p1", "dup", 1.0, 1.0),
            worldClocks = listOf(ClockZoneSetting("Mars/Base", "M"), ClockZoneSetting("Asia/Tokyo", "Tokyo"), ClockZoneSetting("Asia/Tokyo", "dup")),
            activePlaceId = "ghost", alarmPackage = "com.x\u0000;rm -rf",
        ).sanitized()
        assertEquals(Palettes.DEFAULT.id, bad.paletteId)
        assertEquals(100, bad.backgroundAlphaPct); assertEquals(50, bad.scalePct); assertEquals(0, bad.cornerRadiusDp)
        assertEquals("system", bad.language); assertEquals(168, bad.alarmShowWithinHours); assertEquals(3, bad.agendaRotateSeconds)
        assertEquals(WidgetSettings.MAX_PLACES, bad.places.size)
        assertEquals("p0", bad.activePlaceId)
        assertEquals(listOf("Asia/Tokyo"), bad.worldClocks.map { it.zoneId })
        assertEquals("com.xrm-rf".replace("-", ""), bad.alarmPackage.replace("-", ""))
    }

    @Test fun jsonRoundTripAndCorruptionFallBackToDefaults() {
        val s = WidgetSettings(clockStyle = ClockStyle.ANALOG, backgroundAlphaPct = 30, places = listOf(PlaceSetting("a", "Petah Tikva", 32.09, 34.88)))
        assertEquals(s.sanitized(), WidgetSettings.decode(WidgetSettings.encode(s)))
        assertEquals(WidgetSettings().sanitized(), WidgetSettings.decode("{not json"))
        assertEquals(WidgetSettings().sanitized(), WidgetSettings.decode(null))
        assertEquals(WidgetSettings().sanitized(), WidgetSettings.decode("x".repeat(100_000)))
        // a newer app wrote an unknown key and an unknown enum value: still loads
        assertEquals(ClockStyle.FLIP, WidgetSettings.decode("""{"clockStyle":"HOLOGRAM","future":1}""").clockStyle)
    }

    @Test fun defaultsAreTransparentAndPrivate() {
        val d = WidgetSettings()
        assertEquals(0, d.backgroundAlphaPct)
        assertFalse(d.showSteps)
        assertTrue(d.places.isEmpty())
    }

    @Test fun everyPaletteIsReadable() {
        for (p in Palettes.ALL) {
            assertTrue(Palettes.contrast(p.digit, p.cardTop) >= 4.5, "${p.id} digit/card top")
            assertTrue(Palettes.contrast(p.digit, p.cardBottom) >= 4.5, "${p.id} digit/card bottom")
            assertTrue(Palettes.contrast(p.panelText, p.panel) >= 4.5, "${p.id} panel text")
            assertTrue(Palettes.contrast(p.accent, p.panel) >= 3.0, "${p.id} accent on panel")
        }
        assertEquals(Palettes.ALL.size, Palettes.ALL.map { it.id }.toSet().size)
    }
}
