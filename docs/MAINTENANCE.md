# Maintenance guide: how to change the app safely

Rules of the road for future work (human or AI). Start with `docs/CONTEXT.md`. **Docs are part of the code: a change is not done until the docs it touches are updated in the same commit.** Tests enforce part of this (see section 4).

## 1. When you change X, update Y
| You changed | Update |
|---|---|
| any source file added/removed/renamed | `docs/CONTEXT.md` file map (test enforced) |
| a `WidgetSettings` field | `docs/SETTINGS_REFERENCE.md` (test enforced), `sanitized()`, settings UI + both string files, `docs/USER_GUIDE.md` |
| a dashboard card (`DashTile`) | `docs/FEATURES.md` card table (test enforced), `HomeScreen` `when`, `AutoContent`/`AutoDashboard` filters, strings, user guide |
| a permission in the manifest | `docs/DATA_AND_PRIVACY.md` (test enforced), `PermissionsSection` row, strings he+en, privacy text |
| a network host | `docs/DATA_AND_PRIVACY.md` (test enforced), privacy strings, `docs/ARCHITECTURE.md` section 7 |
| data the widget shows | the background table in `docs/ARCHITECTURE.md` (a trigger for it!) |
| a user-visible feature | `docs/FEATURES.md`, `docs/USER_GUIDE.md`, `docs/REQUESTS.md` (ledger row), `docs/CHANGELOG.md` |
| a workflow in `.github/workflows` | `docs/OPERATIONS.md` (test enforced) |
| version | `app/build.gradle.kts` + `## x.y.z` in `docs/CHANGELOG.md` (test enforced) |
| a new ChatGPT art need | prompt in `docs/prompts/`, row in `docs/PENDING_CHATGPT_PROMPTS.md` (test enforced), restate `docs/DESIGN_LANGUAGE.md` |
| something learned or left open | `AGENT.md` (recent / open / not verifiable) and `docs/PLAN.md` |

## 2. Recipes
**Add a setting.** Field with default in `WidgetSettings` (KDoc) -> clamp/validate in `sanitized()` -> use it in `Snapshot`/`WidgetRenderer`/UI -> switch/slider in `SettingsScreen` (strings he + en) -> row in `SETTINGS_REFERENCE.md` -> test in `core` (sanitising + behaviour) -> user guide line.
**Add a dashboard card.** `DashTile` enum entry (id never renamed) -> composable in `DashboardTiles.kt` (use `DashCard`) -> `when` in `HomeScreen` -> `AutoContent`/`AutoDashboard` rule -> drill-down in `DashboardDetails.kt` if any -> `FEATURES.md` row -> strings.
**Add something to the widget.** New `Section` in `WidgetLayout` (cost, order, `FLEX_ORDER`/`STACK_ORDER`) -> draw it in `WidgetRenderer` -> `TapTarget` + `TapZones.targetOf` + `WidgetTaps` -> `AutoContent` rule -> background trigger row -> tests (`WidgetTest`, `TapZonesAndHealthTest`). Every `when` over `Section`/`TapTarget`/`DashTile` must stay exhaustive: grep before pushing.
**Add a widget type.** `WidgetKind` entry + `shape()`; provider subclass in `ClockWidgetProvider.kt` + `PROVIDERS` list; manifest receiver; its own `res/xml/widget_<type>_info.xml` (a test requires a unique info file per receiver); picker name strings.
**Add a palette / season.** `Palettes.ALL` (offered) or `SEASONAL`; contrast tests must pass (>= 4.5:1).
**Add a string.** `values/strings.xml` (Hebrew, default) AND `values-en/strings.xml`, same placeholders; unescaped apostrophes break the build (`\'`).
**Add a permission or network call.** Prefer none. If needed: manifest comment, `PermissionsSection` row, `DATA_AND_PRIVACY.md`, privacy strings, host allow-list in `Net`, and ask the owner first when it changes the privacy promise.
**Add a background trigger.** Row in the ARCHITECTURE table; code in `data/`; a heartbeat/repair path so a lost trigger heals itself; keep `:core` pure and test the decision logic.
**Add art.** Follow the "Design assets come from ChatGPT" rule in `.claude/skills/release-checklist/SKILL.md`; integrate with `scripts/make-art-assets.py <command>`; originals go in `design/<set>/originals`.
**Upgrade dependencies.** Versions live in `gradle/libs.versions.toml` and `app/build.gradle.kts` plugins; upgrade one family at a time (Kotlin + Compose compiler plugin together), push, read CI.

## 3. Engineering rules (from `CLAUDE.md` and the release checklist)
- `:core` is pure: no `android.*`, no clock reads (`Instant.now()` etc. are banned by a test), no I/O. Time, zone and place are parameters.
- Reuse before writing; small single-purpose files with a one-line header on what they do and must not touch.
- Anything external is untrusted: sanitise at the edge.
- Defaults protect people: quiet, private, opt-in for anything that reads personal data or notifies.
- Hebrew first, English equal, RTL safe, targets >= 48 dp, contrast >= 4.5:1.
- Honesty: say what was verified how (sandbox `:core`, CI `:app`, needs a phone). Never claim success from reading code.

## 4. What the repo tests enforce about docs (`RepoHygieneTest`)
Every source file is in the `CONTEXT.md` file map; he/en string parity and placeholders; every used string exists; `:core` has no Android imports and no clock reads; the version has a changelog section; every prompt in `docs/prompts` is listed in `PENDING_CHATGPT_PROMPTS.md`; every `WidgetSettings` field is in `SETTINGS_REFERENCE.md`; every dashboard card id is in `FEATURES.md`; every manifest permission and every network host in the code is in `DATA_AND_PRIVACY.md`; every workflow is in `OPERATIONS.md`; every `docs/*.md` is linked from `README.md`; XML is well formed; each widget receiver has its own info file. If a test fails, fix the doc, do not weaken the test.

## 5. Keeping the docs alive (process)
1. Every task ends with the release checklist (`.claude/skills/release-checklist/SKILL.md`), whose "Docs" step names the files above.
2. `AGENT.md` always has: recently done, open items, not verifiable. Update it before the last commit of a session.
3. `docs/REQUESTS.md` is the owner's ledger: nothing is dropped silently; each request has a status.
4. A stale doc is a bug: fix it when you find it, even if it is not your change.
