package com.clockweather.core.timers

import com.clockweather.core.look.LookCode
import com.clockweather.core.widget.Appearance
import com.clockweather.core.widget.ClockStyle
import com.clockweather.core.widget.Palettes
import com.clockweather.core.widget.Season
import com.clockweather.core.widget.WidgetSettings
import java.time.LocalDate
import java.time.ZoneId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TimerItemsTest {
    private fun cd(id: String, label: String, minutes: Long, startAt: Long? = null): TimerItem {
        var c = Countdown().withDuration(minutes * 60_000L)
        if (startAt != null) c = c.start(startAt)
        return TimerItem(id, true, label).with(c)
    }

    @Test fun manyTimersPersistAndAreCleaned() {
        var list = emptyList<TimerItem>()
        repeat(15) { list = TimerList.add(list, TimerItem(TimerList.nextId(list, 1000L + it), it % 2 == 0, "t$it")) }
        assertEquals(TimerList.MAX_ITEMS, list.size)
        val back = TimerList.decode(TimerList.encode(list))
        assertEquals(list, back)
        assertEquals(emptyList(), TimerList.decode("not json"))
        val evil = TimerList.decode(TimerList.encode(listOf(TimerItem("a", true, "x".repeat(200) + "\u0007", sound = "BOGUS"))))
        assertEquals(TimerList.MAX_LABEL, evil.single().label.length)
        assertEquals(SoundKind.ALARM, evil.single().soundKind)
    }

    @Test fun widgetLinesShowLabelsMostUrgentFirstAndSkipIdle() {
        val now = 1_000_000L
        val list = listOf(
            cd("a", "Pasta", 10, now), cd("b", "Eggs", 3, now), cd("idle", "Unused", 5),
            TimerItem("s", false, "Run").with(Stopwatch().start(now - 5 * 60_000L)),
        )
        val lines = TimerList.widgetLines(list, now, false)
        assertEquals(listOf("⏳ Eggs 3 min", "⏳ Pasta 10 min", "⏱ Run 5 min"), lines)
        assertEquals("b", TimerList.nextDue(list)?.id)
        assertTrue(TimerList.widgetLines(listOf(cd("idle", "x", 5)), now, true).isEmpty())
    }

    @Test fun dateCountdowns() {
        val today = LocalDate.of(2026, 11, 21)
        val list = listOf(DateCountdown("1", "Hanukkah", LocalDate.of(2026, 12, 5).toEpochDay()), DateCountdown("2", "Old", today.minusDays(3).toEpochDay()), DateCountdown("3", "Soon", today.plusDays(1).toEpochDay()))
        assertEquals(listOf("3", "1"), DateCountdowns.upcoming(list, today).map { it.id })
        assertEquals("📅 Tomorrow · Soon", DateCountdowns.widgetLine(list, today, false))
        assertEquals("14 days · Hanukkah", DateCountdowns.text(list[0], today, false))
        assertEquals("14 ימים · Hanukkah", DateCountdowns.text(list[0], today, true))
        assertNull(DateCountdowns.widgetLine(listOf(DateCountdown("9", "Far", today.plusDays(90).toEpochDay())), today, false))
        assertEquals(list.take(1), DateCountdowns.decode(DateCountdowns.encode(list.take(1))))
    }

    @Test fun lookCodeRoundTripKeepsPersonalDataOut() {
        val mine = WidgetSettings(clockStyle = ClockStyle.POCKET, paletteId = "sand", pocketMetal = "rose", seasonalTheme = true, activePlaceId = "home", agendaCalendarIds = listOf(7L))
        val code = LookCode.encodeLook(mine)
        assertTrue(code.startsWith("CW1:"))
        assertFalse(code.contains("home"))
        val other = WidgetSettings(places = listOf(com.clockweather.core.widget.PlaceSetting("x", "Elsewhere", 1.0, 2.0)))
        val applied = assertNotNull(LookCode.applyLook(other, code))
        assertEquals(ClockStyle.POCKET, applied.clockStyle); assertEquals("rose", applied.pocketMetal); assertTrue(applied.seasonalTheme)
        assertEquals(other.places, applied.places) // personal data untouched
        assertNull(LookCode.applyLook(other, "CW1:@@@")); assertNull(LookCode.applyLook(other, "hello"))
    }

    @Test fun backupRoundTrip() {
        val s = WidgetSettings(paletteId = "ocean", places = listOf(com.clockweather.core.widget.PlaceSetting("p1", "Home", 32.0, 34.8)))
        assertEquals(s.sanitized(), LookCode.decodeBackup(LookCode.encodeBackup(s)))
        assertNull(LookCode.decodeBackup("CWB1:!!!")); assertNull(LookCode.decodeBackup("junk"))
    }

    @Test fun seasonsAndAppearance() {
        assertEquals(Season.HANUKKAH, Season.of(LocalDate.of(2026, 12, 5), true))
        assertEquals(Season.ROSH_HASHANA, Season.of(LocalDate.of(2026, 9, 12), true))
        assertNull(Season.of(LocalDate.of(2026, 11, 18), true))
        val s = WidgetSettings(paletteId = "sand", seasonalTheme = true, autoDayNight = true)
        assertEquals("s_hanukkah", Appearance.paletteId(s, LocalDate.of(2026, 12, 5), isNight = false))
        assertEquals("midnight", Appearance.paletteId(s, LocalDate.of(2026, 12, 5), isNight = true))
        assertEquals("sand", Appearance.paletteId(s, LocalDate.of(2026, 11, 18), isNight = false))
        assertEquals("sand", Appearance.paletteId(WidgetSettings(paletteId = "sand"), LocalDate.of(2026, 12, 5), isNight = true))
        assertTrue(Appearance.isNight(18, 0, 6 * 60, 17 * 60 + 30)); assertFalse(Appearance.isNight(12, 0, null, null)); assertTrue(Appearance.isNight(21, 0, null, null))
    }

    @Test fun seasonalPalettesAreReadable() {
        for (p in Palettes.SEASONAL) {
            assertTrue(Palettes.contrast(p.digit, p.cardTop) >= 4.5, "${p.id} digit/card")
            assertTrue(Palettes.contrast(p.panelText, p.panel) >= 4.5, "${p.id} text/panel")
        }
    }
}

