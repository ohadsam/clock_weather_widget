# Operations: build, release, run, troubleshoot

For whoever builds, ships and supports the app. **A repo test fails if a workflow in `.github/workflows` is not described here.**

## 1. Commands
```
gradle :core:test                       # all pure logic + repo hygiene tests; runs anywhere, ~30 s
./gradlew :app:testDebugUnitTest        # app unit tests (Robolectric renderer tests) - needs the Android SDK
./gradlew :app:lintDebug :app:assembleDebug :app:assembleRelease -PversionCodeOverride=<n>   # CI runs this
```
In the cloud sandbox there is no Android SDK and Google Maven is blocked, so `settings.gradle.kts` includes `:app` only when `ANDROID_HOME`/`ANDROID_SDK_ROOT` or `-PwithApp` is set. **`:app` is compiled and tested only by GitHub Actions.** Never claim `:app` works from reading code: push and read the CI log.

## 2. GitHub Actions workflows
| Workflow | Trigger | Does |
|---|---|---|
| `android-ci.yml` | every push / PR | `:core:test`, `:app:testDebugUnitTest`, `:app:lintDebug`, assemble debug + release, prints the signing certificate, uploads APK artifacts; a failure-summary step greps the log |
| `latest-apk.yml` | every push to `main` | builds the release APK (`versionCode = 100 + commit count`), **verifies it is an in-place update of the published one** (same applicationId and signing certificate, higher versionCode), publishes pre-release `latest` with `ClockWise.apk`, `ClockWise.apk.sha256` and `version.json` |
| `ui-tests.yml` | manual (Run workflow) | boots an API 34 emulator, runs `HomeScreenTest` and `ScreenshotJourneyTest`, uploads the screenshots (`ui-screenshots`) and reports as artifacts; run before a phone test or a release and LOOK at the images |
| `api-probe.yml` | manual | calls Open-Meteo / Hebcal from CI to confirm real API shapes (the sandbox cannot reach them) |
Pushing twice quickly cancels the older CI run (concurrency): that is expected, the newest commit is what counts.

## 3. Release procedure (also `.claude/skills/release-checklist/SKILL.md`)
1. Work on `main` (owner decision 2026-11-19). `gradle :core:test` green.
2. Update docs in the same change (`docs/MAINTENANCE.md` "When you change X, update Y").
3. Bump `appVersionName` in `app/build.gradle.kts` for a feature (minor) or fix (patch) and add `## x.y.z` to `docs/CHANGELOG.md` (a test fails without it).
4. Push; wait for `Android CI` and `Publish latest APK` to be green (read failures with the job logs); verify the release `latest` has the three assets and the new version.
5. Tell the owner honestly what only a phone can prove.

## 4. Signing and in-place updates
`app/sideload.jks` is committed and used for debug **and** release, on purpose (personal sideloaded app): every build installs as an update over the previous one. Never rotate or delete it; never change `applicationId` (`com.clockweather.app`). The in-app updater only installs an APK with the same package, a higher versionCode and the same certificate.
`version.json` (on release `latest`): `{versionName, versionCode, sha, sha256, apk, notes}`; the app fetches it only when "Check for updates" is pressed. The first updater-capable APK must be installed by hand once.

## 5. Runbook: something is wrong on the phone
| Symptom | Check | Fix |
|---|---|---|
| Clock frozen or late | Settings -> Widget status. Exact alarms allowed? Battery optimisation? | "Repair now"; allow exact alarms (Settings -> Permissions); the 15-minute `HeartbeatWorker` also repairs it; re-add the widget as last resort |
| Weather old ("לפני X") | airplane mode? WorkManager restricted by battery saver? | pull to refresh in the weather screen (refresh button); check network; `WeatherWorker` runs every 30 min with a network constraint |
| No events | calendar permission, calendar selection, the account syncing in the phone's calendar app | Settings -> Permissions, Calendar section |
| Alarm not shown | the alarm app must use `AlarmManager.setAlarmClock`; "alarm app" filter in Settings | choose "any alarm app" |
| No countdown / rain notification | notification permission, channel muted in system settings, Do Not Disturb | Settings -> Permissions; test button on the countdown card |
| Update fails | `version.json` exists on release `latest`? "install unknown apps" allowed? same signature? | Settings -> Updates shows the reason; install the APK by hand |
| Widget looks cut / blank | launcher size options; `WidgetRendererTest` covers sizes | resize; report the launcher and size |
| Steps 0 | device has a step counter? activity-recognition permission? | Settings -> What to show -> Steps; first reading of a day starts at 0 |

## 6. Reading CI failures cheaply
`mcp__github__get_job_logs` (or the Actions UI) for the failed job; grep `e: ` (Kotlin errors), `FAILED`, `Lint found`. Lint prints only a count: the workflow prints the report on failure. A Robolectric download flake ("MavenArtifactFetcher") passes on re-run (rerun failed jobs).
