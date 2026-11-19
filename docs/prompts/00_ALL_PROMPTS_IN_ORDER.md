# כל הפרומפטים ל-ChatGPT (האפליקציה: ClockWise), לפי הסדר (מוכנים להעתקה)

**איך עובדים (לכל סבב):**
1. פתח צ׳אט **חדש** ב-ChatGPT (עם יצירת תמונות, המודל הכי טוב שיש לך).
2. צרף את תמונות הייחוס שכתוב בכותרת הסבב.
3. העתק את **כל** הבלוק (מהשורה ```text ועד הסגירה ```), הדבק כהודעה אחת ושלח.
4. אחרי כל שלב ChatGPT יעצור. אם התוצאה יפה באמת, כתוב `continue`. אם לא, כתוב מה לא טוב (אל תכתוב continue).
5. בסוף הסבב הורד את קובץ ה-zip ושלח לי אותו כאן. אני בודק, מתקן ומשלב באפליקציה.

**חשוב:** הסדר חשוב. סבב 0 קובע את הקו העיצבי של כל השאר, ולכן כדאי לעבור עליו לאט עד שהלוגו והסגנון ממש מוצאים חן בעיניך. הפרומפטים באנגלית כי ChatGPT מבין אותם הכי טוב; ההנחיות אליך בעברית.


---

## סבב 0 - לוגו ClockWise וספר סגנון (בוצע; ספר הסגנון מאושר)

**לא צריך להריץ שוב.**

(קובץ המקור: `docs/prompts/ROUND0_STYLE_BIBLE_AND_LOGO_PROMPT.md`)

```text
REFERENCE: the owner's reference screenshot of a flip-clock widget (an HTC-Sense-style home-screen clock: two large white flip cards with thin numerals over a wallpaper, and a dark weather bar below). Use it ONLY as a mood/structure hint (physical cards, hinge, calm numerals) - do NOT copy it; our design is original and more refined.

ROLE: You are a senior brand and illustration designer at a top studio. We are creating "ClockWise" (Hebrew name: "שעון ומזג אוויר"), an Android home-screen clock + weather widget app. You will define the visual identity and ONE consistent illustration line that all later work (weather art, animations, holiday badges, icon) must follow.

STAGE 1 - Logo concepts: 4 different logo concepts, each shown as a horizontal lockup (mark on the left + the wordmark "ClockWise" on the right) on a light #F2F2F2 background and again on dark #15171A, 2048 x 768 each. The mark follows the signature: a flip card split by the hinge seam, a luminous sun/moon rising from behind it, a 4-point sparkle. The wordmark is the exact text ClockWise (capital C, capital W, no space, no other text): a custom-feeling geometric or humanist sans with soft rounded terminals; the "W" may subtly echo the hinge seam. Check the spelling letter by letter before showing me. Vary the composition and level of detail (one minimal geometric, one soft-3D, one gradient-glass, one warm tactile). Rate honestly which is best at 24 px for the mark and at 120 px wide for the wordmark. Wait for my pick.
STAGE 2 - Refine the chosen logo: 3 refinements (colour, proportions, sparkle placement, letter shapes/spacing of the wordmark). Wait for my pick.
STAGE 3 - Final logo files on flat magenta #FF00FF: logo_mark.png (mark only, full colour, 1024 x 1024), logo_mono.png (single-colour silhouette of the mark, 1024 x 1024), logo_lockup_dark.png and logo_lockup_light.png (mark + wordmark, horizontal, 2048 x 768, tuned for dark and light backgrounds), logo_wordmark_dark.png and logo_wordmark_light.png (text only, 2048 x 512). Show the lockup also at 480, 240 and 120 px wide and the mark at 192, 96 and 48 px to prove legibility. The ONLY text anywhere is the word ClockWise.
STAGE 4 - Style bible sheet (this is the reference for every later round): one 2048 x 1536 sheet showing the SAME hero treatment applied to 6 sample objects: sun, cloud, raindrop cluster, snowflake, crescent moon, thunderbolt - each with the hinge seam, the sparkle, the lighting rules, the 3-stop gradients and the soft shadow, on light and dark halves; add the ClockWise wordmark once as a style reference (it is the only text). Add a palette strip with the exact hex values from the design language. Then a one-paragraph written rule list ("what is always the same") that I can paste into future chats.
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

RULES YOU MUST FOLLOW: original work only (CC0), no text/letters/numbers inside the art (the ONLY exception: the word "ClockWise" in the Round 0 logo wordmark files), flat #FF00FF background where specified, 6 % margin, one consistent studio style across all files. Work in STAGES and wait for me to say "continue" between stages. Aim for the highest craft you can: if a result is merely decent, redo it before showing me.

VALIDATION AND REPORT (final message of the last stage):
1. List every file with exact name, pixel size and whether the background is flat #FF00FF.
2. Honestly list anything off-model, clipped, too near the edge, containing text, missing the hinge seam or sparkle, or deviating from the approved style.
3. One contact-sheet image with all files on a light (#F2F2F2) and a dark (#15171A) background.
4. ONE zip with the exact file names plus README.txt.

```

