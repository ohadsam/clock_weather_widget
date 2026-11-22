# Settings reference

Every field of `WidgetSettings` (`core/src/main/kotlin/com/clockweather/core/widget/WidgetSettings.kt`). One JSON string in SharedPreferences `settings`/`json`, always passed through `sanitized()` (clamps ranges, drops bad ids, caps lists), so old or corrupt data loads. **A repo test fails if a field is missing here - add a row in the same change as the field.**

Scope: **W** = affects the home-screen widget, **D** = the in-app dashboard, **S** = a sub-setting of something else.

| Field | Type | Default | What it does |
|---|---|---|---|
| `version` | `Int` | `VERSION` | Schema version of the stored JSON (always rewritten by `sanitized()`). |
| `clockStyle` | `ClockStyle` | `ClockStyle.FLIP` | Clock look: FLIP cards, DIGITAL, ANALOG (plain dial), MINIMAL, POCKET (golden pocket watch). |
| `paletteId` | `String` | `"classic_white"` | Colour palette id from `Palettes.ALL` (card, digit, panel, accent colours). |
| `pocketMetal` | `String` | `"gold"` | [PocketMetal.id], used by the pocket-watch style only. |
| `backgroundAlphaPct` | `Int` | `0` | Plate behind everything: 0 = fully transparent (default, the owner's request). |
| `panelAlphaPct` | `Int` | `75` | Plate of the weather/info panel under the clock. |
| `cornerRadiusDp` | `Int` | `18` |Corner radius (dp) of the plate and panels. |
| `scalePct` | `Int` | `100` | Extra scale on top of the automatic fit to the widget size. |
| `use24h` | `Boolean` | `true` | 24-hour (true) or 12-hour clock. |
| `showAmPm` | `Boolean` | `true` | Show AM/PM next to a 12-hour time. |
| `showClock` | `Boolean` | `true` | Show the clock section. |
| `showWeather` | `Boolean` | `true` | Show the weather panel (icon, temperature). |
| `showHiLo` | `Boolean` | `true` | Show today's max / min. |
| `showOutlook` | `Boolean` | `true` | Show the rain/sunny outlook text. |
| `showGregorianDate` | `Boolean` | `true` | Date line: Gregorian date. |
| `showHebrewDate` | `Boolean` | `true` | Date line: Hebrew date. |
| `showHolidays` | `Boolean` | `true` | Date line: today's holiday with its badge. |
| `showAlarm` | `Boolean` | `true` | Next-alarm chip. |
| `showAgenda` | `Boolean` | `true` | Today's calendar events banner. |
| `showWorldClocks` | `Boolean` | `true` | Extra time-zone row. |
| `showSteps` | `Boolean` | `false` | Steps today in the footer (also starts `StepWorker`). |
| `showSunTimes` | `Boolean` | `true` | Sunrise/sunset in the footer. |
| `showNightfall` | `Boolean` | `true` | Nightfall (tzeit) and, on a Shabbat / Yom Tov eve, candle lighting, next to the sun times. Needs [showSunTimes]. |
| `fahrenheit` | `Boolean` | `false` | Temperatures in Fahrenheit. |
| `rainLikelyFromPct` | `Int` | `50` | Rain chance (%) from which the outlook says "rain likely" and the rain alert fires. |
| `rollHebrewAtSunset` | `Boolean` | `true` | The Hebrew date changes at sunset instead of midnight. |
| `inIsrael` | `Boolean` | `true` | Israel holiday rules (one day) instead of diaspora (two days). |
| `language` | `String` | `"system"` | "system", "he" or "en". |
| `calmAnimations` | `Boolean` | `false` | No flip, lid or weather animation anywhere (dashboard too). |
| `animateWeather` | `Boolean` | `true` | Weather illustration loop on every minute tick while the screen is on. |
| `places` | `List<PlaceSetting>` | `emptyList()` | Saved places (max 4) used by the widget and the dashboard. `isCurrent` = follow my location. |
| `worldPlaces` | `List<PlaceSetting>` | `emptyList()` | Extra places around the world, shown with their weather and local time on the dashboard (not used by the widget). |
| `activePlaceId` | `String?` | `null` | Which saved place the widget shows. |
| `worldClocks` | `List<ClockZoneSetting>` | `emptyList()` | Extra time zones (max 3) for the widget row. |
| `alarmPackage` | `String` | `""` | Package whose next alarm is shown; empty = any alarm app (the system's next alarm). |
| `alarmShowWithinHours` | `Int` | `24` | Show the alarm chip only when the alarm is within this many hours (0 = always). |
| `agendaCalendarIds` | `List<Long>` | `emptyList()` | Calendar ids read for events (empty = all). |
| `showSettingsButton` | `Boolean` | `true` | Small gear in the widget corner that opens the app settings. |
| `seasonalTheme` | `Boolean` | `false` | Festive palette on the holiday itself ([Season]). |
| `autoDayNight` | `Boolean` | `false` | At night (after sunset) use [nightPaletteId] instead of the chosen palette. |
| `nightPaletteId` | `String` | `"midnight"` |Palette used at night when `autoDayNight` is on. |
| `autoContent` | `Boolean` | `false` | Smart content: the widget and the dashboard show what matters now and drop what is empty ([com.clockweather.core.auto.AutoContent]). |
| `showSpecialEvents` | `Boolean` | `true` | Special events (birthdays, memorials) from the Special Events app: nearest one in the widget row and the dashboard card. |
| `specialEventsDays` | `Int` | `7` | The widget row shows a special event only when it is at most this many days away (1-30). |
| `showParsha` | `Boolean` | `true` | Date line: the weekly Torah portion on Friday and Shabbat (Hebrew UI). |
| `showOmer` | `Boolean` | `true` | Date line: the Omer count (days 1-49). |
| `showAirQuality` | `Boolean` | `false` | Dashboard weather card: one line with air quality, UV and pollen (needs the extra host `air-quality-api.open-meteo.com`; off by default). |
| `shabbatMode` | `Boolean` | `false` | From candle lighting to nightfall: no animations and no rain alerts; one "candle lighting" notification 15 minutes before it and one "Shabbat ended" notification at nightfall (needs the notification permission). |
| `showTimers` | `Boolean` | `true` | Show an active stopwatch / countdown as a row in the widget. |
| `timerRowActive` | `Boolean` | `false` | DERIVED, never stored by the user: the app sets it from the timer state so the layout plan reserves the row. |
| `rainAlert` | `Boolean` | `false` | Notify when rain is about to start at the active place (opt-in; needs the notification permission). |
| `quietHoursEnabled` | `Boolean` | `false` | Quiet hours: no animations and no alerts between these hours. |
| `quietFromHour` | `Int` | `23` |Quiet hours start (hour of day). |
| `quietToHour` | `Int` | `7` |Quiet hours end (hour of day); wraps past midnight when smaller than the start. |
| `weatherAppPackage` | `String` | `""` | Weather app opened by a tap on the weather; empty = the weather screen of this app. |
| `perWidget` | `Map<String, WidgetOverride>` | `emptyMap()` | Per placed widget (key = appWidgetId): own clock style, palette, transparency, scale, 24h, and which sections show; null fields follow the global value (`forWidget(id)`). Edited in Settings -> Widgets on my home screen. |
| `dashboardOrder` | `List<String>` | `emptyList()` | In-app dashboard: card order and hidden cards ([com.clockweather.core.dashboard.DashTile] ids); empty = defaults. |
| `dashboardHidden` | `List<String>` | `emptyList()` | Dashboard cards the owner hid. |
| `agendaRotateSeconds` | `Int` | `8` | Seconds each event stays in the rotating banner. |
| `agendaIncludeAllDay` | `Boolean` | `true` | Include all-day events. |
