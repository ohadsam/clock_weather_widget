# Suggestions - ALL APPROVED by the owner on 2026-11-19 (ledger R28); scheduled in docs/PLAN.md

## A. Look and feel
1. **Presets** (DONE in code, batch 2): one-tap looks ("Classic HTC", "Frosted glass", "Night stand", "Kids", "Shabbat calm") that set style + palette + transparency together.
2. **Living weather**: soft animated layers (drifting clouds, falling rain, twinkling stars) - ChatGPT sprite prompt after round 1; calm switch kept.
3. **Auto day/night palette** that follows sunrise/sunset, and **wallpaper-aware text colour** (shadow strength adapts to the wallpaper brightness).
4. **Font choices** for digits (thin, bold, slab, rounded) and a Hebrew-friendly font for the date line.
5. **Seasonal and holiday skins** that appear automatically (Hanukkah candles, Pesach, Independence Day) with the same cards.

## B. Time and calendar
6. **Shabbat/holiday mode**: candle-lighting and Havdalah times on Fridays (computed locally from location), Parashat HaShavua, Omer count, Daf Yomi (optional).
7. **Countdown chips**: "3 days to the trip", birthdays from contacts (read-only, opt-in).
8. **Multiple widgets with different settings** (e.g. a small clock-only for the lock screen area and a large one for the home page).
9. **Quiet-hours dimming**: widget dims at night.

## C. Weather
10. **Rain alert**: "rain starts in 40 minutes" line from minutely forecast; optional notification.
11. **Air quality / UV / wind / pollen** (Open-Meteo has them) as a second footer line.
12. **Radar/rain map** link, and "feels like" with a comfort word ("pleasant", "hot").
13. **Compare places**: swipe between your 4 places on the widget (tap the city name).

## D. Alarm, health, home
14. **Alarm integration upgrade**: show the next 2-3 SmartRing alarms by name, tap opens that alarm in SmartRing (needs a small change in SmartRing: an exported read-only provider).
15. **Steps goal ring** around the steps number; weekly sparkline.
16. **Battery and Wi-Fi mini-status** and **smart-home** shortcuts - only if you want them.
17. **Lock-screen / always-on** friendly variant (Android 16 widgets on lock screen when available).

## E. Quality and sharing
18. **Backup/restore** of all settings to a file; **share my widget look** as a code/QR to family phones.
19. **Share the app** (WhatsApp/e-mail/share sheet) + **in-app update check** exactly like SmartRing.
20. **Accessibility**: high-contrast preset, big-text preset, TalkBack reads the whole widget as one sentence ("Thursday 17:05, 25 degrees, rain expected").


# Status and new suggestions (2026-11-21)
Done since the list above: presets, shabbat times (candle lighting, nightfall), countdown chips (days until an event), rain alert, quiet hours, seasonal skins (colours), day/night look, backup/restore + share-my-look, share the app + in-app update, steps (device sensor), compare places (dashboard + pager), several widgets types, accessibility basics (48 dp, contrast).
Still open from the list: living weather layers beyond the loops, font choices, wallpaper-aware text colour, Parashat HaShavua / Omer / Daf Yomi, air quality / UV / wind / pollen, radar link, SmartRing alarm list, steps goal ring, lock-screen variant, TalkBack sentence for the whole widget, per-widget settings (item 8).

## New suggestions (not yet built - tell me which to build)
1. ~~Per-widget settings~~ DONE 2026-11-21 (R65).
2. **Emulator UI tests with screenshots** (`ui-tests.yml`) and an in-app guide whose pictures come from those tests.
3. **Omer count + Parashat HaShavua** in the date line and on the dashboard (all computed offline from `HebrewCalendar`).
4. **Weather extras**: UV, air quality, wind and pollen as chart tabs (Open-Meteo has them); "feels like" comfort word; radar link.
5. **Dashboard widget gallery**: add a second dashboard "page" for the watch/pocket-watch preview and a full-screen "bedside clock" mode (dim, always-on, flip cards big) with auto-brightness.
6. **Interval timer** (work/rest rounds) and **timer presets by name** ("Eggs 8 min") one tap from the widget.
7. **Haptic/ring preview picker** with the phone's own ringtone chooser per timer (system ringtone picker).
8. **Shabbat mode**: no timers/alerts from candle lighting to nightfall, and a calm look; Havdalah reminder notification.
9. ~~Birthdays and anniversaries~~ DONE 2026-11-21 through the Special Events app's calendar (R67); direct contacts birthdays and a direct provider remain optional (see `docs/SPECIAL_EVENTS_INTEGRATION.md`).
10. **Widget themes from art**: the optional round 7 ornaments (`docs/prompts/ROUND7_SEASONAL_PROMPT.md`) plus painted backgrounds that follow the weather.
11. **Reliability telemetry kept local**: a small on-device log of tick lateness and last errors shown in Widget status (no network), to debug launcher/OEM battery killers.
12. **Accessibility**: TalkBack reads the whole widget as one sentence; high-contrast and big-text presets; reduce-motion follows the system setting automatically.
13. **Wear / tablet**: a tablet layout of the dashboard (two columns) and a Wear OS tile (later).