---

## סבב 0ב - תיקון הלוגו (בוצע)

**לא צריך להריץ שוב.**

(קובץ המקור: `docs/prompts/ROUND0B_LOGO_FIX_PROMPT.md`)

```text
REFERENCE: attach (1) style_bible.png from Round 0 - it is APPROVED and is the visual reference for everything; (2) the current draft logo_lockup_dark.png and logo_mark.png so you can see exactly what must be fixed.

ROLE: Same senior brand designer as in Round 0. The style bible is excellent - do not change it. The LOGO FILES are not good enough yet. Below is an exact list of defects. Fix every one and show me the corrected mark before producing final files.

DEFECTS TO FIX (numbered; address each one explicitly in your report):
1. STRAY ARTIFACT: a white half-moon / arc appears to the right of the mark in logo_mark.png (and in the small previews). It is a cut-out error. The new files must contain only the logo: nothing else anywhere on the canvas.
2. THE CARD DOES NOT READ AS A FLIP-CLOCK CARD. Today it looks like a squat sofa/pillow or a pair of headphones: it has a vertical centre seam and two ear-cup-like side lugs. Redesign it so it is instantly a FLIP CARD: one upright rounded-rectangle card (taller than wide, about 4:5), split by ONE horizontal hinge seam at about 52 % height, with two small round hinge pins on the left and right ends of the seam (like a real flip clock), the upper half very slightly lifted or tilted forward to show it flipping. NO vertical seam, NO ear-cup lugs, NO text or digits on the card. It must be recognisable as a flip card at 48 px.
3. THE SUN LOOKS LIKE FIRE. The pink/red flame-like aura around the sun looks burning and aggressive. Replace it with the exact sun from the style bible (round golden disc, rounded petal rays, one hinge seam, calm warm glow in soft yellow-orange only; no red, no pink). The sun rises from behind the upper half of the card, about 45 % of it visible.
4. CUT-OUT QUALITY: the previous export used an automatic background removal that left halos, rough edges and lost highlights. Do NOT run any code to remove or recolour the background. Generate every image DIRECTLY on a flat solid #FF00FF background (exactly), with clean smooth edges, and no magenta, pink or purple spill on the object edges.
5. THE MONO FILE IS A SHAPELESS BLOB. logo_mono.png must be a clean single-colour (pure white #FFFFFF) silhouette OF THE NEW MARK drawn on flat #FF00FF, with the hinge seam, the hinge pins and the gaps between the sun rays cut out so the shape stays readable at 48 px (like an Android monochrome icon). No anti-aliasing noise, no blob.
6. THE WORDMARK: keep the current letterforms (bold, rounded, slightly italic "ClockWise" - the spelling is correct, keep capital C and capital W), they are good. Fix only: (a) generate the light-background and dark-background versions NATIVELY as separate drawings, do not recolour one into the other; (b) remove the purple outline/halo; the light-background version is solid deep navy #1B2A3A with a subtle top highlight, the dark-background version is white with a soft blue shade; (c) keep the golden swoosh underline but with ONE standard 4-point sparkle at its end (same shape as in the style bible).
7. CONSISTENCY: the final mark must look like it belongs to the style bible sheet: same sun, same sparkle (upper right of the mark), same blue of the card (#00B0FF / #4FA3E0 with light #C9D3DD highlights), same soft shadow.

STAGE 1 - Fix test: show ONLY the corrected logo mark, three variations of the flip card (differences: how much the upper half is lifted, hinge-pin style, sun position), each 1024 x 1024 on flat #FF00FF, plus the same three at 48 px on light and dark backgrounds. For each, say honestly which of the 7 defects it fixes. Wait for my pick ("continue").
STAGE 2 - Final files from the picked variation, exact names and sizes: logo_mark.png (1024 x 1024), logo_mono.png (1024 x 1024), logo_lockup_dark.png and logo_lockup_light.png (mark + wordmark, 2048 x 768), logo_wordmark_dark.png and logo_wordmark_light.png (2048 x 512), all on flat #FF00FF. Mark in the lockup must be the same drawing as logo_mark.png.
STAGE 3 - Packaging: one zip, README.txt, and a contact sheet that uses the real pixels (no recolouring) at 480/240/120 px wide for the lockup and 192/96/48 px for the mark, on #F2F2F2 and #15171A.

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

RULES: original work only (CC0). The ONLY text anywhere is the word "ClockWise" in the lockup/wordmark files. Flat #FF00FF background, 6 % margin, nothing cut off. Wait for "continue" between stages.

VALIDATION AND REPORT (final message): list every file with exact name, pixel size and whether the background is flat #FF00FF; state for each of the 7 defects whether it is fixed and how; honestly list anything still imperfect; provide the zip.

```

