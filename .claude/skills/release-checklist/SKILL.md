---
name: release-checklist
description: Run this after implementing any feature or fix in ClockWeather (ohadsam/clock_weather_widget), before considering the work done. Covers the owner's standing rules, the 5 specialist reviews (architecture, QA, UI/UX + RTL, security, performance), version bump, What's New, user guide, docs, skills, tests, CI as the Android verification gate, and shipping to main. Invoke by name ("run the release checklist") or whenever the user asks to finish/ship/wrap up a change here.
---

# Release checklist - ClockWeather

(Adapted 2026-11-19 from `ohadsam/distance_analyzer` `.claude/skills/release-checklist` and its generic copy in `app-development/references/release-checklist.md`; web/PWA steps replaced with native-Android equivalents.)

ClockWeather = a **fully native Kotlin Android app, widget-first**: a very beautiful, large, resizable, transparent home-screen widget (flip-card / digital / analog clock, weather, Hebrew + Gregorian date, alarm, Google-calendar banner, world clocks, steps, sun times) plus an app for settings and drill-downs. Two Gradle modules: `:core` (pure Kotlin/JVM logic - **tested in the sandbox**) and `:app` (Android - compiled/tested only by GitHub Actions). Method details: `.claude/skills/app-development/` (+ `references/native-widget-app.md`) and `.claude/skills/android-native-dev/`.
Do not skip or reorder steps. If a step finds nothing, say so explicitly and move on.

**First:** confirm the previous batch's checklist really finished (task list, `docs/PLAN.md`) - a green `main` and a half-finished checklist look identical.

## 0. The owner's standing rules - every change must satisfy these (say how in the report)
1. **Audience first.** One owner-family audience: people of all ages glancing at the phone's home screen, often one-handed, in sunlight, in Hebrew. The widget must be readable in one second; settings are few per screen, grouped and collapsed; big targets (>= 48 dp); short kind wording. Weather/health numbers are general information, never advice.
2. **UI/UX: simple, accessible, minimal - and beautiful.** The owner called design "critical" and accuracy "critical": compare every changed screen and widget size against the design language; transparent background must look right on light AND dark wallpapers. Controls grouped by topic; settings collapsed with a one-line summary per section; one primary action per screen; every screen explains itself.
3. **Parity widget = in-app preview = settings.** Everything the widget shows is shown identically in the in-app live preview, and every widget option is a setting with a default. Anything that must work with the app closed (clock tick, refresh, alarm indication, calendar banner rotation) lives in `:app` background code, with its logic in `:core` and a JVM unit test.
4. **Hebrew first, English equal.** Every string in `values/` (he) and `values-en/`; RTL-safe (`start/end`, mirrored directional icons); number + space + unit (`25 °C` rendered per locale convention, `5 ק"מ`); font scale to 200 % does not break the app; the widget respects RTL (clock digits stay LTR).
5. **Defaults protect people.** Quiet and private: location is coarse and optional (a fixed city works without any permission), calendar/steps/alarm reading are opt-in with in-app explanations, nothing leaves the device except the named weather/geocoding requests, which are listed in Settings -> Privacy and can be turned off (then the widget shows clock/date only).
6. **Tests are the proof.** `:core` logic -> JVM unit tests (run locally: `gradle :core:test`); `:app` -> JVM unit tests (Robolectric where needed) + Compose UI tests on the emulator with screenshots of every screen in he + en, light + dark, font scale 1.0 and 2.0; widget rendering has golden/renderer tests (non-blank bitmap, sizes, tiers). Every new flow has a test; every fixed bug a regression test. Accessibility checks (contentDescription, 48 dp, contrast) run in the UI tests.
7. **Requirements ledger.** `docs/REQUESTS.md` lists every request/suggestion and its status. Before finishing: add new requests, tick what shipped, walk the whole list - nothing silently dropped (if something cannot be done, say why).
8. **Honesty.** Say what was verified how: sandbox (`:core` tests) / CI (`:app` compile, lint, emulator tests) / needs a real phone (widget visuals on a real launcher, permissions dialogs, exact-alarm timing, Health Connect, Google Calendar sync). Never claim an external API shape without the probe workflow output; never claim a library fits without checking licence and size.
9. **Efficiency and code quality.** Reuse before writing (grep first); small single-purpose files (< ~300 lines) with a one-line header saying what they are for and what they must not touch; `:core` has no Android imports (enforced by a test); names say what a thing is; no dead code. Token-efficient work: read only what the change touches, edit rather than rewrite, short reports.
10. **Design assets come from ChatGPT, via exact prompts.** Weather illustrations, card faces, backgrounds, app icon and animations are generated by the owner's ChatGPT, never improvised. At every design need: (a) write a precise self-contained prompt into `docs/prompts/<NAME>_PROMPT.md` (fenced block, audience/principles, style system + colour tokens, size/format rules the app validates, staged delivery "wait for continue", original-work rule, motion safety, validation/report section); (b) restate `docs/DESIGN_LANGUAGE.md` in every prompt; (c) list it in `docs/PENDING_CHATGPT_PROMPTS.md` with what it blocks; (d) give the prompt to the owner in the reply and stop building that part (placeholders only); (e) when results arrive validate (names, sizes, transparency without halos, contrast), look at contact sheets, integrate, update the docs.
11. **Keep the context docs current.** Behaviour/structure/data/test/decision changes update `docs/CONTEXT.md`, `docs/ARCHITECTURE.md`, `docs/TESTING.md`, `docs/CHANGELOG.md`, `docs/REQUESTS.md`, `docs/PLAN.md`, `AGENT.md` (recently done / open / not verifiable) in the same change, so a fresh session continues from the docs alone. `:core` tests include a docs test that every source file is in the file map in `docs/CONTEXT.md`.
12. **In-place updates, always.** Same `applicationId` and the same committed signing key forever; `versionCode` strictly increasing; Room migrations (never destructive in release); DataStore keys only added. CI compares the new APK with the published one.

