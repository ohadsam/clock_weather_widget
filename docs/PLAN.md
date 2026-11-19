# Plan (batches; one at a time; never start a batch on red CI)

| # | Batch | Status |
|---|---|---|
| 0 | Skills + docs + prompts (this session) | DONE |
| 1 | `:core` logic with tests (Hebrew calendar, weather, sun, clocks, settings, layout, agenda, alarm) | DONE (all green locally) |
| 2 | `:app` foundation: Gradle/CI, widget renderer + provider, tick/flip, data sources, app screens, strings | CODE written -> **first CI compile pending** |
| 2b | Fix CI findings, run `api-probe.yml`, fix API shape, first APK to the owner's phone | NEXT |
| 3 | Integrate ChatGPT art in order 0 (logo + style bible) -> 1 (weather) -> 5 (animations: add a frame player to `WeatherArt`, widget plays a short loop only while the screen is on, calm mode = frame 01) -> 2 (icon) -> 4 -> 3 | waits for owner |
| 4 | Per-region tap zones from `LayoutPlan`; deep links (system clock settings, location, calendar app, SmartRing); animated agenda transition + weather micro-animations | TODO |
| 5 | Steps (Health Connect, fallback step counter), permissions screen + start-up sheet | TODO |
| 6 | Per-widget settings (widget id) + presets ("Classic HTC", "Minimal", "Night"); backup/restore of settings | TODO |
| 7 | In-app user guide + screenshots generated from emulator tests (`ui-tests.yml`), What's New, About, check for updates, share the app | TODO |
| 8 | Icon (round 2), holiday badges (round 3), polish, full regression, release 1.0 | TODO |
| 9 | Ideas from `docs/ROADMAP.md` chosen by the owner | later |

Each batch ends with the release checklist and a FULL regression (`docs/TESTING.md`).
