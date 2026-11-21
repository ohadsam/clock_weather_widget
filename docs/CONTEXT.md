# CONTEXT - ClockWise (read first)

## Product
Widget-first native Android app. The home-screen widget is the product: a big, beautiful, resizable, **transparent** widget (reference: an HTC-Sense-style flip clock over the wallpaper with a dark weather panel below; direction only, not a copy). Shows: flip / digital / analog / minimal clock, weather (current, max/min, "rain expected / sunny"), Gregorian + Hebrew date with holidays, next alarm (from a chosen alarm app, e.g. SmartRing), a rotating banner of today's Google Calendar events, up to 3 extra time zones, sunrise/sunset, optional steps. The app = live preview + settings + weather drill-down (hourly temp / rain / humidity, up to 4 places). Tapping widget regions opens the matching screen.

## Status (2026-11-19, version 0.1.0 - foundation)
- `:core` complete for batch 1-2 and green (`gradle :core:test`). `:app` written but never compiled (needs CI). Widget art is placeholder until ChatGPT rounds arrive (`docs/PENDING_CHATGPT_PROMPTS.md`).
- Next: `docs/PLAN.md`.

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
| `app/src/androidTest/java/com/clockweather/app/HomeScreenTest.kt` | Emulator smoke test |
| `app/src/main/java/com/clockweather/app/MainActivity.kt` | App entry + state navigation |
| `app/src/main/java/com/clockweather/app/WidgetConfigActivity.kt` | Launcher configure hook for the widget |
| `app/src/main/java/com/clockweather/app/data/AlarmSource.kt` | System next alarm (getNextAlarmClock) |
| `app/src/main/java/com/clockweather/app/data/CalendarSource.kt` | Read-only device calendar (Google Calendar sync) |
| `app/src/main/java/com/clockweather/app/data/LocationSource.kt` | Coarse last-known location |
| `app/src/main/java/com/clockweather/app/data/Net.kt` | The only network entry (HTTPS allow-list, size cap) |
| `app/src/main/java/com/clockweather/app/data/SettingsStore.kt` | Settings persistence |
| `app/src/main/java/com/clockweather/app/data/WeatherRepository.kt` | Forecast fetch + cache + place search |
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
| `core/src/main/kotlin/com/clockweather/core/hebrew/HebrewDateFormat.kt` | Hebrew date text + roll at sunset |
| `core/src/main/kotlin/com/clockweather/core/hebrew/Holidays.kt` | Jewish holidays/special days for a Gregorian day (Israel/diaspora) |
| `core/src/main/kotlin/com/clockweather/core/steps/StepMath.kt` | Daily steps from a cumulative counter (reboot-safe) |
| `core/src/main/kotlin/com/clockweather/core/dashboard/Dashboard.kt` | Dashboard cards: ids, order and visibility maths |
| `core/src/main/kotlin/com/clockweather/core/widget/TapZones.kt` | Which section each tap row of the widget opens (from the layout plan) |
| `core/src/main/kotlin/com/clockweather/core/clock/WidgetHealth.kt` | Verdict: is the minute tick alive? |
| `core/src/main/kotlin/com/clockweather/core/auto/AutoContent.kt` | Smart content rules for the widget and the dashboard order |
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
| `app/src/main/java/com/clockweather/app/data/TickState.kt` | When the minute tick last ran |
| `app/src/main/java/com/clockweather/app/data/HeartbeatWorker.kt` | 15-minute safety net that re-arms the tick |
| `app/src/main/java/com/clockweather/app/widget/WidgetTaps.kt` | What each widget tap zone opens (calendar, alarm, weather app, our screens) |
| `app/src/main/java/com/clockweather/app/ui/WidgetStatusSection.kt` | Settings: is the clock tick alive, repair button |
| `app/src/main/java/com/clockweather/app/ui/PlacesOverview.kt` | All saved places at a glance on Home |
| `app/build.gradle.kts`, `settings.gradle.kts`, `gradle/libs.versions.toml` | build (versionName here is the single source) |
| `app/src/main/AndroidManifest.xml` | permissions (each commented), widget receiver, activities |
| `app/src/main/res/*` | strings he/en, widget layout + info, theme, placeholder icon |
| `.github/workflows/*` | CI, latest-APK publish with in-place-update check, API probe |
| `docs/*` | PLAN, REQUESTS (ledger), ROADMAP (suggestions), ARCHITECTURE, TESTING, CHANGELOG, USER_GUIDE, RELEASING, DESIGN_LANGUAGE, PENDING_CHATGPT_PROMPTS, prompts/ |