---

## סבב 0ג - הלוגו: כרטיס שעון ספרתי עם ספרות (להריץ עכשיו)

**הדבק באותו צ׳אט של סבב 0ב. צרף שוב logo_mark.png ו-logo_lockup_light.png.**

(קובץ המקור: `docs/prompts/ROUND0C_LOGO_CLOCK_CUE_PROMPT.md`)

```text
Great progress - the sun, the hinge seam, the sparkle, the wordmark and the mono silhouette are now right. Change ONLY the items below and keep everything else exactly as it is in the 0B files.

1. THE CARD NOW READS AS A HANDBAG / LUNCH BOX, NOT A CLOCK. Cause: wide squat shape plus round clasp-like pins that stick out. Make it a FLIP-CLOCK CARD, which is a DIGITAL clock card: upright portrait rounded rectangle (about 4:5, taller than wide), the single horizontal hinge seam at about 52 % height, the upper half very slightly lifted/tilted. On the card face put ONE big, thin, elegant numeral pair "10" (two digits, light weight like the HTC-style flip clocks), cut cleanly in half by the hinge seam so the top halves of the digits sit on the upper half and the bottom halves on the lower half. NO clock hands anywhere on the card (hands belong to an analog watch, a different product style). The numerals are the only characters allowed on the card. The hinge pins become small, half-embedded notches/slots flush with the card edge (not round balls protruding like clasps).
2. NO COLOURED GLOW OR SPILL. Around and under every object there must be no magenta, purple or pink tinted glow or shadow. Drop shadow: dark navy #1B2A3A at about 20 % opacity, only close to the object. Sun glow: soft yellow-orange only, no pink.
3. THE BACKGROUND MUST BE EXACTLY FLAT #FF00FF in every pixel of every file (no noise, no vignette, no gradient, no variation between rows). logo_mark.png from 0B had a varying background.
4. THE MARK INSIDE THE LOCKUPS MUST BE THE SAME DRAWING AS logo_mark.png (identical sun size, card, hands, sparkle), only scaled. Update logo_mono.png from the new mark (same clean single-colour approach as 0B, the numerals and the seam cut out).
Keep unchanged: the ClockWise wordmark letterforms and spelling, the golden swoosh with ONE sparkle, the sun from the style bible, native light/dark wordmarks, file names and sizes.

STAGE 1: show ONLY the new mark, three variations (differences: numeral weight and size, how much the upper half is lifted, hinge-notch style), each 1024 x 1024 on flat #FF00FF, plus each at 48 px on #F2F2F2 and #15171A. Say honestly which of the 4 items each fixes. Wait for "continue".
STAGE 2: final 6 files from my pick: logo_mark.png, logo_mono.png (1024 x 1024), logo_lockup_dark.png, logo_lockup_light.png (2048 x 768), logo_wordmark_dark.png, logo_wordmark_light.png (2048 x 512), then a real-pixel contact sheet and one zip with README.txt. Report honestly any item not fully fixed.
```

---

## סבב 1 - איורי מזג אוויר (16 תמונות)

**צרף: style_bible.png והלוגו שאושר.**

(קובץ המקור: `docs/prompts/ROUND1_WEATHER_PROMPT.md`)

