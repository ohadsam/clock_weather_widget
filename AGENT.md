# AGENT.md - memory for the next session

## Recently done (2026-11-19, session 1)
- Copied + adapted skills from `ohadsam/distance_analyzer` (`app-development`, release checklist) and `ohadsam/special_events` (`android-native-dev`) into `.claude/skills/`.
- `:core` pure logic with 50+ JVM tests (green locally): Hebrew calendar + holidays + gematria, Open-Meteo parser/URLs + WMO mapping + outlook text, offline sunrise/sunset, world clocks, flip math, widget settings (sanitised JSON), palettes (contrast-tested), layout tiers, agenda rotation, next-alarm indicator, untrusted-text cleaner, repo hygiene tests.
- `:app` first batch written (NOT yet compiled anywhere until CI runs): widget provider + bitmap renderer (4 clock styles, flip animation burst, weather panel, date/holiday line, alarm chip, agenda banner, world clocks, footer), minute tick via exact alarms, WorkManager weather refresh, calendar/alarm/location data sources, Compose app (Home preview, Settings sections, Weather detail), he/en strings.
- ChatGPT prompts 1-4 written (`docs/prompts/`), registered in `docs/PENDING_CHATGPT_PROMPTS.md`.
- CI: `android-ci.yml`, `latest-apk.yml` (in-place update check), `api-probe.yml`.

## Open items (next)
1. Push, read CI, fix `:app` compile errors (first compile ever). Then run `api-probe.yml` and adjust `OpenMeteo` if the shape differs.
2. Owner runs ChatGPT round 1 (weather art) then 4, 2, 3; integrate (validate, key out #FF00FF, drawables, tests).
3. See `docs/PLAN.md` batches 3+: steps (Health Connect), per-region tap zones from LayoutPlan, in-app guide + screenshots from emulator tests (`ui-tests.yml`), What's New, update check + share app, permissions screen, widget configuration per widget id, drill-down screens (clock settings, location).
4. Owner must confirm the holiday rules (REQUESTS R-DOM-1) and the calendar route (device calendar, which already carries Google Calendar).

## Not verifiable here (needs CI or a phone)
Everything in `:app`; widget look on a real launcher and wallpapers; flip burst smoothness and launcher throttling; exact-alarm behaviour; battery; Google Calendar content; Open-Meteo field names; SmartRing `getNextAlarmClock` showing up under its package.
