# CONTEXT - ClockWise (read first)

## Product
Widget-first native Android app. The home-screen widget is the product: a big, beautiful, resizable, **transparent** widget (reference: an HTC-Sense-style flip clock over the wallpaper with a dark weather panel below; direction only, not a copy). Shows: flip / digital / analog / minimal clock, weather (current, max/min, "rain expected / sunny"), Gregorian + Hebrew date with holidays, next alarm (from a chosen alarm app, e.g. SmartRing), a rotating banner of today's Google Calendar events, up to 3 extra time zones, sunrise/sunset, optional steps. The app = live preview + settings + weather drill-down (hourly temp / rain / humidity, up to 4 places). Tapping widget regions opens the matching screen.

## Status (2026-11-21, version 0.3.0)
- `:core` green (`gradle :core:test`, ~120 tests). `:app` compiles and passes CI on `main`; every push publishes the signed APK as pre-release `latest`. Art integrated: logo, 16 weather loops, pocket watch (gold, silver, rose), UI spots, 13 holiday badges. Optional pending: round 7 seasonal ornaments (`docs/prompts/ROUND7_SEASONAL_PROMPT.md`).
- Built: widget (5 types, tap zones per section, smart content, quiet hours, festive/night looks), dashboard of customisable cards with drill-downs, forecast charts, places + map + world places, several labelled timers and date countdowns, updater + share, backup/look codes, permissions and widget-status screens, first-run guide. Nothing here is verified on a phone yet (see AGENT.md "Not verifiable").
- Next: `docs/PLAN.md` (per-widget settings, emulator UI tests with screenshots, in-app guide screenshots, `/simplify` + `/code-review` passes).