```text
REFERENCE: attach the approved Round 0 images (logo + style bible sheet) to this chat first and match them exactly - same light, same hinge seam, same sparkle, same palette. If you have no reference images yet, STOP and ask me for them.

ROLE: You are the lead illustrator for a premium Android home-screen clock and weather widget called "ClockWise". You will create ONE consistent family of weather illustrations that are used large (up to 160 dp) on the widget and in the app.

DELIVERABLE: the weather pack from the Asset contract: files wx_<sky>_<day|night>.png, 512 x 512 px each. Skies: clear, mostly_clear, partly_cloudy, overcast, fog, drizzle, rain, heavy_rain, freezing_rain, snow, thunderstorm, hail, unknown. Day AND night variants for clear, mostly_clear, partly_cloudy; the other skies have a day version only. Total 16 images (13 day + 3 night).

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

RULES YOU MUST FOLLOW: original work only (CC0), no text/letters/numbers in images, flat #FF00FF background, 6 % margin, same style family across all files. Work in STAGES and wait for me to say "continue" between stages.

VALIDATION AND REPORT (final message of the last stage):
1. List every file with its exact name, pixel size and whether the background is flat #FF00FF.
2. Honestly list anything that is off-model, clipped, too close to the canvas edge, contains text, or deviates from the style (do not hide problems).
3. Provide one contact-sheet image with all files on both a light (#F2F2F2) and a dark (#15171A) background so I can judge readability on any wallpaper.
4. Package everything into ONE zip with the exact file names above, plus a README.txt listing the files.

```

---

## סבב 1ב - תיקון איורי מזג האוויר (אחרי סבב 1)

**הדבק באותו צ׳אט של סבב 1. צרף: style_bible.png, הלוגו, ה-contact_sheet שלך, ו-wx_rain_day / wx_clear_night / wx_mostly_clear_night / wx_unknown_day.**

(קובץ המקור: `docs/prompts/ROUND1B_WEATHER_FIX_PROMPT.md`)

```text
REFERENCE: attach (1) style_bible.png (approved law), (2) the approved ClockWise logo mark, (3) your own Round 1 contact sheet and the files wx_rain_day.png, wx_clear_night.png, wx_mostly_clear_night.png, wx_unknown_day.png so you can see exactly what to fix.

Round 1 is a strong, consistent family - composition, cloud shapes, colours, drops, flakes, hail and bolt are right and must stay. It is NOT yet at the quality of the style bible. Fix exactly the defects below, keep everything else, and show a test first. (Note: the pack is 16 images = 13 day + 3 night, not 19; my earlier count was wrong - your 16 files are correct.)

DEFECTS TO FIX (answer each by number in your report):
1. THICK DARK-BLUE OUTLINES around clouds, drops, flakes, hail and moon. The style bible has NO outlines: edges are defined by soft rim light and a slightly darker shade of the object's own hue (3-stop gradient: light / base / shade). Remove all outlines.
2. SPARKLE POSITION. The signature 4-point sparkle must sit at the UPPER-RIGHT of the hero object (as in the style bible). In most files it is at the lower-right. Exactly one standard sparkle per image (night images: the small stars stay, plus one standard sparkle at the upper-right of the moon).
3. HINGE SEAM TOO HEAVY. Keep the horizontal seam at about 52 % height, but exactly like the style bible: a thin, subtle line (8-12 % darker than the surface) with a soft highlight below it. NO black line, NO round pins/balls at its ends (pins belong only to the logo card).
4. wx_unknown_day: remove the question mark. Use three soft dots (small round dots in a row) on/under the cloud instead - calm, not alarming.
5. wx_clear_night: the seam crosses empty space. Draw the seam only ON the body of the crescent moon (two short segments), never across the empty middle.
6. wx_mostly_clear_night: there is a stray fragment at the right edge and the artwork is off-centre; also rectangular patches of slightly different magenta are visible in several files. Centre every object, keep >= 6 % empty margin, and make nothing touch or approach the edges.
7. BACKGROUND AND SPILL. Every file must be ONE exactly flat #FF00FF (no patches, no noise, no vignette) generated directly, no background-removal code. No purple/blue/pink coloured shadow or glow around objects: shadow = dark navy #1B2A3A at about 20 % opacity only close to the object.
8. wx_clear_day: use the sun from the style bible (round golden disc with rounded petal-shaped rays and one seam), not pointed spikes.
9. wx_fog_day: three horizontal translucent fog bands (currently two) under a soft cloud.
10. Precipitation: wx_drizzle_day = 5-6 SMALL drops; wx_rain_day = 8-9 medium drops; wx_heavy_rain_day = darker, larger cloud with dense LONG drops and 2-3 small splashes. Keep what is already good in each.

STAGE 1 - Fix test: redo ONLY wx_clear_day, wx_rain_day, wx_clear_night and wx_unknown_day (512 x 512, flat #FF00FF), then say honestly which of the 10 defects each fixes. Wait for "continue".
STAGE 2 - Redo all 16 files in the approved fixed style (13 day + 3 night: wx_<sky>_day.png for clear, mostly_clear, partly_cloudy, overcast, fog, drizzle, rain, heavy_rain, freezing_rain, snow, thunderstorm, hail, unknown; wx_<sky>_night.png for clear, mostly_clear, partly_cloudy), 4 per message. Wait for "continue" between messages.
STAGE 3 - Packaging: one zip, README.txt, a real-pixel contact sheet on #F2F2F2 and #15171A, and an honest list of anything still imperfect.

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

RULES: original work only (CC0). No text or letters in any image. Flat exact #FF00FF background, 6 % margin, nothing cut off. Wait for "continue" between stages.

```

