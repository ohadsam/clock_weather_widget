# Architecture

```
:app (Android)                                   :core (pure Kotlin/JVM)
MainActivity -> ui/* (Compose)  ---------+       hebrew/  weather/  sun/  clock/  widget/  agenda/  alarm/  text/
ClockWidgetProvider -> Snapshot ---------+--->  WidgetSettings, WidgetLayout, Agenda, AlarmIndicator,
   -> WidgetRenderer (Canvas -> Bitmap)  |       Holidays, HebrewDateFormat, OpenMeteo parser, FlipMath ...
data/* (SettingsStore, WeatherRepository,|
 CalendarSource, AlarmSource, Location,  |       Rules: no android.* in :core; time is a parameter;
 Net, WeatherWorker)                     |       :core never reads files or network.
```
* **Dependency direction**: `:app` -> `:core` only. UI -> Snapshot/data -> core. The renderer decides nothing: WHAT to show comes from `WidgetLayout.plan`, `Agenda`, `AlarmIndicator`, `Holidays`.
* **Parity**: the home-screen widget and the in-app preview call the same `WidgetRenderer.render` with the same `RenderState`.
* **Live clock**: `TickScheduler` sets an exact alarm for the next minute; `ClockWidgetProvider.tick` pushes 8 pre-rendered flip frames (0.45 s) when the screen is on and "calm animations" is off, else one frame. Re-armed after boot/time/zone/locale change/app update.
* **Weather**: `WeatherWorker` (30 min, network constraint) -> `WeatherRepository` -> raw JSON cached -> parsed in `:core`; the widget never waits for the network.
* **Network**: `Net.get` is the only entry; HTTPS, hosts `api.open-meteo.com` and `geocoding-api.open-meteo.com`, 10 s timeouts, 512 KB cap, no redirects. `INTERNET` is declared only for this.
* **Permissions**: INTERNET (weather), ACCESS_COARSE_LOCATION (optional "my location"), READ_CALENDAR (optional events), SCHEDULE_EXACT_ALARM<=32 / USE_EXACT_ALARM (minute flip). No notifications, no background location, no accounts.
* **Exported components**: the widget receiver (system must deliver widget broadcasts), the launcher activity, the widget configure activity. PendingIntents are immutable.
* **Persistence**: SharedPreferences JSON (settings, forecast cache). No database yet; if one is added, Room needs a Migration per version.

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
| Weather, sun times | new forecast | `WeatherWorker` every 30 min (network constraint) and on place/settings change; the widget never waits for the network | <= 30 min; stale data is labelled "לפני X" after 90 min |
| Settings changes | the user | `ClockWidgetProvider.updateAll` right after saving | immediate |
| Resize / add widget | launcher | `onAppWidgetOptionsChanged`, `onUpdate`, `onEnabled` | immediate |
Self-repair: `WeatherWorker` (every 30 min) also re-arms the minute tick and the calendar watcher, and `updatePeriodMillis` (30 min) calls `onUpdate`, which re-arms the tick too, so a lost chain heals itself. The schedule math is in `:core` (`TickMath`, tested); what only a phone can prove is Doze/OEM battery-saver behaviour.


## Updates, sharing and charts (2026-11-20)
- `latest-apk` publishes `ClockWise.apk`, `ClockWise.apk.sha256` and `version.json` on release `latest`. `Updater` fetches `version.json` only when the user presses the button, downloads only from github.com / *.githubusercontent.com (`UpdateMath.validUrl`), checks SHA-256, package, version, signing certificate, then installs with `PackageInstaller` (result in `InstallReceiver`).
- Share: link text, or the installed APK through a `FileProvider` (`cache/share`).
- Forecast charts: `DayChart` (core, pure) builds the series; `HourChart` (Compose Canvas) animates it. The pager in `WeatherScreen` shows each saved place.