## Decisions (do not re-litigate)
- 2026-11-19 (owner) Two clock worlds: flip cards = digital (digits); analog = a golden pocket watch with an opening lid and chain (NOT hands on a card). Clock styles: FLIP, DIGITAL, MINIMAL (digital) and ANALOG (plain dial), POCKET (gold pocket watch, placeholder art until Round 6).
- 2026-11-19 (owner) Name: **ClockWise** (one word). Package/applicationId `com.clockweather.app` and repo name `clock_weather_widget` stay unchanged so builds keep updating in place.
- 2026-11-19 (owner) The calendar is **Google Calendar** (Gmail), not Outlook: read through the device calendar, which already carries it - no Google login or API key.
- 2026-11-19 (owner) All ROADMAP suggestions are approved (ledger R28). The user chooses what the widget shows or hides (Settings -> What to show) and there are several widget TYPES in the launcher picker (R29, R30), plus one-tap look presets (R31).
- 2026-11-19 Native Kotlin, widget drawn as one Bitmap via Canvas (not Glance) for pixel-exact flip/analog faces and a transparent plate.
- 2026-11-19 Google Calendar events come from the device calendar (Google Calendar syncs into it, as does any calendar account on the phone; read-only `READ_CALENDAR`); no Google login or API key. SmartRing alarm via `AlarmManager.getNextAlarmClock()` + package filter; no change to SmartRing needed.
- 2026-11-19 Hebrew calendar + holidays computed locally in `:core` (offline). Weather/geocoding = Open-Meteo (no key), coarse coordinates only.
- 2026-11-19 Default background transparency 100% (owner's request); weather panel plate 75%.
- 2026-11-19 `:app` only joins the Gradle build where an Android SDK exists, so `:core` tests run in the cloud sandbox.

## Rules
`.claude/skills/release-checklist/SKILL.md` section 0. Strings only in `values/` (he) + `values-en/`. `:core` has no Android imports and never calls `now()` (tests enforce).

## Data model
`WidgetSettings` (JSON in SharedPreferences `settings/json`, `version` field, sanitised on every load; max 4 places, 3 world clocks). Forecast cache: raw Open-Meteo JSON per place in SharedPreferences `forecast`, parsed by `OpenMeteo.parseForecast`.

## File map (every .kt file must be listed - enforced by RepoHygieneTest)
| File | Purpose |
|---|---|
| `app/src/androidTest/java/com/clockweather/app/ScreenshotJourneyTest.kt` | Emulator test: walks the main screens and saves screenshots |
| `app/src/androidTest/java/com/clockweather/app/HomeScreenTest.kt` | Emulator smoke test |
| `app/src/main/java/com/clockweather/app/MainActivity.kt` | App entry + state navigation |
| `app/src/main/java/com/clockweather/app/WidgetConfigActivity.kt` | Launcher configure hook for the widget |
| `app/src/main/java/com/clockweather/app/data/AlarmSource.kt` | System next alarm (getNextAlarmClock) |
| `app/src/main/java/com/clockweather/app/data/CalendarSource.kt` | Read-only device calendar (Google Calendar sync) |
| `app/src/main/java/com/clockweather/app/data/LocationSource.kt` | Coarse last-known location |
| `app/src/main/java/com/clockweather/app/data/Net.kt` | The only network entry (HTTPS allow-list, size cap) |
| `app/src/main/java/com/clockweather/app/data/SettingsStore.kt` | Settings persistence |
| `app/src/main/java/com/clockweather/app/data/WeatherRepository.kt` | Forecast fetch + cache + place search |
| `app/src/main/java/com/clockweather/app/data/AirQualityRepository.kt` | Air quality / UV / pollen fetch + cache for the active place (only when switched on) |
| `app/src/main/java/com/clockweather/app/data/WeatherWorker.kt` | 30-minute weather refresh (WorkManager) |
| `app/src/main/java/com/clockweather/app/ui/Components.kt` | Shared Compose components |
| `app/src/main/java/com/clockweather/app/ui/HomeScreen.kt` | Home: live preview + shortcuts |
| `app/src/main/java/com/clockweather/app/ui/SettingsScreen.kt` | Settings sections |
| `app/src/main/java/com/clockweather/app/ui/Theme.kt` | Material theme |
| `app/src/main/java/com/clockweather/app/ui/WeatherScreen.kt` | Weather drill-down |
| `app/src/main/java/com/clockweather/app/widget/ClockWidgetProvider.kt` | The home-screen widget provider |
| `app/src/main/java/com/clockweather/app/widget/RenderState.kt` | Inputs of one widget frame |
| `app/src/main/java/com/clockweather/app/widget/Snapshot.kt` | Builds RenderState from stored data |
| `app/src/main/java/com/clockweather/app/widget/TickScheduler.kt` | Minute-boundary exact alarm |
| `app/src/main/java/com/clockweather/app/widget/WeatherArt.kt` | Weather illustration loader + placeholder |
| `app/src/main/java/com/clockweather/app/widget/WidgetRenderer.kt` | Draws the widget bitmap (all styles and sections) |
| `app/src/test/java/com/clockweather/app/WidgetRendererTest.kt` | Robolectric renderer tests |
| `core/src/main/kotlin/com/clockweather/core/agenda/Agenda.kt` | Today's events, rotation, banner text |
| `core/src/main/kotlin/com/clockweather/core/alarm/AlarmIndicator.kt` | Next-alarm filtering and label |
| `core/src/main/kotlin/com/clockweather/core/clock/FlipMath.kt` | Flip-card animation math |
| `core/src/main/kotlin/com/clockweather/core/clock/WorldClock.kt` | Extra time-zone clocks, flip digits |
| `core/src/main/kotlin/com/clockweather/core/hebrew/Gematria.kt` | Hebrew numerals for days and years |
| `core/src/main/kotlin/com/clockweather/core/hebrew/HebrewDate.kt` | Hebrew calendar arithmetic (fixed-day algorithm), HebrewDate |
| `core/src/main/kotlin/com/clockweather/core/hebrew/DateCardText.kt` | Date card lines (weekday/date, Hebrew date, holiday or portion) for the Wear tile; pure |
| `core/src/main/kotlin/com/clockweather/core/hebrew/HebrewDateFormat.kt` | Hebrew date text + roll at sunset |
| `core/src/main/kotlin/com/clockweather/core/hebrew/Holidays.kt` | Jewish holidays/special days for a Gregorian day (Israel/diaspora) |
| `core/src/main/kotlin/com/clockweather/core/steps/StepMath.kt` | Daily steps from a cumulative counter (reboot-safe) |
| `core/src/main/kotlin/com/clockweather/core/dashboard/TodaySummary.kt` | The "Today and next" card: rows from the day's facts, pure |
| `core/src/main/kotlin/com/clockweather/core/dashboard/Dashboard.kt` | Dashboard cards: ids, order and visibility maths |
| `core/src/main/kotlin/com/clockweather/core/widget/TapZones.kt` | Which section each tap row of the widget opens (from the layout plan) |
| `core/src/main/kotlin/com/clockweather/core/clock/WidgetHealth.kt` | Verdict: is the minute tick alive? |
| `core/src/main/kotlin/com/clockweather/core/auto/AutoContent.kt` | Smart content rules for the widget and the dashboard order |
| `core/src/main/kotlin/com/clockweather/core/weather/PlaceIds.kt` | Place ids and fallback names for tapped map points |
| `core/src/main/kotlin/com/clockweather/core/timers/Timers.kt` | Stopwatch and countdown state machines + text for app and widget |
| `core/src/main/kotlin/com/clockweather/core/timers/IntervalPlan.kt` | Interval timer (work/rest rounds) plan: phases, clamps, wording; runs on a countdown |
| `core/src/main/kotlin/com/clockweather/core/weather/RainAlert.kt` | Rain about to start: when to alert |
| `core/src/main/kotlin/com/clockweather/core/wear/WearState.kt` | What the phone sends the watch (weather line + countdowns): codec, clamps, tile lines; pure |
| `core/src/main/kotlin/com/clockweather/core/weather/AirQuality.kt` | Air quality / UV / pollen: URL, parsing, wording (pure) |
| `core/src/main/kotlin/com/clockweather/core/widget/QuietHours.kt` | Night mode hours (wrap past midnight) |
| `core/src/main/kotlin/com/clockweather/core/timers/TimerItems.kt` | Many labelled timers, each with its own end-of-time sound/vibration |
| `core/src/main/kotlin/com/clockweather/core/timers/DateCountdown.kt` | Days until an event |
| `core/src/main/kotlin/com/clockweather/core/look/LookCode.kt` | Share-my-look code and full backup text |
| `core/src/main/kotlin/com/clockweather/core/widget/SunArc.kt` | Daylight arc (sunrise -> sunset) on a 12-hour analog dial; pure |
| `core/src/main/kotlin/com/clockweather/core/widget/Seasons.kt` | Holiday looks and the effective palette (night / season / chosen) |
| `core/src/main/kotlin/com/clockweather/core/widget/WidgetOverride.kt` | What one placed widget changes vs the global settings |
| `core/src/main/kotlin/com/clockweather/core/agenda/SpecialEvents.kt` | Birthdays / special events as "N days until" lines |
| `core/src/main/kotlin/com/clockweather/core/hebrew/Parsha.kt` | Weekly Torah portion (ported from the Special Events app) |
| `core/src/main/kotlin/com/clockweather/core/hebrew/Omer.kt` | Omer count |
| `core/src/main/kotlin/com/clockweather/core/weather/Comfort.kt` | Comfort and UV words |
| `core/src/main/kotlin/com/clockweather/core/sun/ShabbatMode.kt` | Shabbat / Yom Tov window for the calm mode |
| `core/src/main/kotlin/com/clockweather/core/widget/WidgetSpeech.kt` | One TalkBack sentence for the widget |
| `core/src/main/kotlin/com/clockweather/core/sun/SunTimes.kt` | Offline sunrise/sunset (NOAA) |
| `core/src/main/kotlin/com/clockweather/core/sun/Zmanim.kt` | Nightfall (tzeit), candle lighting, Shabbat/Yom Tov eve |
| `core/src/main/kotlin/com/clockweather/core/text/Untrusted.kt` | Cleaning of untrusted text |
| `core/src/main/kotlin/com/clockweather/core/weather/OpenMeteo.kt` | Open-Meteo URLs + tolerant parsers (forecast, geocoding) |
| `core/src/main/kotlin/com/clockweather/core/weather/WeatherModels.kt` | Sky enum (WMO mapping, labels), Place, Forecast, Day/Hour models |
| `core/src/main/kotlin/com/clockweather/core/weather/WeatherOutlook.kt` | One-line outlook (sunny/rain expected) and temperature text |
| `core/src/main/kotlin/com/clockweather/core/widget/Palettes.kt` | Colour palettes + contrast math |
| `core/src/main/kotlin/com/clockweather/core/widget/WidgetLayout.kt` | Which sections fit a widget size; card sizing |
| `core/src/main/kotlin/com/clockweather/core/widget/WidgetSettings.kt` | All widget options, sanitiser, JSON |
| `core/src/test/kotlin/com/clockweather/core/agenda/AgendaAlarmTest.kt` | Agenda, alarm indicator, untrusted text tests |
| `core/src/test/kotlin/com/clockweather/core/clock/ClockTest.kt` | World clock + flip math tests |
| `core/src/test/kotlin/com/clockweather/core/hebrew/HebrewCalendarTest.kt` | Hebrew calendar, gematria, holidays tests |
| `core/src/test/kotlin/com/clockweather/core/hygiene/RepoHygieneTest.kt` | Docs file map, strings parity, :core purity, changelog |
| `core/src/test/kotlin/com/clockweather/core/steps/StepMathTest.kt` | Step maths tests |
| `core/src/test/kotlin/com/clockweather/core/dashboard/DashboardTest.kt` | Dashboard order/visibility tests |
| `core/src/test/kotlin/com/clockweather/core/widget/TapZonesAndHealthTest.kt` | Tap zones, widget health, daylight-saving clock tests |
| `core/src/test/kotlin/com/clockweather/core/auto/AutoContentTest.kt` | Smart content tests |
| `core/src/test/kotlin/com/clockweather/core/weather/PlaceIdsTest.kt` | Place id and world-places sanitising tests |
| `core/src/test/kotlin/com/clockweather/core/timers/TimersTest.kt` | Timers, rain alert and quiet hours tests |
| `core/src/test/kotlin/com/clockweather/core/timers/TimerItemsTest.kt` | Timer list, date countdowns, look code, seasons tests |
| `core/src/test/kotlin/com/clockweather/core/sun/SunTimesTest.kt` | Sunrise/sunset tests |
| `core/src/test/kotlin/com/clockweather/core/sun/ZmanimTest.kt` | Nightfall / candle lighting tests |
| `core/src/test/kotlin/com/clockweather/core/weather/OpenMeteoTest.kt` | Forecast/geocoding parsing, WMO, outlook tests |
| `core/src/test/kotlin/com/clockweather/core/widget/WidgetTest.kt` | Layout, settings sanitising/JSON, palette contrast tests |
| `core/src/main/kotlin/com/clockweather/core/widget/WidgetKind.kt` | Widget types (classic/clock/weather/agenda/strip) and one-tap look presets |
| `core/src/test/kotlin/com/clockweather/core/widget/KindsAndPresetsTest.kt` | Widget kinds, flex layout and presets tests |
| `core/src/main/kotlin/com/clockweather/core/weather/WeatherAnim.kt` | Weather loop timing: which frame at which time (12 frames, 8 fps) |
| `core/src/main/kotlin/com/clockweather/core/weather/WeatherAnimTable.kt` | GENERATED frame sequences per loop (scripts/make-art-assets.py anim) |
| `core/src/test/kotlin/com/clockweather/core/weather/WeatherAnimTest.kt` | Loop table and timing tests |
| `core/src/main/kotlin/com/clockweather/core/clock/TickMath.kt` | Next-minute tick math and weather freshness labels |
| `core/src/test/kotlin/com/clockweather/core/clock/TickAndFreshnessTest.kt` | Tick and freshness tests |
| `app/src/main/java/com/clockweather/app/data/CalendarChangeWorker.kt` | Background watcher: redraw on any calendar change |
| `core/src/main/kotlin/com/clockweather/core/widget/PocketWatchGeometry.kt` | GENERATED fractions for the pocket-watch art (scripts/make-art-assets.py pocket) |
| `core/src/test/kotlin/com/clockweather/core/widget/PocketWatchGeometryTest.kt` | Pocket-watch geometry sanity test |
| `app/src/main/java/com/clockweather/app/widget/ArtCache.kt` | Decodes drawable art once by name |
| `core/src/main/kotlin/com/clockweather/core/hebrew/HolidayBadge.kt` | Maps holiday ids to the 13 approved badge drawables (or deliberately none) |
| `core/src/test/kotlin/com/clockweather/core/hebrew/HolidayBadgeTest.kt` | Every holiday id has a badge or an explicit no-badge |
| `core/src/main/kotlin/com/clockweather/core/update/UpdateMath.kt` | In-app updater rules (release manifest parsing, allowed URLs, version order) and share text |
| `core/src/test/kotlin/com/clockweather/core/update/UpdateMathTest.kt` | Updater rules tests |
| `core/src/main/kotlin/com/clockweather/core/weather/DayChart.kt` | Hourly chart series (temperature, rain, humidity) |
| `core/src/test/kotlin/com/clockweather/core/weather/DayChartTest.kt` | Chart series tests |
| `app/src/main/java/com/clockweather/app/data/Updater.kt` | In-app update: manifest, verified download, installer session |
| `app/src/main/java/com/clockweather/app/data/InstallReceiver.kt` | PackageInstaller result receiver |
| `app/src/main/java/com/clockweather/app/data/AppSharer.kt` | Share the app (link or APK via FileProvider) |
| `app/src/main/java/com/clockweather/app/ui/UpdatesSection.kt` | Settings section: updates and sharing |
| `app/src/main/java/com/clockweather/app/ui/SkyIcon.kt` | Weather illustration composable (optionally animated) |
| `app/src/main/java/com/clockweather/app/ui/HourChart.kt` | Animated hourly chart (temperature, rain, humidity) |
| `app/src/main/java/com/clockweather/app/ui/PermissionsSection.kt` | Settings: every permission, why, state, allow / open settings |
| `app/src/main/java/com/clockweather/app/data/StepSource.kt` | Today's steps from the device step counter |
| `app/src/main/java/com/clockweather/app/data/StepWorker.kt` | 15-minute step refresh |
| `app/src/main/java/com/clockweather/app/data/SourceApps.kt` | Open the calendar / alarm app the data comes from |
| `app/src/main/java/com/clockweather/app/ui/DashboardTiles.kt` | Dashboard cards (date, weather, alarm, events, sun, steps, world clocks) |
| `app/src/main/java/com/clockweather/app/ui/DashboardDetails.kt` | Drill-down screens for dashboard cards |
| `app/src/main/java/com/clockweather/app/ui/DashboardEditor.kt` | Settings: show/hide and reorder dashboard cards |
| `app/src/main/java/com/clockweather/app/ui/WidgetTile.kt` | Dashboard card with the live widget preview and widget quick settings |
| `app/src/main/java/com/clockweather/app/ui/WeatherParticles.kt` | Soft rain / snow drifting over the dashboard weather card (code-drawn, only while animation is on) |
| `app/src/main/java/com/clockweather/app/data/TickState.kt` | When the minute tick last ran |
| `app/src/main/java/com/clockweather/app/data/HeartbeatWorker.kt` | 15-minute safety net that re-arms the tick |
| `app/src/main/java/com/clockweather/app/widget/WidgetTaps.kt` | What each widget tap zone opens (calendar, alarm, weather app, our screens) |
| `app/src/main/java/com/clockweather/app/ui/WidgetStatusSection.kt` | Settings: is the clock tick alive, repair button |
| `app/src/main/java/com/clockweather/app/ui/OsmMap.kt` | OpenStreetMap view (osmdroid) with pins and tap |
| `app/src/main/java/com/clockweather/app/ui/PlacePickerScreen.kt` | Pick a place by search or map tap, preview weather and local time, save it |
| `app/src/main/java/com/clockweather/app/data/TimerStore.kt` | Stored stopwatch and countdown |
| `app/src/main/java/com/clockweather/app/data/Notifier.kt` | The only notification poster (countdown, rain) |
| `app/src/main/java/com/clockweather/app/data/CountdownAlarm.kt` | Exact alarm + receiver when the countdown ends |
| `app/src/main/java/com/clockweather/app/data/RainAlerts.kt` | Rain-is-about-to-start notification check |
| `app/src/main/java/com/clockweather/app/data/HavdalahAlert.kt` | "Shabbat ended" notification at nightfall (Shabbat mode on), called by the minute tick |
| `app/src/main/java/com/clockweather/app/data/CandleAlert.kt` | Candle-lighting notification 15 minutes before (Shabbat mode on), called by the minute tick |
| `app/src/main/java/com/clockweather/app/ui/TimerTiles.kt` | Dashboard cards: stopwatch and countdown |
| `app/src/main/java/com/clockweather/app/ui/DateCountdownTile.kt` | Dashboard card: days until a holiday or a chosen date |
| `app/src/main/java/com/clockweather/app/ui/BackupSection.kt` | Settings: share my look, backup, restore by pasted code |
| `app/src/main/java/com/clockweather/app/ui/GuideDialog.kt` | First-run guide (4 pages) and its seen flag |
| `app/src/main/java/com/clockweather/app/ui/WidgetsSection.kt` | Settings: each placed widget with its own look and items |
| `app/src/main/java/com/clockweather/app/data/SpecialEventsSource.kt` | Reads the Special Events app's calendar (birthdays, memorials) |
| `app/src/main/java/com/clockweather/app/ui/BedsideScreen.kt` | Bedside clock mode |
| `app/src/main/java/com/clockweather/app/widget/Speech.kt` | TalkBack sentence for the widget |
| `app/src/main/java/com/clockweather/app/ui/PlacesOverview.kt` | All saved places at a glance on Home |
| `wear/build.gradle.kts`, `wear/src/main/AndroidManifest.xml`, `wear/src/main/res/values*/strings.xml` | Wear OS date tile module (standalone, no permissions, same sideload key) |
| `wear/src/main/java/com/clockweather/wear/StatusTileService.kt`, `DataLayerService.kt`, `StateStore.kt` | Watch tile with the phone's weather and running timers: Data Layer listener, stored copy, tile |
| `app/src/main/java/com/clockweather/app/data/WearSync.kt` | Sends the weather line + countdowns to the watch over the Data Layer when they change |
| `wear/src/main/java/com/clockweather/wear/DateTileService.kt` | The watch tile: weekday + date, Hebrew date, holiday / weekly portion; redraws after midnight |
| `app/build.gradle.kts`, `settings.gradle.kts`, `gradle/libs.versions.toml` | build (versionName here is the single source) |
| `app/src/main/AndroidManifest.xml` | permissions (each commented), widget receiver, activities |
| `app/src/main/res/*` | strings he/en, widget layout + info, theme, placeholder icon |
| `.github/workflows/*` | CI, latest-APK publish with in-place-update check, API probe |
| `docs/*` | CONTEXT (this), ARCHITECTURE (design), FEATURES, SETTINGS_REFERENCE, DATA_AND_PRIVACY, OPERATIONS, MAINTENANCE, TESTING, RELEASING, PLAN, ROADMAP, REQUESTS (ledger), CHANGELOG, USER_GUIDE, DESIGN_LANGUAGE, PENDING_CHATGPT_PROMPTS, prompts/ (index: `README.md`) |