---

## סבב 2 - אנימציות מזג אוויר (לולאות של 12 פריימים)

**צרף: הלוגו, style_bible.png ותמונות מזג האוויר שאושרו בסבב 1.**

(קובץ המקור: `docs/prompts/ROUND5_WEATHER_ANIMATION_PROMPT.md`)

```text
REFERENCE: attach the approved Round 0 logo + style bible sheet AND the approved Round 1 weather stills. Every animation is the SAME illustration as its Round 1 still, brought to life: identical drawing, colours, hinge seam and sparkle.

ROLE: Motion illustrator producing frame sequences for an Android widget. The app plays the frames itself (so each frame is a separate PNG); smoothness and perfect registration matter more than frame count.

DELIVERABLE per animation: 12 PNG frames wxa_<sky>_<day|night>_01.png ... _12.png, each 512 x 512 on flat #FF00FF, plus one 4 x 3 contact sheet (2048 x 1536). The loop must be seamless (frame 12 flows into frame 01), 1.2-2.4 s at about 8 fps; frame 01 is the complete still image (identical to the Round 1 still) and is used in "calm" mode.
Animations (priority order): clear_day (slow ray shimmer + sparkle twinkle), clear_night (stars twinkle out of phase, moon glow breathes), partly_cloudy_day (cloud drifts +/-4 % width, sun glow pulses), overcast, rain, heavy_rain, drizzle, snow, thunderstorm, fog, mostly_clear_day, mostly_clear_night, partly_cloudy_night, freezing_rain, hail.
REGISTRATION RULES (critical): same canvas, same object position and scale in every frame; only the moving elements change; no camera shake, no scale pumping of the whole object; identical colours/lighting between frames (no flicker); no new elements popping in suddenly - everything that appears also disappears smoothly within the loop.
MOTION RULES: ease-in-out, calm, tactile; rain drops fall along one fixed slant with tiny splashes; snow flakes sway; thunderstorm = soft double glow flash inside the cloud (max 2 flashes per loop, never faster than 3 Hz, no full-frame white); fog bands drift slowly.

STAGE 1 - Motion test: ONLY clear_day and rain: 12 frames each as contact sheets + one sentence describing the motion curves. Wait for "continue".
STAGE 2 - clear_night, partly_cloudy_day, snow, thunderstorm. Wait for "continue".
STAGE 3 - the rest, 3 animations per message. Wait for "continue" between messages.
STAGE 4 - packaging: validation and report; also state for every animation which element moves and by how much (so the app can verify registration).

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

RULES YOU MUST FOLLOW: original work only (CC0), no text/letters/numbers inside the art (the ONLY exception: the word "ClockWise" in the Round 0 logo wordmark files), flat #FF00FF background where specified, 6 % margin, one consistent studio style across all files. Work in STAGES and wait for me to say "continue" between stages. Aim for the highest craft you can: if a result is merely decent, redo it before showing me.

VALIDATION AND REPORT (final message of the last stage):
1. List every file with exact name, pixel size and whether the background is flat #FF00FF.
2. Honestly list anything off-model, clipped, too near the edge, containing text, missing the hinge seam or sparkle, or deviating from the approved style.
3. One contact-sheet image with all files on a light (#F2F2F2) and a dark (#15171A) background.
4. ONE zip with the exact file names plus README.txt.

```

---

## סבב 3 - שעון כיס מזהב (סגנון השעון האנלוגי, כולל פתיחת המכסה)

**צרף: style_bible.png והלוגו שאושר.**

(קובץ המקור: `docs/prompts/ROUND6_POCKET_WATCH_PROMPT.md`)

