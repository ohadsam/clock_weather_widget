# ClockWeather - שעון ומזג אוויר

A big, beautiful, resizable, transparent Android home-screen widget: flip-card / digital / analog clock, weather with max/min and rain outlook, Hebrew + Gregorian date with holidays, next alarm (SmartRing or any alarm app), a rotating banner of today's Outlook events, extra time zones, sunrise/sunset. Native Kotlin. Hebrew first, English equal.

Start at `docs/CONTEXT.md`. Plan: `docs/PLAN.md`. Ideas: `docs/ROADMAP.md`. Design prompts for ChatGPT: `docs/prompts/` (status in `docs/PENDING_CHATGPT_PROMPTS.md`). User guide: `docs/USER_GUIDE.md`.

```
gradle :core:test          # pure logic, runs anywhere (also in the cloud sandbox)
./gradlew :app:assembleDebug   # needs the Android SDK (CI does this)
```
