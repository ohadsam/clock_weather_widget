# ClockWeather - architecture decisions for a widget-first native app

(Written 2026-11-19 from the owner's brief; keep in sync with `docs/ARCHITECTURE.md`.)

## Modules
* `:core` - pure Kotlin/JVM, no Android types. Hebrew calendar + holidays, weather model/parsing/WMO mapping, sun times, time zones (`java.time`), widget layout tiers, agenda rotation, alarm selection, settings model + sanitising. **Runs `gradle :core:test` in the sandbox** -> all logic is proven before CI.
* `:app` - Android: Compose app (home preview, drill-downs, settings), widget providers, data sources (Open-Meteo, location, calendar, alarms, steps), WorkManager, DataStore. Compiled and tested only by CI.

## Widget technology (why)
* **Glance is not used for the clock face**: flip cards, analog hands and rich typography are drawn on a `Canvas` into a `Bitmap` (`WidgetRenderer`) and shown in a `RemoteViews` `ImageView`. This gives pixel-exact, themeable, size-adaptive faces (flip / digital / analog / minimal) that Glance cannot do.
* **Transparent background** is a first-class setting (alpha 0..100 %): the bitmap is drawn with a transparent canvas; text gets an optional soft shadow for legibility on any wallpaper.
* **Live time without waking the app every second**: minute boundaries are scheduled with `AlarmManager.setExactAndAllowWhileIdle` only while the screen is on (`ACTION_SCREEN_ON/OFF` receiver); flip animation = a short burst of pre-rendered frames pushed with `updateAppWidget` at ~16 fps for <= 0.5 s (rate-limited, falls back to a crossfade `ViewFlipper` when frames are dropped).
* **Size adaptivity**: `onAppWidgetOptionsChanged` gives min/max width/height -> `WidgetLayoutMath.tierFor(w,h)` (core, tested) chooses what is shown (clock only, + weather, + hi/lo, + agenda banner, + world clocks, + footer). The owner can override with a user scale (50-150 %) and a style preset.
* **Click targets**: each region (clock, weather, alarm chip, agenda banner, world clock) has its own `PendingIntent` deep link: clock -> clock settings / system clock app; weather -> day detail; alarm -> chosen alarm app; agenda -> calendar app.

## Data sources (all opt-out-able, named in Settings -> Privacy)
| Need | Source | Notes |
|---|---|---|
| Weather + hourly + sun times | Open-Meteo (no key) | probe workflow before shipping; cache 30 min; offline = last good data with "updated X ago" |
| Place search | Open-Meteo geocoding | up to 4 saved places, "current location" optional (coarse permission) |
| Hebrew date + holidays | **local algorithm in `:core`** | no network; holiday rules confirmed with the owner (`docs/REQUESTS.md` R-DOM-*) |
| Alarms | `AlarmManager.getNextAlarmClock()` (works with SmartRing `setAlarmClock`, any alarm app) + optional deep link to SmartRing | the "which alarm app" setting filters by `showIntent` creator package |
| Calendar (Outlook) | `CalendarContract` provider (Outlook Android syncs into it) - read-only, `READ_CALENDAR`, user picks which calendars | no Microsoft login/Graph needed |
| Steps | Health Connect (`READ_STEPS`) preferred, `TYPE_STEP_COUNTER` fallback | permission + explained in-app |

## Design assets
Everything drawn (weather illustrations, card faces, backgrounds, app icon) is commissioned from ChatGPT with the prompts in `docs/prompts/`, listed in `docs/PENDING_CHATGPT_PROMPTS.md`. Asset naming contract: `docs/DESIGN_LANGUAGE.md` section "Asset contract".