```text
REFERENCE: attach the approved Round 0 style_bible.png (and the final logo files once approved). The style bible is the law for light, gradients, palette, signature and finish.

ROLE: Senior illustrator and product designer. ClockWise has two completely different clock worlds: FLIP CARDS (digital, already designed) and the ANALOG look, which is a GOLDEN POCKET WATCH of the kind carried on a chain in a waistcoat pocket: a hunter-case lid that springs open on a hinge at the top (12 o'clock), a crown and bow ring at the top, a chain, a cream/ivory porcelain dial with elegant numerals, slim ornate hands. It must feel precious, warm and luminous, in exactly the same studio style as the style bible (soft volumes, 3-stop gradients, top-left light, hinge-seam and 4-point-sparkle signature adapted below), NOT a realistic photo and NOT a cheap clip-art watch.

SIGNATURE ADAPTED FOR THE WATCH: (a) the hinge seam = the thin hinge/joint line where the lid meets the case at the top plus one fine engraved horizontal line at about 52 % height on the closed lid; (b) one standard 4-point sparkle glint on the upper-right of the gold, same shape as in the style bible. Metal colours: gold = #FFB300 base, #FFD54F highlight, #C77700 shade; silver = #E9EEF3 / #FFFFFF / #8D99A6; rose gold = #F4B3A0 / #FFD9CC / #B5705F. Dial: cream #FFF4D6 or ivory #F6F1E4 or black #14181F with gold numerals.

DELIVERABLES (all on flat exact #FF00FF, generated directly, NO background-removal code, no magenta/purple/pink glow; shadow = dark navy #1B2A3A at 20 % only close to the object):
A. pw_case_closed.png (1024 x 1024): the whole watch, lid CLOSED, front view, crown and bow ring on top, engraved simple sun-and-sparkle motif on the lid, soft shadow.
B. pw_bezel_gold.png, pw_bezel_silver.png, pw_bezel_rose.png (1024 x 1024): the open watch front WITHOUT dial face and without hands: only the round case ring/bezel with crown and bow ring; the dial area inside the ring is flat #FF00FF so the app can place a dial there. Same geometry in all three; only metal differs.
C. pw_dial_cream.png, pw_dial_ivory.png, pw_dial_black.png (1024 x 1024): just the circular dial face (round, edge to edge of the circle, nothing outside), elegant Roman numerals XII...I, fine minute track, a small seconds sub-dial at 6 o'clock, soft inner shadow. NO hands. Numerals are allowed here (the only text besides the logo).
D. pw_hand_hour.png, pw_hand_minute.png, pw_hand_second.png (256 x 1024 each): one hand pointing STRAIGHT UP, pivot circle centred at exactly x=128, y=768 (the app rotates it around that point), short counterweight tail below the pivot. Hour hand: ornate spade/fleur style; minute hand: slim Breguet moon-tip style; second hand: very thin with a small ring counterweight. Gold with highlight and soft shadow. Flat #FF00FF around.
E. pw_lid_01.png ... pw_lid_12.png (1024 x 1024 each): the OPENING ANIMATION: the lid is hinged at the TOP of the case and swings up and back. Frame 01 = lid closed (identical to pw_case_closed.png), frame 12 = lid fully open (lid foreshortened to a thin gold rim at the top, the dial and bezel visible; the dial is the cream dial WITHOUT hands). Same canvas, same case position and scale in every frame, pixel-exact registration of the case; only the lid moves; ease-in-out; calm; no bounce. Frames evenly spaced in angle.
F. pw_chain.png (1024 x 512): a golden chain hanging in a gentle curve from the top centre (where the bow ring attaches) off to the top-right corner, links readable at 100 px wide.

STAGE 1 - Style test: ONLY pw_case_closed.png, pw_bezel_gold.png and pw_dial_cream.png, plus one preview composite of them assembled (bezel + dial). Say honestly what is not yet perfect. Wait for "continue".
STAGE 2 - The three hands and the two other dials (ivory, black). Wait for "continue".
STAGE 3 - The 12 lid frames as a 4 x 3 contact sheet first (to check smoothness and registration), then individually. Wait for "continue".
STAGE 4 - silver and rose bezels, the chain, then packaging.

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

RULES: original work only (CC0). No text except the dial numerals. Flat exact #FF00FF background, 6 % margin, nothing cut off. Wait for "continue" between stages.

VALIDATION AND REPORT (final message): list every file with exact name, pixel size and whether the background is exactly flat #FF00FF; check the pivot position of each hand (x=128, y=768) and the registration of the 12 lid frames; list honestly anything off-model, clipped or with colour spill; one contact sheet on #F2F2F2 and #15171A; one zip with README.txt.

```