## 1. Five reviews - separate passes, in this order
Re-read every changed file fresh for each pass; each pass catches different bugs.
1. **System architecture.** `:app` -> `:core`, never the reverse; UI -> ViewModel -> repository -> data source; no business logic in composables or widget providers (they call `:core`). Time is always a parameter (`Clock`/`Instant`/`ZoneId`) - nothing in `:core` calls `now()`. New persisted field -> sanitiser + default; old data still loads; Room version bump + `Migration` + exported schema. Sealed classes: grep every `when` before pushing. Update `docs/ARCHITECTURE.md` if a boundary moved.
2. **QA / functional.** Walk the journeys for what changed, he + en: first launch (no permissions, offline), add widget -> resize through every tier, change style/colour/transparency, change place / add 4th place / no GPS, alarm app chosen / none / alarm in the past, calendar with 0/1/many events and all-day events, Hebrew date around sunset and Rosh Chodesh/holidays/Shabbat, steps permission denied, time-zone change, DST day, device reboot (widget re-arms), airplane mode (stale-data label), upgrade from previous version's data.
3. **UI / UX + RTL.** Touch targets >= 48 dp; one primary action; contentDescription on every icon; contrast AA in light/dark and **over transparent backgrounds** (text shadow/scrim verified on a light and dark wallpaper test image); font scale 2.0; reduce-motion / "calm animations" setting respected; RTL mirroring; compare screenshots from the UI tests (he + en) - actually open and look at them.
4. **Security & privacy.** Permissions minimal and explained (`ACCESS_COARSE_LOCATION`, `READ_CALENDAR`, `ACTIVITY_RECOGNITION`/Health Connect `READ_STEPS`, `POST_NOTIFICATIONS`, `SCHEDULE_EXACT_ALARM` only if needed); `INTERNET` justified in `docs/ARCHITECTURE.md` (weather + geocoding only); HTTPS only, no cleartext, response size caps, JSON parsed defensively; exported components only the widget providers and the launcher activity; PendingIntents `FLAG_IMMUTABLE`; calendar text is untrusted (length-clamped, control/bidi chars stripped); no personal data in logs/exports without opt-in; no secrets in the repo (the committed sideload keystore is the documented exception). Run the `security-review` skill on the diff.
5. **Performance & battery.** Widget updates are timestamp-derived (no drift), bitmaps sized to the widget (never larger than the RemoteViews memory limit), recycled; animation bursts <= 0.5 s and only while the screen is on and the widget visible; network refresh <= every 30 min with backoff and WorkManager constraints; no wake locks; cache everything the widget needs so it never blocks on the network; APK size tracked in CI.

Also run `/simplify` and `/code-review` on the diff. Fix findings **before** moving on; re-run the affected pass for non-trivial fixes. Write what each pass found (even "nothing") in the final report.

## 2. Version + What's New
`versionName` bumped per release (feature -> minor, fix -> patch) in `app/build.gradle.kts`; `versionCode = 100 + git commit count`. Add `## x.y.z` to `docs/CHANGELOG.md` (a `:core` test fails without it); the in-app What's New dialog shows that section (he + en) after an update.