class SpecialEventsTest {
    private val zone = ZoneId.of("Asia/Jerusalem")
    private val today = LocalDate.of(2026, 11, 21)
    private fun ev(id: Long, title: String, day: LocalDate) = com.clockweather.core.agenda.AgendaEvent(id, title, day.atStartOfDay(zone).toInstant().toEpochMilli(), day.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli(), true)

    @Test fun nearestFirstWithinWindowAndLabelled() {
        val list = com.clockweather.core.agenda.SpecialEvents.upcoming(
            listOf(ev(1, "יום הולדת דני", today.plusDays(3)), ev(2, "אזכרה לסבא", today), ev(3, "Far", today.plusDays(40)), ev(4, "Past", today.minusDays(2)), ev(5, "  ", today.plusDays(1))),
            today, zone, withinDays = 14,
        )
        assertEquals(listOf("אזכרה לסבא", "יום הולדת דני"), list.map { it.title })
        assertEquals("🕯 אזכרה לסבא · היום", com.clockweather.core.agenda.SpecialEvents.text(list[0], true))
        assertEquals("🎂 יום הולדת דני · בעוד 3 ימים", com.clockweather.core.agenda.SpecialEvents.widgetLine(list.drop(1), true))
        assertEquals("🎂 Dan's birthday · tomorrow", com.clockweather.core.agenda.SpecialEvents.text(com.clockweather.core.agenda.SpecialEvents.Item("Dan's birthday", today.plusDays(1), 1, "🎂"), false))
        assertNull(com.clockweather.core.agenda.SpecialEvents.widgetLine(emptyList(), true))
    }
}

class JewishDayTest {
    private val zone = ZoneId.of("Asia/Jerusalem")

    @Test fun hanukkahShowsTheRightCandlesEachDayAndEvening() {
        val d1 = LocalDate.of(2026, 12, 5) // 25 Kislev 5787
        assertEquals("hanukkah_1", com.clockweather.core.widget.Season.ornamentKey(d1, true))
        assertEquals("hanukkah_8", com.clockweather.core.widget.Season.ornamentKey(d1.plusDays(7), true))
        assertEquals((1..8).map { "hanukkah_$it" }, (0..7).map { com.clockweather.core.widget.Season.ornamentKey(d1.plusDays(it.toLong()), true) })
        // the evening before day 1 already counts as the first night
        val eve = com.clockweather.core.widget.Season.effectiveDay(d1.minusDays(1), java.time.LocalTime.of(17, 30), java.time.LocalTime.of(16, 40))
        assertEquals("hanukkah_1", com.clockweather.core.widget.Season.ornamentKey(eve, true))
        assertEquals(d1.minusDays(1), com.clockweather.core.widget.Season.effectiveDay(d1.minusDays(1), java.time.LocalTime.of(12, 0), java.time.LocalTime.of(16, 40)))
        assertEquals("pesach", com.clockweather.core.widget.Season.ornamentKey(LocalDate.of(2027, 4, 22), true))
    }

