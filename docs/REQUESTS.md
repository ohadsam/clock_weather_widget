# Requirements ledger (owner's requests - nothing is dropped)

Status: DONE (verified as noted) · CODE (written, needs CI/phone proof) · ART (waits for ChatGPT) · TODO · CONFIRM (needs owner decision).

| ID | Request (2026-11-19 brief) | Status / where |
|---|---|---|
| R1 | Create the app from the distance_analyzer skills (app-development + release-checklist), adapted | DONE `.claude/skills/*` |
| R2 | Mainly a widget: very beautiful, large, usable; resizable; different designs | CODE `WidgetRenderer`, `WidgetLayout` tiers, 4 styles, 7 palettes, size slider |
| R3 | Illustrations from ChatGPT -> generate prompts | DONE: prompts written; weather art (16) delivered, approved and integrated 2026-11-19; remaining rounds: animations (5), pocket watch (6), icon refinements (2), textures/spot art/previews (4), holiday badges (3) |
| R4 | Transparent background like the reference screenshot | CODE default 100% transparent plate; slider |
| R5 | Big flip-card clock with nice animation | CODE flip burst (8 frames/0.45 s); phone proof needed |
| R6 | Weather with nice animation; auto location | CODE weather panel + coarse location; animated weather art = ART round (see ROADMAP A2) |
| R7 | Connect to alarm; choose which alarm app; show indication | CODE `AlarmSource` + settings chips (any/SmartRing/Google/Samsung) |
| R8 | Auto-connect to our SmartRing app | CODE via system next-alarm (no SmartRing change). CONFIRM: want a deeper SmartRing link (list of alarms, tap opens SmartRing)? needs a change in that repo |
| R9 | Max/min temperature; rain or sunny expected | CODE `WeatherOutlook` + hi/lo |
| R10 | Tap into app for more weather: hourly temp, rain, humidity | CODE `WeatherScreen` (3 days) |
| R11 | Choose another location; up to 3-4 places in the app | CODE 4 places + search + current location |
| R12 | Extra smaller clocks for other time zones | CODE up to 3 (`WorldClock`) |
| R13 | Banner of today's Google Calendar events, rotating with animation | CODE device-calendar route + rotation; animated transition TODO (batch 3). route confirmed 2026-11-19: Google Calendar via the device calendar |
| R14 | Settings window; drill-down per area; buttons to open apps (clock settings, location, weather...) | PARTIAL settings done; per-area deep links to system screens TODO batch 3 |
| R15 | More suggestions from me | DONE `docs/ROADMAP.md` |
| R16 | Design is critical; accuracy is critical | ONGOING release checklist rule 2 + renderer tests + screenshots |
| R17 | Hebrew calendar: Hebrew + Gregorian date; holiday shown prettily | CODE dates + holiday text; badges ART round 3. CONFIRM holiday rules (R-DOM-1) |
| R18 | Daily steps (small, bottom) | TODO batch 3 (Health Connect); renderer slot exists |
| R19 | Sunset / sunrise | CODE footer + offline fallback |
| R20 | Several widget styles/sizes; clock as cards / digital / analog; colours | CODE 4 styles x 7 palettes; more TODO (ROADMAP) |
| R21 | Plan first, then develop well | DONE `docs/PLAN.md` + batches |
| R22 | Native Android | DONE |
| R23 | Use app-development skills: UI tests, user guide, etc. | PARTIAL emulator smoke test + docs; in-app guide + screenshot pipeline TODO batch 3 |
| R24 | Chat prompts for me | DONE `docs/prompts/` |
| R-DOM-1 | Holiday rules (owner-confirmed domain rules) | CONFIRM: implemented as Hebcal-style Israel calendar: Yom HaAtzmaut moves (Thu/Tue), fasts never on Shabbat, Rosh Chodesh, Hanukkah days; diaspora toggle gives second days |
| R25 | A designed, beautiful logo for the app | DONE 2026-11-19: approved after rounds 0/0B/0C; integrated as launcher icon (adaptive + monochrome) and Home header lockup. Open: Round 2 may refine the icon; faint pink fringe on the sun edge (acceptable) |
| R26 | Illustrations polished and invested, with one consistent, beautiful design line | DONE in docs: quality bar + signature (hinge seam + sparkle) in `DESIGN_LANGUAGE.md`, style bible stage in Round 0, every prompt requires the approved reference images |
| R27 | Beautiful, invested animations in the same design line | PROMPT DONE (Round 5: 12-frame seamless loops, motion language); app-side frame player TODO batch 3 (calm mode shows frame 01); flip card + agenda transition animated in code |
| R28 | (owner) "All your suggestions sound great" = every item of `docs/ROADMAP.md` (A1-E20) is approved | TODO by batches: presets DONE (code), the rest scheduled in PLAN batches 4-9 |
| R29 | (owner) In Settings the user chooses what to show and what to hide on the widget | CODE: What-to-show section now covers clock, weather, max/min, outlook, both dates, holidays, alarm, events, extra clocks, sun times, steps. TODO: per-widget choice (batch 6) and element order |
| R30 | (owner) Many kinds of widgets in different sizes, layouts and designs | CODE 5 types (full dashboard, clock, weather, agenda+alarm, slim strip) x 4 clock styles x 7 palettes x free resizing; more types and per-widget settings TODO (batch 6) |
| R31 | (owner) Different designs | CODE 6 one-tap presets (classic, frosted glass, night stand, minimal, sunset analog, Shabbat calm) |
| R32 | (owner) Calendar means Google Calendar, not Outlook | DONE in code and docs (device calendar route) |
| R33 | (owner) Prompts in an organised, copy-ready form | DONE `docs/prompts/00_ALL_PROMPTS_IN_ORDER.md` |
| R34 | (owner) The app is named **ClockWise**: in the logo (wordmark), the prompt, and the app itself | DONE in strings (app + widget names, he/en), docs, CI APK names, Gradle root, Round 0 prompt (logo lockups + wordmark). applicationId stays `com.clockweather.app` on purpose (in-place updates). Logo ART waits for owner |
| R35 | (owner) Flip cards are a DIGITAL clock: digits on cards, never hands (logo card carries digits) | DONE in prompt 0C and design language ("Two clock worlds") |
| R36 (update 2026-11-20: art integrated; lid opens on the hour in code) | (owner) The analog (hands) clock must look completely different: a golden pocket watch whose lid opens, like antique chain watches | CODE: new clock style "Gold pocket watch" with a PLACEHOLDER drawing + preset; PROMPT DONE (Round 6: case, bezels, dials, hands, 12-frame lid-opening, chain); TODO batch 3: load pw_* art, lid-opening animation when the widget is added/tapped, hands rotate in code |
| R27 | (update 2026-11-20) Weather animations | Round 5 delivered 15 loops. 13 integrated (app weather screen loops continuously; widget plays one 1.5 s loop per minute tick while the screen is on; setting "Animate the weather picture"). clear_day and clear_night have no visible motion and stay still. Round 5B prompt asks for 7 stronger redos (clear x2, thunderstorm, mostly_clear x2, partly_cloudy x2) |
| R37 | (owner, 2026-11-20) The widget must run in the background and update at the right moment for every change: clock, events, weather, alarms, Hebrew and Gregorian dates | DONE in code (see ARCHITECTURE "Background updates" table): minute tick re-armed first, alarm-changed and exact-alarm broadcasts, calendar content watcher, weather heartbeat, stale-weather label; Doze/OEM behaviour needs a phone |
| R27b | (update 2026-11-20) Weather loops after rework | 14 of 15 loops integrated with 12 unique frames where reworked; thunderstorm keeps the older clean loop until round 5C |
| R27c | (update 2026-11-20) All 15 weather loops integrated, including the reworked thunderstorm | DONE (needs a phone for smoothness/battery) |
| R38 | (owner, 2026-11-20) Pocket-watch dial numerals: round, decorative, antique-style Arabic numerals, NOT Roman | in prompt 6B; art waits |
| R17b | (update 2026-11-20) Holiday shown prettily | DONE in code: badge + name in the date line (13 painted badges); needs a phone check |

