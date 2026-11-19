# ClockWise - instructions for Claude

Native Android (Kotlin) **widget-first** app: a very beautiful, large, resizable, transparent home-screen widget (flip-card / digital / analog / minimal clock, weather, Hebrew + Gregorian date and holidays, next alarm, Google-calendar banner, world clocks, sun times) plus an app for live preview, settings and weather drill-down. Hebrew first (RTL), English equal. Owner writes Hebrew: reply in Hebrew, short; code, commits and docs in English.

Start with `docs/CONTEXT.md` (status, file map, rules) -> `docs/PLAN.md` (next batch) -> `docs/REQUESTS.md` (owner's ledger) -> `AGENT.md` (recent / open / not verifiable). Then follow `.claude/skills/release-checklist/SKILL.md` for every change.
The method is in `.claude/skills/app-development/` (read "Project overrides" at the top of its SKILL.md and `references/native-widget-app.md`) and `.claude/skills/android-native-dev/` (CI-gated native workflow).

## Facts you must not re-derive
- Two Gradle modules. `:core` = pure Kotlin/JVM (no Android imports, time always a parameter) and is built and tested **in the sandbox**: `gradle :core:test`. `:app` needs the Android SDK, which the sandbox lacks (Google Maven blocked) -> `:app` is compiled/tested only by GitHub Actions (`android-ci.yml`). `settings.gradle.kts` includes `:app` only when `ANDROID_HOME`/`ANDROID_SDK_ROOT` or `-PwithApp` is set.
- Open-Meteo / Hebcal are unreachable from the sandbox: confirm API shapes with `.github/workflows/api-probe.yml`, never assume.
- Design assets (weather art, card textures, icon, holiday badges, spot art) come from the owner's ChatGPT by prompts in `docs/prompts/`; until delivered `WeatherArt` draws placeholders and loads `res/drawable-nodpi/wx_<sky>_<day|night>.png` automatically when it exists.
- Same `applicationId` + the committed sideload key (`app/sideload.jks`) forever: every build is an in-place update.
- Never claim `:app` works from reading code: push, read the CI log.

## Rules in one breath
Audience first, beautiful + simple UI, widget = preview = settings parity, Hebrew/English/RTL, private safe defaults, tests prove it, requirements ledger never loses an item, honesty about what only a phone proves, reuse before writing, ChatGPT-made art via exact prompts, docs current in the same change.