    @Test fun parshaAndOmer() {
        assertEquals("ויצא", com.clockweather.core.hebrew.WeeklyPortion.label(LocalDate.of(2026, 11, 21), true)) // Shabbat
        assertEquals("ויצא", com.clockweather.core.hebrew.WeeklyPortion.label(LocalDate.of(2026, 11, 18), true)) // Wednesday: the coming Shabbat
        assertEquals("וישב", com.clockweather.core.hebrew.WeeklyPortion.label(LocalDate.of(2026, 12, 5), true)) // Shabbat Hanukkah
        val h = com.clockweather.core.hebrew.HebrewDate(5787, com.clockweather.core.hebrew.HebrewCalendar.NISAN, 16)
        assertEquals(1, com.clockweather.core.hebrew.Omer.day(h))
        assertEquals(49, com.clockweather.core.hebrew.Omer.day(com.clockweather.core.hebrew.HebrewDate(5787, com.clockweather.core.hebrew.HebrewCalendar.SIVAN, 5)))
        assertNull(com.clockweather.core.hebrew.Omer.day(com.clockweather.core.hebrew.HebrewDate(5787, com.clockweather.core.hebrew.HebrewCalendar.SIVAN, 6)))
        assertEquals("Omer: day 8", com.clockweather.core.hebrew.Omer.text(8, false))
    }

    @Test fun shabbatModeWindow() {
        val lat = 32.09; val lon = 34.88
        val fri = LocalDate.of(2026, 11, 20); val sat = fri.plusDays(1)
        fun on(d: LocalDate, h: Int, m: Int) = com.clockweather.core.sun.ShabbatMode.isActive(d, java.time.LocalTime.of(h, m), lat, lon, zone, true)
        assertFalse(on(fri, 12, 0)); assertTrue(on(fri, 17, 30)); assertTrue(on(sat, 12, 0)); assertFalse(on(sat, 20, 30)); assertFalse(on(LocalDate.of(2026, 11, 18), 17, 30))
    }

    @Test fun havdalahOnlyOnTheLastRestDay() {
        val lat = 32.09; val lon = 34.88; val fri = LocalDate.of(2026, 11, 20)
        fun h(d: LocalDate) = com.clockweather.core.sun.ShabbatMode.havdalahOn(d, lat, lon, zone, true)
        assertNull(h(fri)); assertNull(h(fri.minusDays(1))); assertNotNull(h(fri.plusDays(1)))
        assertTrue(h(fri.plusDays(1))!!.hour in 16..19)
    }

    @Test fun comfortAndSpeechAndPresets() {
        assertEquals("נעים", com.clockweather.core.weather.Comfort.word(22.0, true)); assertEquals("Very cold", com.clockweather.core.weather.Comfort.word(0.0, false))
        assertEquals("Thursday 17:05, 25°, rain expected, alarm 06:30", com.clockweather.core.widget.WidgetSpeech.sentence(false, "17:05", "Thursday", "25°, rain expected", listOf("alarm 06:30")))
        val preset = TimerItem("p", true, "Eggs", cd = Countdown().withDuration(8 * 60_000L).start(0).encode(), sound = "ALARM", vibrate = false)
        val fresh = TimerList.fromPreset(preset, "n1")
        assertEquals("n1", fresh.id); assertEquals(8 * 60_000L, fresh.countdownState.totalMs); assertFalse(fresh.countdownState.running); assertEquals("Eggs", fresh.label)
        assertEquals("", TimerList.decode(TimerList.encode(listOf(TimerItem("x", true, soundUri = "file:///evil")))).single().soundUri)
        assertEquals("content://media/1", TimerList.decode(TimerList.encode(listOf(TimerItem("x", true, soundUri = "content://media/1")))).single().soundUri)
        assertTrue(com.clockweather.core.widget.Preset.values().size >= 9)
    }
}
