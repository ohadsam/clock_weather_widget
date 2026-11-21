# Architecture

How ClockWise is built, why it is built that way, and where to change what. Read `docs/CONTEXT.md` first for the status and the file map; this file is the design.

## 1. Shape of the system
ClockWise is a **widget-first** native Android app (Kotlin, Compose, Material 3, `minSdk 26`, `targetSdk 35`). The home-screen widget is one bitmap drawn on a Canvas and pushed into a `RemoteViews`; the app is a **dashboard** of cards, a settings area and some drill-down screens. Everything the widget can show is also visible and configurable in the app (parity rule).

```
:app (Android, only built by CI)                      :core (pure Kotlin/JVM, tested everywhere)
MainActivity -> ui/*  (Compose screens)  ----+        hebrew/    calendar, holidays, badges, date text
ClockWidgetProvider -> Snapshot  -----------+---->   weather/   Open-Meteo parsing, outlook, charts, rain alert
   -> WidgetRenderer (Canvas -> Bitmap)      |        sun/       sunrise/sunset, nightfall, candle lighting
   -> WidgetTaps (PendingIntents per zone)   |        clock/     flip digits, world clocks, tick maths, widget health
data/*  workers, receivers, sources, stores  |        widget/    WidgetSettings, layout plan, tap zones, palettes, seasons, presets
                                             |        agenda/ alarm/ steps/ timers/ dashboard/ auto/ look/ update/ text/
Rules: :app -> :core only. No android.* in :core. Time is always a parameter. :core never touches files or the network.
```
Why two modules: everything decidable without Android lives in `:core` so it is unit-tested in seconds in a sandbox with no Android SDK. `:app` is thin glue (sources, drawing, UI) and is verified by GitHub Actions only.

## 2. Packages in `:core` (what each owns)
| Package | Owns |
|---|---|
| `hebrew` | `HebrewCalendar` (conversion, 30-year round-trip tests), `Holidays.on(date, inIsrael)`, `HolidayBadge` (which painted badge), `HebrewDateFormat`, `Gematria` |
| `weather` | `OpenMeteo` (URL builders, defensive JSON parser, WMO -> `Sky`), `Forecast` models, `WeatherOutlook` text, `DayChart` (series for the charts), `RainAlert`, `PlaceIds`, `WeatherAnim` (frame tables) |
| `sun` | `SunTimes` (NOAA maths), `Zmanim` (nightfall, candle lighting, Shabbat/holiday eve) |
| `clock` | `FlipMath`, `WorldClock`, `TickMath` (next minute boundary), `Freshness` (stale-weather label), `WidgetHealth` |
| `widget` | `WidgetSettings` (the one settings object + `sanitized()`), `WidgetLayout.plan` (which sections fit), `TapZones`, `Palettes`/`Season`/`Appearance`, `WidgetKind` + `Preset`, `QuietHours`, `PocketMetal`, generated `PocketWatchGeometry` |
| `agenda`, `alarm` | event filtering/banner text, next-alarm visibility/label |
| `steps`, `timers` | daily steps from a cumulative counter; stopwatch / countdown / labelled timer list / date countdowns |
| `dashboard`, `auto` | dashboard card ids, order and visibility; "smart content" rules for the widget and the dashboard order |
| `look` | share-my-look code and full backup text |
| `update`, `text` | update manifest parsing and URL rules, share text; `Untrusted.clean` for any external string |

## 3. Packages in `:app`
| Package | Owns |
|---|---|
| `widget` | `ClockWidgetProvider` (+4 subclasses, one per widget type), `Snapshot` (gathers data into a `RenderState`), `WidgetRenderer` (all drawing), `WidgetTaps`, `TickScheduler`, `WeatherArt`/`ArtCache` (painted art), `RenderState` |
| `data` | sources (`AlarmSource`, `CalendarSource`, `LocationSource`, `StepSource`), stores (`SettingsStore`, `TimerStore`, `TickState`), workers (`WeatherWorker`, `HeartbeatWorker`, `StepWorker`, `CalendarChangeWorker`), alarms/notifications (`CountdownAlarm`, `Notifier`, `RainAlerts`), `Net`, `Updater`/`InstallReceiver`/`AppSharer`, `SourceApps` (opens other apps) |
| `ui` | `HomeScreen` (the dashboard) + `DashboardTiles`/`TimerTiles`/`DateCountdownTile`/`WidgetTile`/`PlacesOverview`, drill-downs (`DashboardDetails`, `WeatherScreen`, `PlacePickerScreen`), `SettingsScreen` and its sections, `HourChart`, `OsmMap`, `GuideDialog` |