---

## סבב 4 - אייקון האפליקציה

**צרף: הלוגו שאושר.**

(קובץ המקור: `docs/prompts/ROUND2_ICON_BRAND_PROMPT.md`)

```text
REFERENCE: attach the approved Round 0 images (logo + style bible sheet) to this chat first and match them exactly - same light, same hinge seam, same sparkle, same palette. If you have no reference images yet, STOP and ask me for them.

ROLE: Icon designer for "ClockWise" (a flip-clock + weather widget app for Android, Hebrew-first; the app is called ClockWise, but the icon contains NO text - mark only, built from the approved Round 0 logo mark).

DELIVERABLE: Android adaptive icon layers, 432 x 432 px each (108 dp at 4x): ic_launcher_fg.png (foreground art only, safe zone = centre 66 %, i.e. everything important inside a 285 px circle), ic_launcher_bg.png (background layer: a rich but calm gradient/texture, full bleed, NO magenta needed here - fill the full square), ic_launcher_mono.png (single-colour silhouette on flat magenta for the themed monochrome icon, shape only).

CONCEPT: one flip-clock card (rounded, hinge line across the middle, soft depth) showing NO digits (digits are text) - instead a bold stylised glowing sun/cloud peeking out from behind the card's upper right corner. Reads at 48 px. Palette: classic_white card on a warm-to-deep-blue gradient background using accent #FFB300 and ocean #00B0FF. Avoid copying any existing app icon.

STAGE 1: 3 different concepts of the full composed icon (foreground over background, in a rounded-square mask preview), 512 px, in one message. Wait for "continue" and my pick.
STAGE 2: after my pick, produce the three layer files exactly as specified, plus a preview of the composed icon in circle, squircle and rounded-square masks on light and dark home-screen backgrounds. Then packaging.

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

RULES YOU MUST FOLLOW: original work only (CC0), no text/letters/numbers in images, flat #FF00FF background, 6 % margin, same style family across all files. Work in STAGES and wait for me to say "continue" between stages.

VALIDATION AND REPORT (final message of the last stage):
1. List every file with its exact name, pixel size and whether the background is flat #FF00FF.
2. Honestly list anything that is off-model, clipped, too close to the canvas edge, contains text, or deviates from the style (do not hide problems).
3. Provide one contact-sheet image with all files on both a light (#F2F2F2) and a dark (#15171A) background so I can judge readability on any wallpaper.
4. Package everything into ONE zip with the exact file names above, plus a README.txt listing the files.

```

---

## סבב 5 - טקסטורות לכרטיסים, איורי הסבר ותמונות לבורר הווידג׳טים

**צרף: הלוגו, style_bible.png ותמונת מזג אוויר אחת שאושרה.**

(קובץ המקור: `docs/prompts/ROUND4_CARD_TEXTURES_AND_SPOT_ART_PROMPT.md`)

