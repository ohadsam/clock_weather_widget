# Testing

| Layer | Where | Runs | Covers |
|---|---|---|---|
| `:core` unit tests (JUnit4 + kotlin.test, ~125 tests) | `core/src/test` | **sandbox + CI**: `gradle :core:test` | Hebrew calendar round-trips over 30 years, holidays and badges, Open-Meteo parsing with bad inputs, sun/nightfall maths vs an independent NOAA implementation, layout plans, settings sanitising + JSON, palette contrast >= 4.5:1, agenda/alarm, steps maths (reboot-safe), timers/countdowns/date countdowns, rain alert, quiet hours, smart content, tap zones, widget health, daylight-saving clock behaviour (Israel 2026), look codes and backups, update manifest rules, chart series |
| Repo hygiene (also in `:core:test`) | `core/.../hygiene/RepoHygieneTest.kt` | sandbox + CI | docs file map, he/en string parity, `:core` purity (no Android imports, no clock reads), changelog section for the version, prompts registered, and the doc-completeness checks listed in `docs/MAINTENANCE.md` section 4 |
| `:app` JVM tests (Robolectric, native graphics) | `app/src/test` | CI only | every clock style x 5 sizes x he/en renders non-blank, flip frames differ, size cap, transparency |
| Lint | `:app:lintDebug` | CI only | Android lint (errors abort the build) |
| Emulator UI tests (Compose) | `app/src/androidTest` | manual: `ui-tests.yml` (Run workflow) | app opens, first-run guide skipped, Settings opens; `ScreenshotJourneyTest` saves screenshots of the main screens as an artifact (he + en, light + dark, font 2.0 variants still to add) |
| API probe | `.github/workflows/api-probe.yml` | manual in CI | real Open-Meteo / Hebcal response shapes |

## How to add tests
New `:core` logic gets a test next to it in the same commit (time passed as a parameter, so no mocking). A fixed bug gets a regression test. New UI flows get an emulator test once `ui-tests.yml` exists. Do not weaken a failing hygiene test: fix the doc or the code.

## Full regression at the end of a stage
`gradle :core:test` + all CI jobs + (when available) emulator tests with screenshots of every screen and every widget size/style, he + en, light + dark, font 1.0/2.0; open the images and LOOK; usability audit (touch targets 48 dp, contrast over light and dark wallpapers); live check of the published APK.

## Cannot be proven in the sandbox or by CI
The widget on a real launcher and wallpaper, flip/lid/weather animation smoothness, exact-alarm timing and battery, permission dialogs, notification sound and vibration, Google Calendar content, SmartRing alarms appearing under their package, the OpenStreetMap map and tap, the installer prompt of the in-app update, the step sensor. These are listed in `AGENT.md` -> "Not verifiable here" and must be said plainly in every report.