## 4. The two main data flows
**Widget (every minute and on events).** Trigger (minute alarm, broadcast, worker, settings change) -> `ClockWidgetProvider.pushAll` -> `Snapshot.build` reads settings + cached forecast + alarm + agenda + steps + timers and applies smart content, night/season palette -> `WidgetLayout.plan(size, settings)` chooses sections -> `WidgetRenderer.render` draws one Bitmap -> `TapZones.rows(plan)` + `WidgetTaps` attach what each part opens -> `AppWidgetManager.updateAppWidget`. The widget never waits for the network: it draws what is cached.

**App (dashboard).** `HomeScreen` rebuilds a `RenderState` every 15 s with `Snapshot.build(forDashboard = true)` (agenda, alarm and steps are loaded even if the widget hides them), asks `Dashboard`/`AutoDashboard` for the cards, and each card is a plain composable. Changes call `SettingsStore.save` then `ClockWidgetProvider.updateAll`, so the widget follows immediately.

## 5. Rendering
One `Bitmap` per widget id sized to the widget (never larger than the RemoteViews memory limit), drawn by `WidgetRenderer`: sections stacked top to bottom by `WidgetLayout.plan` (the clock section is the flexible one). Clock styles: flip cards (8 pre-rendered frames in a 0.45 s burst each minute while the screen is on), digital, minimal, plain analog, painted golden pocket watch (lid opens on the hour). Weather art is painted PNG/WebP loops (`WeatherArt`); until an asset exists a code-drawn placeholder is used. Text always has a soft shadow so it reads over any wallpaper. The settings gear and tap rows are separate invisible views over the image (see `widget_clock.xml`).

## 6. Persistence
SharedPreferences only (no database): `settings` (one sanitised JSON), `forecast` (raw Open-Meteo JSON per place + fetch time), `timers` (JSON list + date countdowns), `tick`, `steps`, `rain_alert`, `update`, `guide`. Details and privacy in `docs/DATA_AND_PRIVACY.md`. If a database is ever added, Room needs a `Migration` per version and exported schemas.

## 7. Network and permissions (summary; full table in `docs/DATA_AND_PRIVACY.md`)
Network hosts: Open-Meteo (weather, city search), GitHub (update check, only when pressed), OpenStreetMap tiles (map, only after "Show map"). `Net.get` is the single entry for weather: HTTPS, host allow-list, 10 s timeouts, 512 KB cap, no redirects; `Updater` has its own redirect-validated download. Every permission is optional.

## 8. Security decisions
Exported components: only the widget receiver (system delivers widget broadcasts), the launcher activity and the widget configure activity. Every other receiver/provider is `exported=false`. All PendingIntents are `FLAG_IMMUTABLE` except the PackageInstaller result (the system must fill it; explicit component, our package). External strings (calendar text, place names, pasted codes) go through `Untrusted.clean` or `sanitized()`. Updates verify SHA-256, package name, higher versionCode and the same signing certificate before install. The sideload key is committed on purpose (personal app, stable in-place updates).

