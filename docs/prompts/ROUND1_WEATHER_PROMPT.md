# Round 1 - Weather illustration family (13 skies, day + night)

**How to use (owner):** open a NEW ChatGPT chat (image generation on, best model available), paste the whole fenced block below as ONE message, and answer "continue" after each stage when you are happy. Download the final images/zips and send them back to Claude in the session; Claude validates and integrates them. If a stage looks off, reply with what is wrong - do not say continue.

```text
REFERENCE: attach the approved Round 0 images (logo + style bible sheet) to this chat first and match them exactly - same light, same hinge seam, same sparkle, same palette. If you have no reference images yet, STOP and ask me for them.

ROLE: You are the lead illustrator for a premium Android home-screen clock and weather widget called "ClockWise". You will create ONE consistent family of weather illustrations that are used large (up to 160 dp) on the widget and in the app.

DELIVERABLE: the weather pack from the Asset contract: files wx_<sky>_<day|night>.png, 512 x 512 px each. Skies: clear, mostly_clear, partly_cloudy, overcast, fog, drizzle, rain, heavy_rain, freezing_rain, snow, thunderstorm, hail, unknown. Day AND night variants for clear, mostly_clear, partly_cloudy; the other skies have a day version only. Total 19 images.

SUBJECTS (one hero object each, same camera, same light, same scale):
- clear day: glowing warm sun, soft rim light, tiny sparkle. clear night: crescent moon with 2-3 tiny stars.
- mostly_clear: sun mostly visible with one small soft cloud at the lower right (night: moon with a small cloud).
- partly_cloudy: sun half behind a larger soft cloud (night: moon behind cloud).
- overcast: two layered clouds, slightly grey-blue, no sun.
- fog: a soft cloud with three horizontal translucent fog bands below it.
- drizzle: light cloud with 5-6 small short drops. rain: cloud with 8-9 medium slanted drops. heavy_rain: darker larger cloud with dense long drops and a few splashes.
- freezing_rain: grey-blue cloud with drops and 2-3 small ice crystals mixed in.
- snow: light cloud with 8-9 six-pointed flakes of varied size. hail: cloud with 6-7 round white-blue hailstones.
- thunderstorm: dark violet-grey cloud with one bright golden lightning bolt and a few drops.
- unknown: a soft cloud with a small neutral question-free dotted outline (no question mark; use three soft dots) - calm, not alarming.

STAGE 1 - Style test: produce ONLY 3 images in your proposed style: clear day, rain, thunderstorm - in one single message, each 512 x 512. Stop and wait for "continue". (I will give feedback on style first.)
STAGE 2 - Day set: after "continue", produce the remaining day images (clear, mostly_clear, partly_cloudy, overcast, fog, drizzle, heavy_rain, freezing_rain, snow, hail, unknown) with exactly the approved style. Show them as individual 512 x 512 images, 4 per message. Wait for "continue".
STAGE 3 - Night set: clear night, mostly_clear night, partly_cloudy night. Same style, cooler palette (night accents), the same hero shapes. Wait for "continue".
STAGE 4 - Packaging: validation and report below.

=== DESIGN LANGUAGE (apply to everything) ===
## Personality (3 words)
**Tactile, calm, luminous.** A premium physical flip-clock reborn for a modern phone: real-feeling cards with a hinge and soft depth, big confident numerals, quiet colour, and weather art that feels warm and hand-finished - never clip-art, never emoji-like, never childish.

## Audience and use
People of all ages glancing at the home screen for one second, in sunlight, in Hebrew (RTL) or English. The widget sits over ANY wallpaper (light, dark, busy) and has a transparent background, so every asset must read on both a bright photo and a dark photo: soft contact shadow under shapes, no thin low-contrast outlines, no text baked into art.

## Style system
* Shapes: rounded, generous, slightly soft; corner radius ~22 % of the shorter side for cards/plates; consistent light from the top-left, soft shadow falling bottom-right.
* Depth: gentle layered "paper / ceramic / frosted glass" feel - subtle gradient (top lighter, bottom darker, max 8 % luminance change), 1 soft highlight edge, NO heavy bevels, NO photorealistic textures, NO harsh black outlines.
* Weather art: a single hero object per image (sun, cloud, rain...) built from 2-4 simple volumes with soft gradient shading and a tiny sparkle/rim light; flat colour accents allowed; same camera (front view, no perspective tilt), same light, same scale, same visual weight across the whole family.
* Motion (when animation is requested): calm, 800 ms or longer loops, first frame is a complete still image, no flashing, nothing faster than 3 Hz; a "calm animations" switch exists in the app.
* Hebrew/RTL safety: no text or letters in any art; no left/right-only symbolism that breaks when mirrored (mirroring must stay correct).


## The name (owner, 2026-11-19): **ClockWise**
One word, capital C and capital W, Latin letters in both Hebrew and English UI. The logo lockup contains the name as a wordmark; it is the ONLY artwork allowed to contain text. The "W" may subtly echo the hinge seam (two strokes meeting on a horizontal seam). The name pun: a clock that is wise - calm, smart, helpful.

## Quality bar (owner, 2026-11-19: "designed, beautiful, invested; one consistent design line")
Every asset must look like it came from ONE professional studio, at the level of a premium weather/clock app (think award-winning iOS/Android weather apps, but original). Rich but calm: considered lighting, soft layered depth, crisp edges, careful colour harmony. Reject (and redo) anything that looks like clip-art, a generic emoji, stock icon-set, flat sticker, AI-glossy plastic, or that differs in style from its siblings. Detail is welcome only if it still reads at 40 px.

## The signature (what makes the family unmistakable - every asset has BOTH)
1. **The hinge seam**: a very thin, soft horizontal split line crossing the hero object at about 52 % height (echoing the flip-card hinge of the logo and the clock), subtle (8-12 % darker, 1 soft highlight beneath). Clouds, sun, moon, flakes, bolts, badges all carry it; in animation it is the line the object "flips" or breathes around.
2. **The four-point sparkle**: one small crisp 4-point star highlight on the upper-right of the hero object, same shape and proportions everywhere (long thin cross, soft glow, 3 sizes).
Plus: top-left light, soft shadow bottom-right, 3-stop gradients (light/base/shade of the SAME hue), rim light on the lit edge, no outlines.

## The logo (Round 0 sets it; all later rounds reference it)
A flip-card mark: a rounded card split by the hinge seam, with a luminous sun/moon rising from behind it, sparkle top-right. Works in one colour, on light and dark, at 24 px.

## Motion language (animations)
Calm and tactile. Easing: ease-in-out, nothing linear or bouncy. Weather loops: 12 frames, 1.2-2.4 s, seamless, the first frame is a complete still image (used for calm mode), registration between frames pixel-exact (same canvas, same object position/scale), only the elements that should move change. Clear sky: slow ray shimmer + sparkle twinkle; clouds: gentle horizontal drift (+/-4 % width) and soft breathing; rain/drizzle: drops fall on a seamless loop with tiny splashes; snow: flakes sway down; thunderstorm: calm loop with a soft double glow flash (never faster than 3 Hz, never full-screen white); fog: bands drift slowly; night: stars twinkle out of phase. Flip card (drawn by the app in code): fold 0.45 s with soft shade, hinge highlight.

## Colour tokens (the app's palettes, ARGB hex shown as #RRGGBB)
| Palette id | Card top | Card bottom | Digit ink | Panel | Panel text | Accent |
|---|---|---|---|---|---|---|
| classic_white | #FFFFFF | #E9E9EC | #111111 | #1B1B1D | #FFFFFF | #FFB300 |
| graphite | #3A3D42 | #2B2E33 | #F5F5F5 | #16181B | #F5F5F5 | #4FC3F7 |
| sand | #F6EBD7 | #E8D8BB | #3B2F1E | #3B2F1E | #FFF4E0 | #E07A1F |
| ocean | #DFF3FA | #BFE3F2 | #0B2A3C | #0B2A3C | #EAF8FF | #00B0FF |
| sunset | #FFE3D1 | #FFC9A8 | #4A1D12 | #4A1D12 | #FFEFE6 | #FF6D3A |
| forest | #E3EFE0 | #C9DEC4 | #14301C | #14301C | #EAF6E7 | #66BB6A |
| midnight | #1A2238 | #111829 | #F2F5FF | #0B0F1C | #F2F5FF | #8C9EFF |
Weather accents (use consistently): sun #FFB300 / #FFD54F glow, cloud light #F4F7FA to #C9D3DD (shade), rain blue #4FA3E0, storm violet-grey #5B5F8D with #FFD54F bolt, snow #EAF4FF with #A8CBEA shade, fog #D5DCE3, night sky accents #8C9EFF moon #FFF3C4.

## Hard rules
1. 100 % original work, created for this app; no copyrighted characters, logos, stock or brand look-alikes; you may only use generic shapes. Treat the output as CC0 and say if you cannot.
2. Output backgrounds are a FLAT solid magenta #FF00FF (exactly), no gradient, no shadow on the magenta, no magenta inside the art - the app keys it out. (ChatGPT transparency creates halos; flat magenta does not.) Drop shadows are drawn INSIDE the art as soft dark-blue-grey (#1B2A3A at <= 25 % opacity) only close to the object, never reaching the canvas edge.
3. Every asset fits fully inside its canvas with >= 6 % empty margin; nothing is cut off.
4. No text, numbers, letters, watermarks or signatures in any image - EXCEPT the logo lockup/wordmark files, which contain exactly the word "ClockWise" (check the spelling letter by letter) and nothing else.
5. Consistency beats novelty: when in doubt copy the previously approved image's style exactly.

## Asset contract (what the app loads - names are exact, lowercase, underscores)
| Pack | File name | Size | Notes |
|---|---|---|---|
| Weather | `wx_<sky>_<day|night>.png` | 512 x 512 | sky keys: clear, mostly_clear, partly_cloudy, overcast, fog, drizzle, rain, heavy_rain, freezing_rain, snow, thunderstorm, hail, unknown. Night variants only for clear, mostly_clear, partly_cloudy (others reuse day) |
| Weather mini | generated by script from the above at 96 x 96 | - | not requested from ChatGPT |
| Holiday badges | `hol_<id>.png` | 256 x 256 | ids: shabbat, rosh_chodesh, rosh_hashana, yom_kippur, sukkot, hanukkah, tu_bishvat, purim, pesach, yom_haatzmaut, lag_baomer, shavuot, fast_day |
| Card textures | `tex_card_<light|dark|warm>.png` | 1024 x 1024 tileable greyscale | tinted by the app per palette |
| App icon | `ic_launcher_fg.png`, `ic_launcher_bg.png`, `ic_launcher_mono.png` | 432 x 432 | adaptive icon layers, safe zone = centre 66 % |
| Spot art | `spot_<name>.png` | 640 x 480 | onboarding / empty states: add_widget, location, calendar, alarm, steps, offline |
| Logo | `logo_mark.png` (mark only, colour, 1024 x 1024 on magenta), `logo_mono.png` (single colour mark), `logo_lockup_dark.png`, `logo_lockup_light.png` (mark + wordmark "ClockWise", horizontal, 2048 x 768), `logo_wordmark_dark.png`, `logo_wordmark_light.png` (text only, 2048 x 512) | see left | Round 0; the app icon in Round 2 is mark-only and built from it |
| Weather animation | `wxa_<sky>_<day|night>_<NN>.png`, NN = 01..12 | 512 x 512 each, magenta background | seamless loop frames, frame 01 = still; delivered also as a 4x3 sheet 2048 x 1536 |
| Widget picker preview | `preview_<style>.png` and `preview_kind_<kind>.png` | 640 x 360 | styles: flip, digital, analog, minimal; kinds: classic, clock, weather, agenda, strip |
=== END DESIGN LANGUAGE ===

RULES YOU MUST FOLLOW: original work only (CC0), no text/letters/numbers in images, flat #FF00FF background, 6 % margin, same style family across all files. Work in STAGES and wait for me to say "continue" between stages.

VALIDATION AND REPORT (final message of the last stage):
1. List every file with its exact name, pixel size and whether the background is flat #FF00FF.
2. Honestly list anything that is off-model, clipped, too close to the canvas edge, contains text, or deviates from the style (do not hide problems).
3. Provide one contact-sheet image with all files on both a light (#F2F2F2) and a dark (#15171A) background so I can judge readability on any wallpaper.
4. Package everything into ONE zip with the exact file names above, plus a README.txt listing the files.

```
