# Data, permissions and privacy

The promise: **everything stays on the device**; the network is used only for the things below, only when needed, and every permission is optional. **Repo tests fail if a manifest permission or a network host in the code is missing from this file - update it in the same change.**

## 1. Permissions (AndroidManifest.xml)
| Permission | Why | Needed for | Without it |
|---|---|---|---|
| `INTERNET` | weather, city search, update check, map tiles | weather, places, updates, map | widget shows clock/date and the last cached weather |
| `ACCESS_COARSE_LOCATION` | optional "my location" place (approximate only) | the "my location" place | pick a city by search or map |
| `READ_CALENDAR` | today's events (Google Calendar syncs into the device calendar) and the calendar of the Special Events app (birthdays, memorials), read only | events banner / card | no events |
| `SCHEDULE_EXACT_ALARM` (<= API 32) and `USE_EXACT_ALARM` | the minute tick and the countdown end on the exact moment | clock accuracy, countdown | alarms become inexact (the clock may be late); Settings -> Permissions and Widget status say so |
| `RECEIVE_BOOT_COMPLETED` | re-arm the minute tick after a reboot | clock after reboot | the heartbeat worker repairs it within 15 min |
| `POST_NOTIFICATIONS` (Android 13+) | countdown finished, rain alert | those two notifications | no notifications |
| `ACTIVITY_RECOGNITION` | steps from the phone's step counter | steps | no steps |
| `REQUEST_INSTALL_PACKAGES` | install an app update from inside the app | in-app update | install the APK by hand |
The Settings -> Permissions screen lists each one with its state and an Allow / Open settings button.

## 2. Network hosts (HTTPS only)
| Host | What is sent | When |
|---|---|---|
| `api.open-meteo.com` | rounded latitude/longitude of saved places | every 30 min by `WeatherWorker`, on refresh, when a place is added |
| `geocoding-api.open-meteo.com` | the text you typed in place search | when you press Search |
| `github.com` and `githubusercontent.com` | nothing personal (a plain GET of `version.json` and the APK) | only when you press "Check for updates" / download |
| `tile.openstreetmap.org` | the map area you look at, your IP address | only after you press "Show map" |
No analytics, no ads, no accounts, no crash reporting. The phone's own geocoder (Android `Geocoder`) may be used to name a tapped map point; that is the phone's service, not ours.

## 3. What is stored on the device (SharedPreferences, app-private)
| File | Content |
|---|---|
| `settings` / `json` | all settings (`docs/SETTINGS_REFERENCE.md`), including saved places and calendar ids |
| `forecast` | raw weather JSON per place + fetch time |
| `timers` | stopwatches, countdowns (with labels and sound choices), date countdowns |
| `tick` | when the minute tick last ran |
| `steps` | today's baseline and last reading of the step counter |
| `rain_alert` | when the last rain notification was shown |
| `update` | last install status |
| `guide` | whether the first-run guide was shown |
Cache directories: `cache/updates` (a downloaded APK, replaced on the next download), `cache/share` (the APK copy made for sharing), `cache/osmdroid` (map tiles). Backup (`allowBackup=true`) lets Android's own backup restore these on a new phone.

## 4. What leaves the device by user action
- "Share my look" (`CW1:` code): appearance only, no places or personal data.
- "Back up all settings" (`CWB1:` text): **includes saved places and calendar ids**. The UI warns; keep it private.
- Share the app: a link or the APK itself.

## 5. Untrusted input
Calendar titles/locations, place names, pasted codes and the update manifest are treated as untrusted: length-clamped, control and bidi characters stripped (`Untrusted.clean`), JSON parsed defensively with size caps, and everything stored passes `WidgetSettings.sanitized()` or a list `clean()`.