```text
REFERENCE: attach the approved Round 0 images (logo + style bible sheet) to this chat first and match them exactly - same light, same hinge seam, same sparkle, same palette. If you have no reference images yet, STOP and ask me for them.

ROLE: Art director for the clock face surfaces and onboarding illustrations of "ClockWise".

PART A - Card textures (greyscale, tileable): tex_card_light.png, tex_card_dark.png, tex_card_warm.png, each 1024 x 1024 px, seamlessly tileable, GREYSCALE only (the app tints them per palette), very subtle surface (ceramic / soft paper / brushed matte plastic - choose one family and keep it for all three), luminance between 40 % and 60 % grey so tinting works, NO visible pattern repetition, NO magenta (these fill the whole square).

PART B - Spot art, 640 x 480, flat magenta background: spot_add_widget (a phone home screen with an empty slot being filled by a flip-clock card - no text), spot_location (soft map pin over a gentle landscape), spot_calendar (a calendar page with two small coloured event dots), spot_alarm (a bell with a clock card), spot_steps (two stylised footprints with a path), spot_offline (a calm cloud with a small disconnected plug) - all in the design language, friendly, not childish.

PART C - Widget picker previews, 640 x 360, flat magenta background around a phone-widget mock (no phone frame, just the widget floating with soft shadow over a blurred wallpaper that you paint as a rounded rectangle inside the image). Use only placeholder blocks for text lines (grey rounded bars), NO real text or digits. Two families:
 (a) by clock style: preview_flip (two big flip cards with a dark info panel below), preview_digital (large thin digits, no cards), preview_analog (analog dial with hands), preview_minimal (tiny clock and one line of weather).
 (b) by widget type: preview_kind_classic (full dashboard: clock, date line, weather panel, alarm chip, event banner, world clocks, footer), preview_kind_clock (clock only with a date line), preview_kind_weather (big weather panel with illustration, temperature, max/min bars, sunrise/sunset), preview_kind_agenda (3 event rows as rounded pills + an alarm chip), preview_kind_strip (one slim wide row: time on one side, two small text bars in the middle, weather icon and temperature on the other side).

STAGE 1: texture family test: tex_card_light only + one spot art (spot_location) so I can approve the surface style and the illustration style. Wait for "continue".
STAGE 2: all three textures + remaining spot art (3 per message).
STAGE 3: the nine previews (4 by clock style, 5 by widget type), 3 per message.
STAGE 4: packaging.

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

RULES YOU MUST FOLLOW: original work only (CC0), no text/letters/numbers in images, flat #FF00FF background, 6 % margin, same style family across all files. Work in STAGES and wait for me to say "continue" between stages.

VALIDATION AND REPORT (final message of the last stage):
1. List every file with its exact name, pixel size and whether the background is flat #FF00FF.
2. Honestly list anything that is off-model, clipped, too close to the canvas edge, contains text, or deviates from the style (do not hide problems).
3. Provide one contact-sheet image with all files on both a light (#F2F2F2) and a dark (#15171A) background so I can judge readability on any wallpaper.
4. Package everything into ONE zip with the exact file names above, plus a README.txt listing the files.

```

---

## סבב 6 - תגי חגים (13)

**צרף: הלוגו, style_bible.png ותמונת מזג אוויר אחת שאושרה.**

(קובץ המקור: `docs/prompts/ROUND3_HOLIDAY_BADGES_PROMPT.md`)

```text
REFERENCE: attach the approved Round 0 images (logo + style bible sheet) to this chat first and match them exactly - same light, same hinge seam, same sparkle, same palette. If you have no reference images yet, STOP and ask me for them.

ROLE: Illustrator creating small calm badges shown in the widget's date line next to the Hebrew date (about 24-40 dp on screen) and larger in the app.

DELIVERABLE: hol_<id>.png, 256 x 256 px: shabbat (two candlesticks with soft flames and a braided challah hint), rosh_chodesh (thin crescent moon with a few soft stars), rosh_hashana (apple with a drop of honey and a small pomegranate), yom_kippur (a calm white open book/candle - serious, soft, minimal), sukkot (a simple sukkah with a leafy roof and an etrog), hanukkah (a nine-branch menorah with warm flames), tu_bishvat (a blossoming almond branch), purim (a simple festive mask and a small groggers hint - no faces of real people), pesach (a matzah with a wine cup), yom_haatzmaut (a rounded celebratory fireworks burst in blue/white, NO flag, NO text), lag_baomer (a small bonfire), shavuot (wheat sheaf and white flowers), fast_day (a simple empty plate/neutral sand-colour hourglass - respectful, quiet).

STYLE: same design language; each badge is ONE centred object group on the flat magenta with the soft in-art shadow; designed to still be recognisable at 32 px. No Hebrew letters, no Stars of David on flags, no religious figures, respectful and warm.

STAGE 1: 4 badges (shabbat, hanukkah, pesach, rosh_hashana) in one message. Wait for "continue".
STAGE 2: the remaining 9 badges, 3 per message. Wait for "continue" between messages.
STAGE 3: packaging.

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

RULES YOU MUST FOLLOW: original work only (CC0), no text/letters/numbers in images, flat #FF00FF background, 6 % margin, same style family across all files. Work in STAGES and wait for me to say "continue" between stages.

VALIDATION AND REPORT (final message of the last stage):
1. List every file with its exact name, pixel size and whether the background is flat #FF00FF.
2. Honestly list anything that is off-model, clipped, too close to the canvas edge, contains text, or deviates from the style (do not hide problems).
3. Provide one contact-sheet image with all files on both a light (#F2F2F2) and a dark (#15171A) background so I can judge readability on any wallpaper.
4. Package everything into ONE zip with the exact file names above, plus a README.txt listing the files.

```