| R39 | (owner, 2026-11-20) Check for updates and install them from inside the app | Code written (Updater, InstallReceiver, `version.json` published by `latest-apk`); needs a phone; first updater APK is installed by hand once |
| R40 | (owner, 2026-11-20) Share the app | Code written: link or the installed APK; needs a phone |
| R41 | (owner, 2026-11-20) In-app daily forecast with hours, split into rain / temperature / humidity, animated | Code written (`WeatherScreen`, `HourChart`, `DayChart` tested in `:core`); needs a phone |
| R42 | (owner, 2026-11-20) More than one place at once inside the app | Code written: swipe pager + chips in the forecast, `PlacesOverview` on Home; needs a phone |
| R43 | (owner, 2026-11-20) Nightfall (tzeit) and candle lighting times | Code: `Zmanim` (core, tested) + footer of the widget + setting `showNightfall`; needs a phone check |
| R44 | (owner, 2026-11-20) Permission check: what is missing | Code: Settings -> Permissions (location, calendar, exact alarms, install updates) with allow / open settings; needs a phone |
| R45 | (owner, 2026-11-20) Step counter | NOT STARTED (Health Connect / step sensor; needs the owner to choose; cannot be verified here) |
| R45b | (owner, 2026-11-20) Step counter: use the device sensor | Code: `StepMath` (core, tested), `StepSource`, `StepWorker` every 15 min, setting + permission; needs a phone (sensor, permission dialog, reboot behaviour) |
| R46 | (owner, 2026-11-20) Pocket watch silver / rose-gold metals (ChatGPT round 6C) | DONE: art integrated by `make-art-assets.py pocket`, setting `pocketMetal` + chips in Settings; needs a phone look |
| R47 | (owner, 2026-11-20) In-app dashboard: widgets/cards with animation and drill-down for calendar, weather, alarms, events etc., links to the source apps, customisable dashboard and widgets | Code: Home is now the dashboard (cards: widget preview + presets, date/holidays, weather, places, alarm, events, sun/nightfall, steps, world clocks); cards slide in, tap opens drill-down, buttons open the Calendar / alarm app; Settings -> Dashboard shows/hides and reorders cards (`Dashboard` in core, tested). Widget customisation = Settings + presets (per-widget-id settings still TODO). Needs a phone |
| R48 | (owner, 2026-11-21) Daylight-saving changes are automatic | DONE in code + tests: clock derived from Instant + system zone every minute; zone-change receiver resets the default zone; needs a phone to see a real change |
| R49 | (owner, 2026-11-21) Verify the central widget updates in real time in the background | Audited: minute alarm re-armed first, RECEIVE_BOOT_COMPLETED was MISSING (added), new `HeartbeatWorker` safety net + `TickState` + Settings -> Widget status (repair button). Real-time behaviour still needs a phone |
| R50 | (owner, 2026-11-21) Tap on each widget item opens the matching app: clock -> our app, date and events -> calendar, weather -> weather app / our weather, alarm -> alarm app, gear -> our Settings | Code: `TapZones` (core, tested) + `WidgetTaps`; weather app choice in Settings (Pixel / Samsung Weather if installed); needs a phone |
| R51 | (owner, 2026-11-21) Setting: choose automatically what the central widget shows, and adapt the app look and layout | Code: `AutoContent` (widget settings by context) + `AutoDashboard` (card order/visibility), switch "Smart content" in Settings; user switches stay the upper limit; tested in core; needs a phone |
| R52 | (owner, 2026-11-21) Dashboard: place picker with tap on a map and view on a map | Code: `PlacePickerScreen` + `OsmMap` (osmdroid / OpenStreetMap; tiles load only after "Show map"; search works without the map); needs a phone (tiles, tap, geocoder name) |
| R53 | (owner, 2026-11-21) Dashboard: more places around the world with weather and current time | Code: `worldPlaces` (max 10) in settings, "Places around the world" card (weather + local time from each forecast zone), drill-down in the weather screen; needs a phone |
| R54 | (found 2026-11-21) Weather was not refreshed when a place was added (old-vs-new comparison bug in `MainActivity.update`) | FIXED |
| R55 | (owner, 2026-11-21) Dashboard widgets: stopwatch (counts up) and countdown (counts down), shown in the central widget while active | Code: `Timers` (core, tested), `TimerStore`, dashboard cards with live seconds and a progress ring, widget row `Section.TIMERS` (minutes only: the widget redraws once a minute), exact alarm + notification when the countdown ends; needs a phone |
| R56 | (agreed suggestion) Rain alert notification | Code: `RainAlert` (core, tested) + `RainAlerts` after each weather refresh; opt-in, needs notification permission; needs a phone |
| R57 | (agreed suggestion) Quiet hours (night mode) | Code: `QuietHours` (core, tested); no flip/lid/weather animation and no rain alert in those hours; needs a phone |
| R58 | (owner, 2026-11-21) More than one stopwatch and countdown, each with a label, pause / cancel / restart, and per-countdown ring and vibration (any combination) | Code: `TimerItem`/`TimerList` (core, tested), list UI in the two dashboard cards, per-timer exact alarm, one notification channel per sound x vibration combination, widget row shows up to 3 labelled timers; needs a phone (ringtones, vibration, insistent ring) |
| R59 | (agreed suggestion) Event countdown: days until a holiday or any date | Code: `DateCountdowns` (core, tested) + card; nearest within 30 days also in the widget row; needs a phone |
| R60 | (agreed suggestion) Seasonal holiday looks | Code: `Season` + 9 festive palettes (colour only, no art) chosen on the holiday itself; painted seasonal ornaments would need ChatGPT art (optional prompt: `docs/prompts/ROUND7_SEASONAL_PROMPT.md`); needs a phone look |
| R61 | (agreed suggestion) Day / night look | Code: `Appearance` (night = after sunset, or 20:00-06:00 without sun times) with a chosen night palette; needs a phone |
| R62 | (agreed suggestion) First-run guide | Code: `GuideDialog` (4 pages), re-show from Settings |
| R63 | (agreed suggestion) Backup, restore and share-my-look | Code: `LookCode` (core, tested): `CW1:` look codes without personal data, `CWB1:` full backup, via share sheet and paste |
| R64 | (agreed suggestion) Named timers (saved presets) | Covered by labelled timers: each timer keeps its label, duration and alert; a separate preset list is NOT built (say if you want it) |
| R65 | (open item, ROADMAP 8) Per-widget settings | Code: `WidgetOverride` (core, tested) + per-widget snapshot + Settings -> My home-screen widgets; unchanged fields follow the global settings; needs a phone |
| R66 | (owner, 2026-11-21) Detailed, maintained docs for operation, maintenance and future improvement | DONE: `README.md` map, ARCHITECTURE, FEATURES, SETTINGS_REFERENCE, DATA_AND_PRIVACY, OPERATIONS, MAINTENANCE, TESTING; `RepoHygieneTest` enforces settings, cards, permissions, hosts, workflows and README links |
| R67 | (owner, 2026-11-21) Special events (birthdays...) from the Special Events app shown in the widget | Code: reads that app's local calendar (account `special_events`) with the existing calendar permission: nearest one in the widget row, a dashboard card, settings; no change needed in the other app except turning on its "export to calendar"; contract in `docs/SPECIAL_EVENTS_INTEGRATION.md`; needs a phone |
| R68 | (owner, 2026-11-21) Holiday ornaments delivered (round 7) | Integrated (9 webp, widget corner + dashboard header on the holiday). Purim ornament needs the colour fix prompt 7B |
| R69 | (owner, 2026-11-21) Hanukkah: the correct picture for each day (1..8 candles + shamash); the evening before already counts | Code: `Season.ornamentKey/effectiveDay` + `orn_hanukkah_<n>` lookup with fallback (tested for all 8 days); the 8 pictures (delivered 2026-11-21, `orn_hanukkah_1..8`) are integrated; Purim colours and the Star of David on the flag fixed too. Open: confirm on the phone that the copied flames sit well on the wicks |
| R70 | (owner, 2026-11-21) Star of David on the flag; Purim mask colours | DONE: art delivered and integrated (cab2148); phone check of the copied Hanukkah flames open |
| R71 | (owner, 2026-11-21) All earlier suggestions implemented | DONE: weekly portion + Omer, UV/wind charts + comfort word + radar link, bedside clock, saved timer presets + phone ringtone picker, Shabbat mode, TalkBack sentence + High-contrast and Big-text presets + reduce-motion, tick lateness log, tablet two-column dashboard. NOT built: interval (work/rest) timer, pollen / air quality (needs another API host), Wear OS tile |
| R72 | (owner, 2026-11-21) Continue the open items | DONE: Havdalah "Shabbat ended" notification (ties to Shabbat mode), air quality / UV / pollen line (opt-in, probe-verified shape). NOT built: interval timer, Wear OS tile, widget-footer air quality |
| R73 | (owner, 2026-11-21) Interval timer (work/rest rounds) | Code: `IntervalPlan` (tested) + chained countdown segments + editor on the countdown card; needs a phone check of the ring/vibration at each switch |
| R74 | (owner, 2026-11-22) Wear OS tile | Code: `:wear` module (DateTileService + tested `DateCardText`), own CI job, `ClockWiseWear.apk` attached to `latest` best-effort. NOT verified on a watch; weather/timers on the watch not built (needs a phone link) |
| R75 | (owner, 2026-11-22) Weather and timers on the watch | Code: `WearState` (core, tested) + `WearSync` (phone) + `StatusTileService` / `DataLayerService` (watch). The wear APK now uses the phone applicationId (Data Layer pairing). Needs a real phone + watch check; read-only (no start/pause from the watch) |
