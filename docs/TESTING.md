# Testing

| Layer | Where | Runs |
|---|---|---|
| `:core` unit tests (JUnit4 + kotlin.test): Hebrew calendar round-trips over 30 years, holidays invariants, Open-Meteo parsing with bad inputs, sun times vs an independent NOAA implementation, widget layout/settings sanitising/JSON, palette contrast >= 4.5:1, agenda/alarm, repo hygiene (docs file map, he/en strings parity, :core purity, changelog) | `core/src/test` | **sandbox + CI**: `gradle :core:test` |
| `:app` JVM tests (Robolectric, native graphics): every clock style x 5 sizes x he/en renders, flip frames differ, size cap, transparency | `app/src/test` | CI only |
| Emulator UI tests (Compose): app opens, Settings opens; to grow with a screenshot journey for the user guide (he + en, light + dark, font 1.0/2.0) | `app/src/androidTest` | CI (`ui-tests.yml` to be added in batch 7) |
| API probe | `.github/workflows/api-probe.yml` | manual in CI |

## Full regression at the end of a stage
`gradle :core:test` + CI (app unit tests, lint, assemble) + emulator UI tests with screenshots of every screen and every widget size/style (open the images and LOOK) + usability audit (touch targets 48 dp, contrast over light/dark wallpaper, font 2.0) + live check of the published APK.

## Cannot be proven here
Widget on a real launcher/wallpaper, flip smoothness, exact alarms and battery, permission dialogs, Outlook calendar content, Health Connect, SmartRing alarm appearing under its package.
