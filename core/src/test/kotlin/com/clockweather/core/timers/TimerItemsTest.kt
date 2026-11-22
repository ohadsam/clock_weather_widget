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

    @Test fun airQualityParsesTheProbedShapeAndWords() {
        val body = """{"current":{"time":"2026-11-21T23:00","interval":3600,"european_aqi":59,"uv_index":0.0},"hourly":{"time":["2026-11-21T22:00","2026-11-21T23:00"],"birch_pollen":[0.0,3.0],"grass_pollen":[0.0,12.0],"olive_pollen":[null,0.0]}}"""
        val r = com.clockweather.core.weather.AirQuality.parse(body)!!
        assertEquals(59, r.aqi); assertEquals(12, r.pollen)
        assertEquals("Air: Moderate (59) · Pollen 12", com.clockweather.core.weather.AirQuality.line(r, false))
        assertEquals("אוויר: בינונית (59) · אבקנים 12", com.clockweather.core.weather.AirQuality.line(r, true))
        assertNull(com.clockweather.core.weather.AirQuality.parse("{}")); assertNull(com.clockweather.core.weather.AirQuality.parse("["))
        assertNull(com.clockweather.core.weather.AirQuality.parse("""{"current":{"european_aqi":9999}}"""))
        assertTrue(com.clockweather.core.weather.AirQuality.url(32.0888, 34.88666).startsWith("https://air-quality-api.open-meteo.com/"))
    }

    @Test fun intervalPlanPhasesAndClamps() {
        val p = IntervalPlan.of(40, 20, 3)
        assertEquals(5, p.phaseCount); assertEquals(IntervalPlan.Kind.WORK, p.kind); assertEquals(40_000L, p.durationMs)
        val seq = generateSequence(p) { it.next() }.toList()
        assertEquals(listOf("W1", "R1", "W2", "R2", "W3"), seq.map { (if (it.kind == IntervalPlan.Kind.WORK) "W" else "R") + it.round })
        assertTrue(seq.last().isLast); assertNull(seq.last().next()); assertEquals(3 * 40_000L + 2 * 20_000L, p.total())
        assertEquals(3, generateSequence(IntervalPlan.of(30, 0, 3)) { it.next() }.count()) // no rest: rounds follow each other
        assertEquals("Round 1/3 · Rest", seq[1].label(false)); assertEquals("סבב 1/3 · מנוחה", seq[1].label(true))
        assertEquals(IntervalPlan(5, 0, 1, 0), IntervalPlan.of(1, -4, 0))
        assertEquals(p.copy(phase = 4), IntervalPlan.decode(p.copy(phase = 4).encode())); assertEquals(4, IntervalPlan.decode("40|20|3|99")!!.phase)
        for (bad in listOf("", "x|1|2|3", "1|2|3", "40|20|3|-|1", "9".repeat(60))) assertNull(IntervalPlan.decode(bad), bad)
        val kept = TimerList.decode(TimerList.encode(listOf(TimerItem("a", true, iv = p.encode()), TimerItem("b", false, iv = p.encode()), TimerItem("c", true, iv = "junk"))))
        assertEquals(p, kept[0].interval); assertNull(kept[1].interval); assertNull(kept[2].interval)
    }

    @Test fun dateCardTextForTheWatchTile() {
        val c = com.clockweather.core.hebrew.DateCardText.of(LocalDate.of(2026, 11, 21), true, false)
        assertTrue(c.gregorian.startsWith("Saturday")); assertTrue(c.hebrewDate.isNotBlank()); assertNotNull(c.extra) // a Shabbat: the weekly portion
        val he = com.clockweather.core.hebrew.DateCardText.of(LocalDate.of(2026, 12, 8), true, true) // 28 Kislev: Hanukkah
        assertTrue(he.extra!!.contains("חנוכה"))
        assertTrue(com.clockweather.core.hebrew.DateCardText.untilMidnightMs(0L, ZoneId.of("UTC")) in 60_000L..86_400_000L)
    }

    @Test fun wearStateRoundTripLinesAndClamps() {
        val now = 1_000_000_000L
        val run = TimerItem("a", true, "Eggs").with(Countdown().withDuration(8 * 60_000L).start(now))
        val paused = TimerItem("b", true, "Pasta").with(Countdown().withDuration(10 * 60_000L).start(now).pause(now + 120_000L))
        val s = com.clockweather.core.wear.WearState.of(now, "Tel Aviv", "24°", "26° / 17°", 30, "Clear", false, listOf(run, paused, TimerItem("c", false)))
        val back = com.clockweather.core.wear.WearState.decode(s.encode())!!
        assertEquals(2, back.timers.size)
        assertEquals(listOf("Tel Aviv", "24°  Clear", "26° / 17°  ☂ 30%"), back.weatherLines())
        assertEquals(listOf("⏳ Eggs 8 min", "⏳ Pasta 8 min (paused)"), back.timerLines(now))
        assertEquals("⏳ Eggs 3 min", back.timerLines(now + 5 * 60_000L)[0])
        assertEquals("⏳ Eggs Done", back.timerLines(now + 9 * 60_000L)[0])
        assertTrue(!back.isStale(now + 1000L) && back.isStale(now + 4 * 3_600_000L))
        for (bad in listOf("", "{", "[]", "x".repeat(9000), """{"v":2,"at":1,"place":"a","temp":"b","hiLo":"c","sky":"d","hebrew":false}""")) assertNull(com.clockweather.core.wear.WearState.decode(bad), bad.take(20))
        val hostile = com.clockweather.core.wear.WearState.decode(s.copy(place = "x".repeat(500), rain = 999).encode())!!
        assertTrue(hostile.place.length <= 30); assertNull(hostile.rain)
    }

    @Test fun sunArcOnATwelveHourDial() {
        val a = com.clockweather.core.widget.SunArc.of(java.time.LocalTime.of(6, 0), java.time.LocalTime.of(17, 0))!!
        assertEquals(180f, a.startDeg); assertEquals(330f, a.sweepDeg) // 11 hours of daylight = 330 degrees
        val b = com.clockweather.core.widget.SunArc.of(java.time.LocalTime.of(6, 30), java.time.LocalTime.of(18, 30))!!
        assertEquals(195f, b.startDeg); assertEquals(350f, b.sweepDeg) // 12 h = 360, clamped so the arc never closes
        assertNull(com.clockweather.core.widget.SunArc.of(null, java.time.LocalTime.NOON)); assertNull(com.clockweather.core.widget.SunArc.of(java.time.LocalTime.NOON, java.time.LocalTime.of(6, 0)))
        assertTrue(com.clockweather.core.widget.SunArc.isDay(java.time.LocalTime.of(12, 0), java.time.LocalTime.of(6, 0), java.time.LocalTime.of(17, 0)))
        assertTrue(!com.clockweather.core.widget.SunArc.isDay(java.time.LocalTime.of(20, 0), java.time.LocalTime.of(6, 0), java.time.LocalTime.of(17, 0)))
    }

    @Test fun candleLightingAndWatchEvents() {
        val lat = 32.09; val lon = 34.88; val fri = LocalDate.of(2026, 11, 20)
        fun c(d: LocalDate) = com.clockweather.core.sun.ShabbatMode.candleLightingOn(d, lat, lon, zone, true)
        assertNotNull(c(fri)); assertNull(c(fri.minusDays(1))); assertNull(c(fri.plusDays(1)))
        val at = c(fri)!!; assertTrue(at.hour in 15..18)
        val s = com.clockweather.core.wear.WearState.of(1L, "x", "1°", "2/0", null, "Clear", false, emptyList(), listOf("🎂 Dana, today", "💍 Lee, tomorrow", "third"))
        val back = com.clockweather.core.wear.WearState.decode(s.encode())!!
        assertEquals(listOf("🎂 Dana, today", "💍 Lee, tomorrow"), back.eventLines())
        assertTrue(com.clockweather.core.wear.WearState.decode("""{"at":1,"place":"a","temp":"b","hiLo":"c","rain":null,"sky":"d","hebrew":false}""")!!.events.isEmpty()) // an older phone build still decodes
    }

    @Test fun todaySummaryGroupsTheUpcomingThingsInOrder() {
        val zone = ZoneId.of("Asia/Jerusalem")
        val noon = LocalDate.of(2026, 11, 20).atTime(12, 0).atZone(zone).toInstant() // a Friday
        fun ev(title: String, h: Int) = com.clockweather.core.agenda.AgendaEvent(h.toLong(), title, LocalDate.of(2026, 11, 20).atTime(h, 0).atZone(zone).toInstant().toEpochMilli(), LocalDate.of(2026, 11, 20).atTime(h + 1, 0).atZone(zone).toInstant().toEpochMilli(), false)
        val f = com.clockweather.core.dashboard.TodaySummary.Facts(noon, zone, false, true, true, java.time.LocalTime.of(16, 40), java.time.LocalTime.of(16, 20),
            listOf(ev("Past", 9), ev("Dentist", 14)), listOf(com.clockweather.core.agenda.SpecialEvents.Item("Dana", LocalDate.of(2026, 11, 21), 1, "🎂")), listOf("⏳ Eggs 5 min"), "Alarm 06:30", "24° · Clear")
        val rows = com.clockweather.core.dashboard.TodaySummary.rows(f)
        val texts = rows.map { it.text }
        assertTrue(texts.indexOf("24° · Clear") < texts.indexOf("Alarm 06:30"))
        assertTrue("14:00 Dentist" in texts); assertTrue(texts.none { it.contains("Past") }) // an event that is over is not listed
        assertTrue("Eggs 5 min" in texts); assertTrue("Dana · tomorrow" in texts)
        assertEquals("Candle lighting 16:20", texts.last()) // the next time marker of the evening comes last
        assertTrue(rows.size <= com.clockweather.core.dashboard.TodaySummary.MAX_ROWS)
        assertEquals("tomorrow", com.clockweather.core.agenda.SpecialEvents.whenText(1, false)); assertEquals("בעוד 3 ימים", com.clockweather.core.agenda.SpecialEvents.whenText(3, true))
        assertTrue(com.clockweather.core.dashboard.DashTile.values().first { it.id == "today" } == com.clockweather.core.dashboard.Dashboard.visibleTiles(emptyList(), emptyList())[1])
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
