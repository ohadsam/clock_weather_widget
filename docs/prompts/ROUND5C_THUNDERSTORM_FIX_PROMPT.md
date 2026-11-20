# Round 5C - Thunderstorm loop fix (same ChatGPT chat as Round 5B)

**How to use (owner):** paste the block below as ONE message in the Round 5B chat; attach the approved still `wx_thunderstorm_day.png` and your last `wxa_thunderstorm_day_01..12`. Answer "continue" only if the test frames look clean.

```text
Thank you - the other six loops are good and integrated. wxa_thunderstorm_day is the only one that is not usable yet. Fix exactly these defects, keep everything else (cloud, bolt shape, drop shapes, colours, sparkle position):
1. GHOST DROPS: in many frames the rain drops become semi-transparent purple double images (a second faded copy offset from the first). Every drop must be ONE solid drop, fully opaque, same blue as the still (#4FA3E0 family), no transparency, no duplicates, no motion-blur copies.
2. THE BOLT TURNS BROWN/OLIVE in the "off" frames (it darkens to a dirty gold-brown). The bolt must stay bright golden-yellow (#FFD54F with #FFB300 shade) in ALL frames. Flash = a larger, brighter warm-yellow glow around the bolt (+40 % radius) and the cloud lighting up (lighter violet, +30 %); between flashes the bolt is simply calmer (glow small), never darker or brown.
3. RAIN MUST FALL, NOT FLICKER: each drop moves straight down 40-60 px over the 12 frames at a constant slant, and when it reaches the bottom of the rain zone it reappears at the top (staggered so the zone always holds 8 drops). Drops never fade in or out except at the very top and bottom of the zone.
4. TWO FLASHES per loop, each lasting 2 frames, never faster than 3 Hz, never a white full-frame flash. Frame 01 stays pixel-identical to the approved still.
5. The sparkle (upper-right of the cloud): fade by opacity and glow size only, never darker than a bright warm white-yellow at 45 % opacity (no grey/brown).
6. Flat exact #FF00FF background in every frame, no purple/pink tinted shadow, nothing outside the 6 % margin.
Deliver wxa_thunderstorm_day_01..12.png (512 x 512) plus one 4 x 3 sheet 2048 x 1536 in ONE zip under 8 MB, and report honestly the number of drops per frame (must be 8) and how many pixels the bolt colour deviates from #FFD54F in the worst frame.
```
