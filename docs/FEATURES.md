# Features reference

What the app does, where the code is, which setting controls it and how it is tested. For the owner-facing description see `docs/USER_GUIDE.md`; for design see `docs/ARCHITECTURE.md`. **A repo test fails if a dashboard card id is missing from the table in section 2 - add a row in the same change as a new card.**

## 1. The home-screen widget
Five widget types (picker entries): classic (everything), clock only, weather, agenda (events + alarm), strip (one slim row). All share `ClockWidgetProvider`; the type only changes `WidgetKind.shape(settings)`.

| Feature | Code | Settings | Tests |
|---|---|---|---|
| Clock styles: flip cards, digital, minimal, plain analog, golden pocket watch (gold / silver / rose gold, lid opens on the hour) | `WidgetRenderer.drawClock/drawFlip/drawPocketWatch`, `FlipMath`, `PocketWatchGeometry` | `clockStyle`, `pocketMetal`, `use24h`, `showAmPm`, `scalePct` | `WidgetRendererTest`, `FlipMath` tests, `PocketWatchGeometryTest` |
| Transparent plate, palettes, presets | `Palettes`, `Preset` | `paletteId`, `backgroundAlphaPct`, `panelAlphaPct`, `cornerRadiusDp` | palette contrast test |
| Weather panel: animated painted illustration, now, max/min, rain/sunny outlook, stale label | `WeatherArt`, `WeatherOutlook`, `Freshness` | `showWeather`, `showHiLo`, `showOutlook`, `fahrenheit`, `rainLikelyFromPct`, `animateWeather` | `OpenMeteoTest`, `WeatherAnimTest` |
| Date line: Gregorian + Hebrew date (rolls at sunset), holiday with painted badge | `HebrewCalendar`, `Holidays`, `HolidayBadge` | `showGregorianDate`, `showHebrewDate`, `showHolidays`, `rollHebrewAtSunset`, `inIsrael` | `HebrewCalendarTest`, `HolidayBadgeTest` |
| Next alarm (SmartRing or any alarm app) | `AlarmSource`, `AlarmIndicator` | `showAlarm`, `alarmPackage`, `alarmShowWithinHours` | `AgendaAlarmTest` |
| Today's events banner (device calendar, so Google Calendar) | `CalendarSource`, `Agenda`, `CalendarChangeWorker` | `showAgenda`, `agendaCalendarIds`, `agendaRotateSeconds`, `agendaIncludeAllDay` | `AgendaAlarmTest` |
| World clocks row | `WorldClock` | `showWorldClocks`, `worldClocks` | `ClockTest` |
| Footer: sunrise/sunset, nightfall, candle lighting, steps | `SunTimes`, `Zmanim`, `StepSource`, `StepMath` | `showSunTimes`, `showNightfall`, `showSteps` | `SunTimesTest`, `ZmanimTest`, `StepMathTest` |
| Timers row (up to 3 labelled timers + nearest date countdown) | `TimerList.widgetLines`, `DateCountdowns.widgetLine`, `drawTimers` | `showTimers` | `TimersTest`, `TimerItemsTest` |
| Tap zones: clock -> app, date/events -> calendar, weather -> weather app or our weather screen, alarm -> alarm app, footer -> sun screen, gear -> Settings | `TapZones`, `WidgetTaps`, `widget_clock.xml` | `showSettingsButton`, `weatherAppPackage` | `TapZonesAndHealthTest` |
| Smart content (show only what matters now) | `AutoContent.apply` in `Snapshot` | `autoContent` | `AutoContentTest` |
| Festive holiday looks, automatic night look | `Season`, `Appearance` | `seasonalTheme`, `autoDayNight`, `nightPaletteId` | `TimerItemsTest` (seasons) |
| Quiet hours (no animation, no alerts) | `QuietHours`, tick in `ClockWidgetProvider` | `quietHoursEnabled`, `quietFromHour`, `quietToHour` | `TimersTest` |
| Calm animations | checks in tick | `calmAnimations` | - |

