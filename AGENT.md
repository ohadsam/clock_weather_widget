# AGENT.md - memory for the next session

## Recently done (2026-11-20/21, sessions 2-3)
Everything in `docs/CHANGELOG.md` 0.2.0, ledger rows R39-R64. Release checklist run on 2026-11-21 by reading the code (see "Review 2026-11-21" below). Honest gaps: no emulator UI tests with screenshots, no per-widget settings, no `/simplify` or `/code-review` tool run, nothing verified on a phone.

## Review 2026-11-21 (release checklist, by reading)
- Architecture: new logic is in `:core` with tests (timers, date countdowns, look codes, seasons, tap zones, health, rain alert, quiet hours, auto content); `:app` only reads sources and draws. One known wart: `WidgetSettings.timerRowActive` is a derived field (documented, reset by `sanitized()`).
- QA: fixed by review - timer card changes now apply to the latest stored timer (no stale overwrite), `MainActivity.update` compared new settings with themselves (weather not refreshed on place change), `RECEIVE_BOOT_COMPLETED` was missing.
- UI/UX: touch targets >= 48 dp on the new controls; cards respect calm animations; every new string exists in he and en (hygiene test). Not checked: font scale 2.0, RTL mirroring of the new screens, contrast over real wallpapers (needs a phone/emulator).
- Security: notifications and alarms use immutable PendingIntents and non-exported receivers; backup/look codes are decoded defensively (size caps, sanitised); the backup text contains places (warned in the UI); map tiles load only after the user presses "Show map" (disclosed in Privacy).
- Performance: timer cards decode the stored list 4x per second only while visible; widget redraw is still once per minute; no new wake locks (one `RTC_WAKEUP` alarm per running countdown).

## Recently done (2026-11-19, session 1)
- Copied + adapted skills from `ohadsam/distance_analyzer` (`app-development`, release checklist) and `ohadsam/special_events` (`android-native-dev`) into `.claude/skills/`.
- `:core` pure logic with 50+ JVM tests (green locally): Hebrew calendar + holidays + gematria, Open-Meteo parser/URLs + WMO mapping + outlook text, offline sunrise/sunset, world clocks, flip math, widget settings (sanitised JSON), palettes (contrast-tested), layout tiers, agenda rotation, next-alarm indicator, untrusted-text cleaner, repo hygiene tests.
- `:app` first batch written (NOT yet compiled anywhere until CI runs): widget provider + bitmap renderer (4 clock styles, flip animation burst, weather panel, date/holiday line, alarm chip, agenda banner, world clocks, footer), minute tick via exact alarms, WorkManager weather refresh, calendar/alarm/location data sources, Compose app (Home preview, Settings sections, Weather detail), he/en strings.
- ChatGPT prompts 1-4 written (`docs/prompts/`), registered in `docs/PENDING_CHATGPT_PROMPTS.md`.
- CI: `android-ci.yml`, `latest-apk.yml` (in-place update check), `api-probe.yml`.

## Logo (2026-11-19)
Approved logo integrated: `scripts/make-art-assets.py logo` keys the magenta sheets in `design/logo/originals/` into `design/logo/*.png` and writes `app/src/main/res/drawable-nodpi/{ic_launcher_fg,ic_launcher_mono,brand_lockup_light,brand_lockup_dark}.png`. The old placeholder icon is removed. Not yet compiled in CI at the time of writing.

## Weather art (2026-11-19)
16 approved illustrations are in `app/src/main/res/drawable-nodpi/wx_*.webp` (made by `scripts/make-art-assets.py weather` from `design/weather/originals/`). `WeatherArt` already loads them by name; the code-drawn placeholder is only the fallback. Light clouds are pale on a light wallpaper (soft blue shade keeps them visible) - check on a phone.

## Weather animations (2026-11-20)
15 loops integrated (6 reworked with 12 unique frames, sparkle dimming repaired by `repair_dim_sparkles`; thunderstorm reworked in round 5C): `res/drawable-nodpi/wxa_<sky>_<day|night>_<k>.webp` (unique frames, 320 px) + generated `WeatherAnimTable.kt`; made by `python3 -I scripts/make-art-assets.py anim <dir with the 180 ChatGPT PNGs>` (the ChatGPT originals, ~40 MB, are NOT in git; ask the owner for the zips again if a re-run is needed). `WeatherArt.draw(animMs)` plays them; the widget plays one loop per minute tick (setting animateWeather); WeatherScreen loops continuously. Not verified on a phone: launcher throttling of 8 fps updates, battery.

## Pocket watch (2026-11-20)
Painted art integrated: `pw_case`, `pw_open_cream|ivory|black`, `pw_hand_*` in drawable-nodpi, geometry in generated `PocketWatchGeometry`. `WidgetRenderer.drawPocketWatch` draws open face + live hands + crown/bow, and squashes the lower case art towards the hinge for the lid animation (plays on the hour from `ClockWidgetProvider.playLidOpening`). Not compiled/verified on a phone yet. Optional later: silver/rose bezels, chain.

## UI art (2026-11-20)
`python3 -I scripts/make-art-assets.py ui` keys `design/ui/originals` into `drawable-nodpi/preview_*.webp` and `spot_*.webp`. previewImage set on the 5 widget infos; spots used in Home/Weather/Settings empty states.

## Holiday badges (2026-11-20)
13 painted badges in `drawable-nodpi/hol_*.webp` (from `design/holidays/originals` via `make-art-assets.py holidays`); `HolidayBadge` (core) maps holiday ids to them; `drawDateLine` draws the badge before the text.

## Updates, share, forecast charts (2026-11-20)
Written, NOT yet verified by CI or a phone. The first APK with the updater must be installed by hand; the updater needs `version.json`, which exists only after the next `latest-apk` publish.

## Review of updater/share (2026-11-20, by reading the code; CI green on 206d811)
Checked: downloads only from allowed hosts with every redirect re-validated; size caps; SHA-256, package, higher versionCode and same signing certificate checked before install; `InstallReceiver` not exported with an explicit mutable PendingIntent (required by PackageInstaller); FileProvider exposes only `cache/share`. Nothing blocking. Still needs a phone: the installer prompt, "install unknown apps" flow, share sheet.

## Open items (next)
1. Push, read CI, fix `:app` compile errors (first compile ever). Then run `api-probe.yml` and adjust `OpenMeteo` if the shape differs.
2. Owner runs ChatGPT round 1 (weather art) then 4, 2, 3; integrate (validate, key out #FF00FF, drawables, tests).
3. See `docs/PLAN.md` batches 3+: steps (Health Connect), per-region tap zones from LayoutPlan, in-app guide + screenshots from emulator tests (`ui-tests.yml`), What's New, update check + share app, permissions screen, widget configuration per widget id, drill-down screens (clock settings, location).
4. Owner must confirm the holiday rules (REQUESTS R-DOM-1) and the calendar route (device calendar, which already carries Google Calendar).

## Not verifiable here (needs CI or a phone)
Everything in `:app`; widget look on a real launcher and wallpapers; flip burst smoothness and launcher throttling; exact-alarm behaviour; battery; Google Calendar content; Open-Meteo field names; SmartRing `getNextAlarmClock` showing up under its package.
