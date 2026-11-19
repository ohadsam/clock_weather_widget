# Requirements ledger (owner's requests - nothing is dropped)

Status: DONE (verified as noted) · CODE (written, needs CI/phone proof) · ART (waits for ChatGPT) · TODO · CONFIRM (needs owner decision).

| ID | Request (2026-11-19 brief) | Status / where |
|---|---|---|
| R1 | Create the app from the distance_analyzer skills (app-development + release-checklist), adapted | DONE `.claude/skills/*` |
| R2 | Mainly a widget: very beautiful, large, usable; resizable; different designs | CODE `WidgetRenderer`, `WidgetLayout` tiers, 4 styles, 7 palettes, size slider |
| R3 | Illustrations from ChatGPT -> generate prompts | DONE prompts 1-4; ART waits for owner |
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
| R25 | A designed, beautiful logo for the app | PROMPT DONE (Round 0); ART waits for owner; icon (Round 2) derives from it |
| R26 | Illustrations polished and invested, with one consistent, beautiful design line | DONE in docs: quality bar + signature (hinge seam + sparkle) in `DESIGN_LANGUAGE.md`, style bible stage in Round 0, every prompt requires the approved reference images |
| R27 | Beautiful, invested animations in the same design line | PROMPT DONE (Round 5: 12-frame seamless loops, motion language); app-side frame player TODO batch 3 (calm mode shows frame 01); flip card + agenda transition animated in code |
| R28 | (owner) "All your suggestions sound great" = every item of `docs/ROADMAP.md` (A1-E20) is approved | TODO by batches: presets DONE (code), the rest scheduled in PLAN batches 4-9 |
| R29 | (owner) In Settings the user chooses what to show and what to hide on the widget | CODE: What-to-show section now covers clock, weather, max/min, outlook, both dates, holidays, alarm, events, extra clocks, sun times, steps. TODO: per-widget choice (batch 6) and element order |
| R30 | (owner) Many kinds of widgets in different sizes, layouts and designs | CODE 5 types (full dashboard, clock, weather, agenda+alarm, slim strip) x 4 clock styles x 7 palettes x free resizing; more types and per-widget settings TODO (batch 6) |
| R31 | (owner) Different designs | CODE 6 one-tap presets (classic, frosted glass, night stand, minimal, sunset analog, Shabbat calm) |
| R32 | (owner) Calendar means Google Calendar, not Outlook | DONE in code and docs (device calendar route) |
| R33 | (owner) Prompts in an organised, copy-ready form | DONE `docs/prompts/00_ALL_PROMPTS_IN_ORDER.md` |