## 2. The in-app dashboard (home screen)
`HomeScreen` shows the cards chosen and ordered in Settings -> Dashboard (`Dashboard`, `DashboardEditor`). Cards slide in (skipped in calm mode); tap = drill-down; most have a button to the source app (`SourceApps`).

| Card id | What it shows | Code | Drill-down |
|---|---|---|---|
| `widget` | live widget preview (same renderer), size/wallpaper switches, style, presets, "add widget" | `WidgetTile` | - |
| `date` | Gregorian + Hebrew date, holiday | `DateTile` | `DashDetailScreen(DATE)`: upcoming holidays |
| `weather` | animated icon, now, max/min, rain chance | `WeatherTile` | `WeatherScreen` (pager over places, animated hourly charts: temperature / rain / humidity) |
| `places` | every saved place at a glance (when 2+) | `PlacesOverview` | `WeatherScreen` at that place |
| `alarm` | next alarm | `AlarmTile` | `DashDetailScreen(ALARM)` + open the alarm app |
| `stopwatch` | any number of labelled stopwatches (start/pause, restart, cancel) | `TimerTiles` | - |
| `countdown` | any number of labelled countdowns, ring progress, per-timer sound (alarm / notification / phone ringtone / none) + vibration, test button | `TimerTiles`, `CountdownAlarm`, `Notifier` | - |
| `date_countdown` | days until a holiday or a chosen date | `DateCountdownTile` | - |
| `agenda` | today's events | `AgendaTile` | `DashDetailScreen(AGENDA)` + open calendar / event |
| `sun` | sunrise, sunset, nightfall, candle lighting | `SunTile` | `DashDetailScreen(SUN)`: 7 days |
| `steps` | today's steps (animated count) | `StepsTile` | - |
| `world` | world clocks | `WorldTile` | - |
| `world_places` | weather + local time for up to 10 places around the world | `WorldPlacesTile` | `WeatherScreen` |
| `map` | summary + "open map" | `MapTile` | `PlacePickerScreen` |

## 3. Places, map, weather
- Saved places (max 4) feed the widget; world places (max 10) feed the dashboard. Search by name (Open-Meteo geocoding) or tap on the OpenStreetMap map (`PlacePickerScreen`, `OsmMap`); the picker previews weather and the local time before saving. The map loads only after "Show map". Names for tapped points come from the phone's `Geocoder`, else coordinates (`PlaceIds.coordLabel`).
- `WeatherWorker` refreshes every saved + world place every 30 minutes.

## 4. Alerts and timers
- Countdown end: one exact `RTC_WAKEUP` alarm per running countdown -> `CountdownReceiver` -> notification channel per (sound x vibration) combination, insistent ring until dismissed or 5 minutes.
- Rain alert (`RainAlert`, `RainAlerts`): one notification when rain is about to start at the active place; opt-in; muted in quiet hours.
- Notifications need the Android 13+ permission; without it timers still work visually.

## 5. Updates, sharing, backup, guide
- In-app update check/download/verify/install (`Updater`, `UpdatesSection`, `UpdateMath`); `latest-apk.yml` publishes `ClockWise.apk`, `.sha256`, `version.json`. Share the app as a link or as the installed APK (`AppSharer`).
- Backup and sharing (`BackupSection`, `LookCode`): `CW1:` look code (no personal data), `CWB1:` full backup (includes places), restore by paste.
- First-run guide (`GuideDialog`), re-shown from Settings.
- Settings -> Permissions (`PermissionsSection`) and Settings -> Widget status (`WidgetStatusSection`: is the minute tick alive, repair button).

## 6. Hebrew / English / RTL
All strings exist in `values/` (Hebrew, default) and `values-en/` (tests enforce key and placeholder parity). The widget and the dashboard follow `language` (`system`/`he`/`en`). Clock digits stay LTR inside RTL text.