## Background updates - what wakes the widget (owner requirement, 2026-11-20)
The widget must stay correct with the app closed. Every piece of shown data has a trigger; if you add data, add a row.
| Data on the widget | What changes it | Trigger that redraws it | Worst-case delay |
|---|---|---|---|
| Clock, Gregorian date, Hebrew date, holiday, "in 25 min" texts | time passing; midnight; sunset (Hebrew day) | exact alarm on every minute boundary (`TickScheduler`, re-armed first thing in each tick) | <= 1 min (Doze can defer a few min while the screen is off; fires when the device wakes) |
| Time, zone, locale, DST | user or network changes them | `TIME_SET`, `TIMEZONE_CHANGED`, `LOCALE_CHANGED` receivers | immediate |
| After reboot / app update | process gone | `BOOT_COMPLETED`, `MY_PACKAGE_REPLACED` re-arm the tick and redraw | immediate |
| Exact-alarm permission toggled | Settings | `SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED` re-arms | immediate |
| Next alarm (SmartRing or any app) | an alarm is added, edited, snoozed, fired | `NEXT_ALARM_CLOCK_CHANGED` broadcast, plus the minute tick as a safety net | immediate, <= 1 min if the broadcast is not delivered |
| Calendar events | an event is added/moved/deleted or a sync finishes | `CalendarChangeWorker` (WorkManager content-URI trigger on `CalendarContract`), plus the tick (agenda cache 3 min) | ~2-30 s |
| Event banner rotation | time | minute tick picks the event from the clock | <= 1 min |
| The clock itself (safety net) | lost alarm chain, process death, reboot | `HeartbeatWorker` every 15 min (no network) re-arms the tick and redraws if the last tick (`TickState`) is over 2 min old; `RECEIVE_BOOT_COMPLETED` + `MY_PACKAGE_REPLACED` re-arm after reboot/update; Settings -> Widget status shows `WidgetHealth` and has a repair button | <= 15 min worst case, normally < 1 min |
| Seasonal / night look | the date (holiday), sunset, sunrise | derived at every render in `Snapshot` (`Appearance`), so the minute tick applies it; no extra trigger | <= 1 min |
| Countdown end | the end time | one exact `RTC_WAKEUP` alarm (`CountdownAlarm`) -> `CountdownReceiver` marks it finished, notifies, redraws the widget; re-armed after reboot/update | on time (exact-alarm permission) |
| Stopwatch / countdown row in the widget | minute tick, start/pause/reset in the app | derived from stored timestamps at each minute tick (`TimerStore`); minutes only | <= 1 min |
| Rain alert | new forecast | `RainAlerts.check` after every `WeatherWorker` run (every 30 min); at most one notification per 6 h; muted in quiet hours | <= 30 min |
| Daylight-saving / time-zone change | zone rules (automatic: time is always `Instant` + `ZoneId.systemDefault()`), `TIME_SET`, `TIMEZONE_CHANGED` | the tick reads the zone every minute; the receiver resets the process default zone on a zone/time change; tested for Israel 2026 changes in `TapZonesAndHealthTest` | <= 1 min |
| Steps today | walking; midnight; reboot | `StepWorker` every 15 min (no network) reads the step counter; `StepMath` handles day change and reboot | <= 15 min |
| Nightfall, candle lighting | date change; place change; clock/time-zone change | computed offline (`Zmanim`) on every minute tick, so no extra trigger | <= 1 min |
| Weather, sun times | new forecast | `WeatherWorker` every 30 min (network constraint) and on place/settings change; the widget never waits for the network | <= 30 min; stale data is labelled "לפני X" after 90 min |
| Settings changes | the user | `ClockWidgetProvider.updateAll` right after saving | immediate |
| Resize / add widget | launcher | `onAppWidgetOptionsChanged`, `onUpdate`, `onEnabled` | immediate |
Self-repair: `WeatherWorker` (every 30 min) also re-arms the minute tick and the calendar watcher, and `updatePeriodMillis` (30 min) calls `onUpdate`, which re-arms the tick too, so a lost chain heals itself. The schedule math is in `:core` (`TickMath`, tested); what only a phone can prove is Doze/OEM battery-saver behaviour.

## 9. Known limitations (do not re-report as new)
- A home-screen widget cannot animate natively: the flip, lid and weather loops are short bursts of pre-rendered frames; some launchers throttle them. The widget redraws once a minute, so timers show minutes only.
- The APK is signed with the committed sideload key, not for the Play Store.
- `:app` cannot be compiled in the cloud sandbox (no Android SDK); CI is the only compile/lint/test gate for it.
- Everything visual or timing-related still needs a real phone (see `AGENT.md` "Not verifiable here").
