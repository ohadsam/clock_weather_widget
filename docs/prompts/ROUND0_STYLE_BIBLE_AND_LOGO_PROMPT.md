# Round 0 - Style bible and the logo (run this FIRST)

**How to use (owner):** open a NEW ChatGPT chat (image generation on, best model available), attach the reference images named in the REFERENCE line, paste the whole fenced block as ONE message, and answer "continue" after each stage when you are happy. Download the final images/zip and send them back to Claude. If a stage is not beautiful enough, say what is wrong - do not say continue.

```text
REFERENCE: the owner's reference screenshot of a flip-clock widget (an HTC-Sense-style home-screen clock: two large white flip cards with thin numerals over a wallpaper, and a dark weather bar below). Use it ONLY as a mood/structure hint (physical cards, hinge, calm numerals) - do NOT copy it; our design is original and more refined.

ROLE: You are a senior brand and illustration designer at a top studio. We are creating "ClockWeather" (Hebrew name: "שעון ומזג אוויר"), an Android home-screen clock + weather widget app. You will define the visual identity and ONE consistent illustration line that all later work (weather art, animations, holiday badges, icon) must follow.

STAGE 1 - Logo concepts: 4 different logo mark concepts, each 1024 x 1024 on a light #F2F2F2 background and again on dark #15171A, mark only (no text). All must follow the signature: a flip card split by the hinge seam, a luminous sun/moon rising from behind it, a 4-point sparkle. Vary the composition and the level of detail (one minimal geometric, one soft-3D, one gradient-glass, one warm tactile). Rate honestly which is best at 24 px. Wait for my pick.
STAGE 2 - Refine the chosen logo: 3 refinements (colour, proportions, sparkle placement). Wait for my pick.
STAGE 3 - Final logo files on flat magenta #FF00FF: logo_mark.png (full colour, 1024), logo_mono.png (single colour silhouette, 1024), logo_lockup_dark.png and logo_lockup_light.png (mark with soft shadow tuned for dark/light backgrounds, 1024; mark only, NO text). Show them also at 192, 96 and 48 px to prove legibility.
STAGE 4 - Style bible sheet (this is the reference for every later round): one 2048 x 1536 sheet showing the SAME hero treatment applied to 6 sample objects: sun, cloud, raindrop cluster, snowflake, crescent moon, thunderbolt - each with the hinge seam, the sparkle, the lighting rules, the 3-stop gradients and the soft shadow, on light and dark halves. Add a palette strip with the exact hex values from the design language. Then a one-paragraph written rule list ("what is always the same") that I can paste into future chats.
STAGE 5 - Packaging: validation and report. I will re-attach the logo files and the style bible sheet to every later round.

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
4. No text, numbers, letters, watermarks or signatures in any image.
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
| Logo | `logo_mark.png` (1024 x 1024 colour on magenta), `logo_mono.png` (single colour on magenta), `logo_lockup_dark.png`, `logo_lockup_light.png` (mark only, no text) | 1024 | Round 0; the app icon in Round 2 is built from it |
| Weather animation | `wxa_<sky>_<day|night>_<NN>.png`, NN = 01..12 | 512 x 512 each, magenta background | seamless loop frames, frame 01 = still; delivered also as a 4x3 sheet 2048 x 1536 |
| Widget picker preview | `preview_<style>.png` | 640 x 360 | styles: flip, digital, analog, minimal |
=== END DESIGN LANGUAGE ===

RULES YOU MUST FOLLOW: original work only (CC0), no text/letters/numbers inside the art (except where a stage says otherwise), flat #FF00FF background where specified, 6 % margin, one consistent studio style across all files. Work in STAGES and wait for me to say "continue" between stages. Aim for the highest craft you can: if a result is merely decent, redo it before showing me.

VALIDATION AND REPORT (final message of the last stage):
1. List every file with exact name, pixel size and whether the background is flat #FF00FF.
2. Honestly list anything off-model, clipped, too near the edge, containing text, missing the hinge seam or sparkle, or deviating from the approved style.
3. One contact-sheet image with all files on a light (#F2F2F2) and a dark (#15171A) background.
4. ONE zip with the exact file names plus README.txt.

```
