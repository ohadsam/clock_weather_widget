# Plan (batches; one at a time; never start a batch on red CI)

| # | Batch | Status |
|---|---|---|
| 0 | Skills + docs + prompts (this session) | DONE |
| 1 | `:core` logic with tests (Hebrew calendar, weather, sun, clocks, settings, layout, agenda, alarm) | DONE (all green locally) |
| 2 | `:app` foundation: Gradle/CI, widget renderer + provider, tick/flip, data sources, app screens, strings | CODE written -> **first CI compile pending** |
| 2b | Fix CI findings, run `api-probe.yml`, fix API shape, first APK to the owner's phone | CI green on every push since 2026-11-20; APK published as `latest`; waiting for the owner's phone test |
| 3 | Integrate ChatGPT art (round 6 pocket watch: dials/bezels/hands drawn from assets, lid-opening animation, chain) in order 0 (logo + style bible) -> 1 (weather) -> 5 (animations: add a frame player to `WeatherArt`, widget plays a short loop only while the screen is on, calm mode = frame 01) -> 2 (icon) -> 4 -> 3 | waits for owner |
| 4 | Per-region tap zones from `LayoutPlan`; deep links (system clock settings, location, calendar app, SmartRing); animated agenda transition + weather micro-animations | TODO |
| 5 | Steps (Health Connect, fallback step counter), permissions screen + start-up sheet | TODO |
| 6 | Per-widget settings (widget id) incl. per-widget show/hide and element order; more widget types; backup/restore of settings + share-my-look code | DONE except element order (per-widget look + show/hide, backup, look codes, 5 widget types) |
| 7 | In-app user guide + screenshots generated from emulator tests (`ui-tests.yml`), What's New, About, check for updates, share the app | TODO |
| 8 | Icon (round 2), holiday badges (round 3), polish, full regression, release 1.0 | TODO |
| 9 | All remaining ROADMAP items (approved): living weather, auto day/night palette, fonts, seasonal skins, Shabbat times + parasha + Omer, countdowns, rain alert, AQI/UV, compare places, SmartRing alarm list, steps ring, accessibility presets... | TODO, in the order the owner prefers |

Each batch ends with the release checklist and a FULL regression (`docs/TESTING.md`).
