# CONTEXT - ClockWeather (read first)

## Product
Widget-first native Android app. The home-screen widget is the product: a big, beautiful, resizable, **transparent** widget (reference: an HTC-Sense-style flip clock over the wallpaper with a dark weather panel below; direction only, not a copy). Shows: flip / digital / analog / minimal clock, weather (current, max/min, "rain expected / sunny"), Gregorian + Hebrew date with holidays, next alarm (from a chosen alarm app, e.g. SmartRing), a rotating banner of today's Outlook events, up to 3 extra time zones, sunrise/sunset, optional steps. The app = live preview + settings + weather drill-down (hourly temp / rain / humidity, up to 4 places). Tapping widget regions opens the matching screen.

## Status (2026-11-19, version 0.1.0 - foundation)
- `:core` complete for batch 1-2 and green (`gradle :core:test`). `:app` written but never compiled (needs CI). Widget art is placeholder until ChatGPT rounds arrive (`docs/PENDING_CHATGPT_PROMPTS.md`).
- Next: `docs/PLAN.md`.

## Decisions (do not re-litigate)
- 2026-11-19 Native Kotlin, widget drawn as one Bitmap via Canvas (not Glance) for pixel-exact flip/analog faces and a transparent plate.
- 2026-11-19 Outlook events come from the device calendar (Outlook for Android syncs into it; read-only `READ_CALENDAR`); no Microsoft login. SmartRing alarm via `AlarmManager.getNextAlarmClock()` + package filter; no change to SmartRing needed.
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
| `app/src/main/java/com/clockweather/app/data/CalendarSource.kt` | Read-only device calendar (Outlook sync) |
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
| `core/src/main/kotlin/com/clockweather/core/sun/SunTimes.kt` | Offline sunrise/sunset (NOAA) |
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
| `core/src/test/kotlin/com/clockweather/core/sun/SunTimesTest.kt` | Sunrise/sunset tests |
| `core/src/test/kotlin/com/clockweather/core/weather/OpenMeteoTest.kt` | Forecast/geocoding parsing, WMO, outlook tests |
| `core/src/test/kotlin/com/clockweather/core/widget/WidgetTest.kt` | Layout, settings sanitising/JSON, palette contrast tests |
| `app/build.gradle.kts`, `settings.gradle.kts`, `gradle/libs.versions.toml` | build (versionName here is the single source) |
| `app/src/main/AndroidManifest.xml` | permissions (each commented), widget receiver, activities |
| `app/src/main/res/*` | strings he/en, widget layout + info, theme, placeholder icon |
| `.github/workflows/*` | CI, latest-APK publish with in-place-update check, API probe |
| `docs/*` | PLAN, REQUESTS (ledger), ROADMAP (suggestions), ARCHITECTURE, TESTING, CHANGELOG, USER_GUIDE, RELEASING, DESIGN_LANGUAGE, PENDING_CHATGPT_PROMPTS, prompts/ |
