# Integration with the Special Events app

ClockWise shows birthdays, anniversaries, memorials and other events that the **Special Events** app (`ohadsam/special_events`, package `com.ohadsam.specialevents`) manages. This file is the contract between the two apps. Keep it in sync with `docs/SETTINGS_REFERENCE.md` (`showSpecialEvents`, `specialEventsDays`) and with `SpecialEventsSource`.

## How it works today (no change needed in the other app)
- Special Events can write its events into a **local device calendar** (Settings -> export to calendar): `CalendarContract.Calendars.ACCOUNT_NAME = "special_events"`, `ACCOUNT_TYPE = ACCOUNT_TYPE_LOCAL`, display name "אירועים מיוחדים" (`DeviceCalendars.ACCOUNT_NAME` / `CALENDAR_TITLE` in that repo). The export covers the next 5 years and puts the Hebrew date in the description.
- ClockWise looks that calendar up by account name and type (`SpecialEventsSource.calendarId`), reads the instances of the next N days with the calendar permission it already has (`READ_CALENDAR`), and turns them into lines like "🎂 דני · בעוד 3 ימים" (`SpecialEvents`, pure, tested).
- Where it shows: the **widget row** (nearest event within `specialEventsDays`, default 7) and the dashboard card `special_events` (next 60 days, with a button that opens the Special Events app).
- Without the calendar the card explains what to do: install the app, then turn on export to calendar and allow calendar access in ClockWise.
- Privacy: nothing leaves the device; ClockWise only reads. The other app's own database and photos are never touched.

## Limits of this approach
- Events appear only after the other app exported (it refreshes the calendar when the user asks; the export is "written anew").
- Only title, date and all-day flag are used. Titles are untrusted text (length-clamped, control characters removed).
- An emoji is guessed from the title (birthday, anniversary, memorial, mitzvah, else a star).

## Optional stronger integration (needs a change in `ohadsam/special_events`; not built, ask the owner first)
A small **read-only exported ContentProvider** in Special Events (for example authority `com.ohadsam.specialevents.public` with `events/upcoming?days=N`), protected by a signature-level custom permission (both apps are signed with the same sideload key idea, but each repo has its own key today, so a normal read permission with a user prompt would be needed), returning id, type, title, date (Gregorian and Hebrew), days left and a privacy-safe flag. Benefits: no calendar export step, exact types (birthday / yahrzeit) and Hebrew-date events without duplicates. ClockWise would then prefer the provider and fall back to the calendar.
