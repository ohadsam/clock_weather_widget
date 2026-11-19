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
