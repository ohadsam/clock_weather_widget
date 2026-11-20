# Round 5B - Redo the 7 weak weather animations (same ChatGPT chat as Round 5)

**How to use (owner):** attach the files named in REFERENCE, paste the whole block as ONE message, answer "continue" only if the Stage 1 motion is clearly visible.

```text
REFERENCE: attach the approved Round 1B stills of the 7 skies below (wx_clear_day, wx_clear_night, wx_thunderstorm_day, wx_mostly_clear_day, wx_mostly_clear_night, wx_partly_cloudy_day, wx_partly_cloudy_night) and style_bible.png.

Round 5 delivered 15 loops. 8 of them are good and are already integrated (rain, heavy_rain, drizzle, freezing_rain, hail, snow, fog, overcast): the registration is stable, the style matches, and the motion is visible. These 7 are too weak or too repetitive and must be redone (the four cloud ones are in the app meanwhile, clear_day and clear_night stay still until redone):
- clear_day and clear_night: the 12 frames contain only 6 different images and the pixels barely change (peak colour change 13 of 255, ZERO pixels differ visibly). At widget size nothing moves.
- thunderstorm_day: only 4 different images and the flash is too faint (peak change 19 of 255).
- mostly_clear_day / mostly_clear_night / partly_cloudy_day / partly_cloudy_night: the cloud drifts only +/-7-9 px with 7 unique images; the sun/moon and sparkle do not move at all.

REQUIRED FOR EVERY ONE OF THE 7:
A. TWELVE GENUINELY DIFFERENT FRAMES, no ping-pong and no duplicates (frame 12 flows smoothly into frame 01; frame 01 is pixel-identical to the approved Round 1B still). 8 fps, 1.5 s loop.
B. VISIBLE AMPLITUDES (measured on the 512 x 512 canvas, smooth sinusoidal / ease-in-out, nothing linear or jerky):
 - Sparkle (any 4-point star): scale 70 % -> 125 % -> 70 % and brightness 45 % -> 100 % -> 45 % over the loop; night stars twinkle out of phase (each star 40 % -> 100 %).
 - Sun: ray tips pulse in length +/-6 % (rays alternate in two groups, odd and even rays in opposite phase); the glow around the disc grows/shrinks +/-12 %; the whole sun disc breathes +/-2 % scale. Moon: the glow breathes +/-12 %.
 - Clouds: drift horizontally +/-18 px (about 3.5 % of the width) AND bob vertically +/-4 px, the cloud's own soft shape deforms by at most 3 %.
 - thunderstorm_day: two flashes per loop, each flash lasts 2 frames: during a flash the cloud brightens by +30 % (lighter violet) and the bolt glows with a warm yellow halo of +40 % radius; between flashes the bolt is dimmer (60 %). Never faster than 3 Hz, never a white full-frame flash. Rain drops fall continuously: each drop moves down 40-60 px per loop and wraps to the top of the rain zone (the drops are not allowed to just oscillate).
C. REGISTRATION: same canvas, same cloud/sun/moon centre position and scale in every frame except for the motion above; no camera shake; flat exact #FF00FF background in every frame; no purple/pink glow; no outlines.
D. Keep the style bible look (soft 3-stop volumes, hinge seam at ~52 %, one sparkle upper-right).

STAGE 1 - Motion test: deliver ONLY clear_day and thunderstorm_day as 12 frames each (a 4 x 3 sheet 2048 x 1536 plus the individual 512 x 512 PNGs named wxa_<name>_01..12.png), and report numerically how many pixels differ between frame 1 and the most different frame (it must be at least 3 % of the object area for the sun and at least 8 % for the thunderstorm). Wait for "continue".
STAGE 2 - clear_night, mostly_clear_day, mostly_clear_night. Wait for "continue".
STAGE 3 - partly_cloudy_day, partly_cloudy_night, then packaging with README.txt (list per animation: number of unique frames, max changed pixel count, amplitude of each moving element).

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

## Two clock worlds (owner, 2026-11-19)
**Flip cards = a DIGITAL clock** (digits on cards; the logo's card carries digits, never hands). **Hands = an ANALOG clock** and it must look completely different: the **golden pocket watch** (hunter-case lid that opens on a top hinge, chain, crown, bow ring, cream/ivory dial, ornate hands), as on the antique watches carried on a chain in a waistcoat pocket. Never mix hands on a card or digits on the watch.

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
4. No text, numbers, letters, watermarks or signatures in any image - EXCEPT (a) the logo lockup/wordmark files, which contain exactly the word "ClockWise" (check the spelling letter by letter); (b) the two digits "10" on the logo's flip card; (c) the pocket-watch dial numerals (Roman or Arabic) in Round 6.
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
| Pocket watch (analog style) | `pw_case_closed.png`, `pw_bezel_<gold|silver|rose>.png`, `pw_dial_<cream|ivory|black>.png` (1024 x 1024); `pw_hand_hour.png`, `pw_hand_minute.png`, `pw_hand_second.png` (256 x 1024, pivot at x=128,y=768); `pw_lid_01..12.png` (1024 x 1024 opening sequence); `pw_chain.png` (1024 x 512) | see left | Round 6 |
| Widget picker preview | `preview_<style>.png` and `preview_kind_<kind>.png` | 640 x 360 | styles: flip, digital, analog, minimal; kinds: classic, clock, weather, agenda, strip |
=== END DESIGN LANGUAGE ===

RULES: original work only (CC0). No text. Flat exact #FF00FF background. Wait for "continue" between stages. Be honest in the report about any animation that is still too subtle.

```