## 3. User guide - always current
Every user-visible change updates `docs/USER_GUIDE.md` **and** the in-app guide (he + en; a test checks both languages have every title and every screenshot file exists). Screenshots come from the emulator UI tests (`ui-tests.yml`), never by hand. New setting/screen/feature => guide text, even one sentence.

## 4. Docs
`README.md` (features, screenshots if UI changed), `docs/ARCHITECTURE.md`, `docs/RELEASING.md` if the pipeline changed. New strings in **both** `values/` and `values-en/` (a unit test checks key parity and `%s`/`%d` placeholders).

## 5. Skills
If this run revealed a missing step or a wrong claim here or in `app-development`/`android-native-dev`, fix the file now (self-review of the checklist) and tell the owner.

## 6. Tests - green before pushing
```
gradle :core:test            # sandbox: all pure logic, docs file-map test, i18n parity test, changelog test
# CI only (no Android SDK here): :app:testDebugUnitTest, lintDebug, ktlint/detekt, assembleDebug/Release, emulator UI tests + screenshots
```
New logic => unit test with the code; new flow => UI test; fixed bug => regression test. Kotlin cannot be compiled for `:app` in the sandbox - read the diff by eye, push, read the CI log (`mcp__github__get_job_logs`, grep `e: `/`FAILED`), never claim success from eyeballing. After a `/simplify` pass that deletes "dead" Kotlin, grep every deleted name first.
Known Kotlin gotchas: block comments nest (never write `/*` inside KDoc); unescaped apostrophe in strings.xml; KSP flake `Expected start of the object` = re-run; lint prints only a count (print the report file).

## 6b. Updates, permissions, background - verify every release
1. Same `applicationId`, same signing certificate (CI step compares with the published APK), higher `versionCode`.
2. Settings -> "Check for updates" / "Share the app" (APK + install text via FileProvider, `AppSharer` refuses a stale versionCode) still work; What's New shows after an update; About shows the real version.
3. **Permissions**: every permission is listed in Settings -> Permissions with state (granted / ask / blocked / unsupported) and an Allow / Open-settings button; start-up sheet asks for missing required ones once per session; a new permission => manifest comment + row + both languages + test.
4. **Every shown datum has a background trigger** (docs/ARCHITECTURE.md "Background updates" table): a new datum gets a row and a trigger in the same change. **Widget survives**: reboot, app update, launcher restart, time-zone/clock change, battery saver (shows last good data, never a blank card).
5. After the push verify the real thing: CI run green (build + tests + lint), release asset exists with `app.apk` + `.sha256` and the new version, `latest-apk` comparison step passed.
6. Say plainly what only a phone proves: widget look on the real launcher and wallpaper, resize behaviour, permission dialogs, exact-alarm timing, Google Calendar sync content, Health Connect steps, the installer/update-over-install.

## 6c. End of a big stage - FULL regression with screenshots
At the end of every stage (feature batch, asset integration, refactor, before a phone test): `gradle :core:test` + all CI jobs + emulator UI tests with screenshots of every screen and every widget tier (he + en, light + dark, font 1.0/2.0, light and dark wallpaper) + usability audit; open the contact sheets and LOOK; regenerate guide images. Report counts, findings table (empty or explained) and what needs a phone.

## 7. Android verification = CI
`:app` is only compiled by GitHub Actions (`android-ci.yml`: ktlint, detekt, lint, unit tests, assemble; `ui-tests.yml`: emulator + screenshots; `latest-apk.yml`: release). Push, wait (use `send_later` for the re-check, do not poll), read failures by grepping the log. Never start a batch on red CI.

## 8. Ship
Commit on the working branch, push, wait for CI green. On `main`, the release job publishes the APK; verify release + asset + version. Don't claim "done" before that.

## Known limitations (do not re-report as new)
- The APK is signed with a committed sideload key (stable in-place updates, not secret); not for the Play Store.
- Sandbox cannot reach Open-Meteo/Hebcal or Google Maven; API shapes are confirmed by probe workflows; `:app` by CI.
- Home-screen widgets cannot animate natively: the flip is a short burst of pre-rendered frames; some launchers throttle it (a crossfade fallback exists).

## Done means
All five reviews run and fixes applied - version bumped - CHANGELOG + user guide + context docs + ledger updated - `:core` tests green locally - CI green on the branch - (on main) release verified - report states what needs a phone.
