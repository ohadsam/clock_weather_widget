# ClockWise

A big, beautiful, resizable, transparent Android home-screen widget plus a dashboard app: flip-card / digital / minimal / analog / golden pocket-watch clock, weather with animated art and hourly charts, Hebrew + Gregorian date with holidays, next alarm (SmartRing or any alarm app), today's Google Calendar events, world clocks and places on a map, sunrise/sunset/nightfall/candle lighting, steps, several labelled stopwatches and countdowns with their own ringtone/vibration, days-until-event cards, festive and night looks, smart content, backup and look sharing, in-app updates. Native Kotlin and Compose. Hebrew first (RTL), English equal. Everything stays on the device.

## Documentation map
| Read this | For |
|---|---|
| `docs/CONTEXT.md` | **start here**: status, file map, rules |
| `docs/ARCHITECTURE.md` | how it is built: modules, data flows, rendering, background processes, security |
| `docs/FEATURES.md` | every feature with its code, settings and tests |
| `docs/SETTINGS_REFERENCE.md` | every setting, default and meaning |
| `docs/DATA_AND_PRIVACY.md` | permissions, network hosts, stored data |
| `docs/OPERATIONS.md` | build, CI, release, signing, in-place updates, runbook |
| `docs/MAINTENANCE.md` | how to change things safely, "when you change X update Y", recipes |
| `docs/TESTING.md` | test layers, what is covered, what only a phone proves |
| `docs/RELEASING.md` | short release summary |
| `docs/PLAN.md` | what is next |
| `docs/ROADMAP.md` | ideas and suggestions |
| `docs/REQUESTS.md` | the owner's request ledger (nothing is dropped silently) |
| `docs/CHANGELOG.md` | what changed per version |
| `docs/USER_GUIDE.md` | the user-facing guide |
| `docs/DESIGN_LANGUAGE.md` | the visual style that every asset must follow |
| `docs/PENDING_CHATGPT_PROMPTS.md` and `docs/prompts/` | art prompts for the owner's ChatGPT and their status |
| `AGENT.md` | memory for the next session: recent, open, not verifiable |
| `CLAUDE.md` | instructions for AI sessions |

## Quick start
```
gradle :core:test                  # pure logic + repo hygiene tests; runs anywhere (also in the cloud sandbox)
./gradlew :app:assembleDebug       # needs the Android SDK; GitHub Actions does this on every push
```
Every push to `main` builds and publishes the signed APK as the pre-release `latest` (in-place update of the previous build).
