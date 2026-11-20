"""Turns the ChatGPT art sheets (flat #FF00FF background) into transparent assets and Android drawables.
  python3 -I scripts/make-art-assets.py logo      # design/logo/originals -> design/logo + launcher/brand drawables
  python3 -I scripts/make-art-assets.py weather   # design/weather/originals -> res/drawable-nodpi/wx_*.webp
  python3 -I scripts/make-art-assets.py holidays  # design/holidays/originals -> res/drawable-nodpi/hol_*.webp (256 px)
  python3 -I scripts/make-art-assets.py ui        # design/ui/originals (picker previews + onboarding spots) -> res/drawable-nodpi/preview_*.webp, spot_*.webp
  python3 -I scripts/make-art-assets.py pocket    # design/pocketwatch/originals -> aligned case/face/hands webp + core PocketWatchGeometry.kt
  python3 -I scripts/make-art-assets.py anim <dir> # <dir>/wxa_<name>_01..12.png (ChatGPT frames, kept outside git: ~40 MB)
                                                  #   -> res/drawable-nodpi/wxa_<name>_<k>.webp (unique frames only) + core WeatherAnimTable.kt
Chroma key with despill: alpha from how magenta a pixel is, colour = observation minus the magenta share (so soft
shadows and glows become clean semi-transparent navy/yellow instead of pink halos). Needs Pillow + numpy."""
import os, sys
import numpy as np
from PIL import Image, ImageFilter

SRC = 'design/logo/originals'
OUT = 'design/logo'
RES = 'app/src/main/res'
T0, T1 = 30.0, 190.0
NO_REPAIR = {'thunderstorm_day'}  # bolt/drops would match the dull-warm rule; that loop is not re-worked
KEY = np.array([255.0, 0.0, 255.0])


def key(path):
    a = np.asarray(Image.open(path).convert('RGB')).astype(np.float64)
    m = np.minimum(a[..., 0], a[..., 2]) - a[..., 1]          # magenta-ness
    alpha = 1.0 - np.clip((m - T0) / (T1 - T0), 0.0, 1.0)
    out = np.zeros(a.shape, np.float64)
    ok = alpha > 0.02
    out[ok] = (a[ok] - (1.0 - alpha[ok, None]) * KEY) / alpha[ok, None]
    rgba = np.dstack([np.clip(out, 0, 255), alpha * 255.0]).astype(np.uint8)
    return Image.fromarray(rgba, 'RGBA')


def crop(im, pad=0.04):
    bbox = im.getchannel('A').point(lambda v: 255 if v > 8 else 0).getbbox()
    l, t, r, b = bbox
    p = int(max(r - l, b - t) * pad)
    return im.crop((max(l - p, 0), max(t - p, 0), min(r + p, im.width), min(b + p, im.height)))


def fit(im, w, h):
    s = min(w / im.width, h / im.height)
    return im.resize((max(1, int(im.width * s)), max(1, int(im.height * s))), Image.LANCZOS)


def on(bg, fg):
    c = Image.new('RGBA', fg.size, bg); c.alpha_composite(fg); return c.convert('RGB')


def weather():
    """16 weather illustrations: keyed, kept on the full 512 canvas (so every icon has the same scale and margin),
    downscaled to 384 and stored as lossless WebP (Android decodes it natively)."""
    src = 'design/weather/originals'
    os.makedirs('design/weather', exist_ok=True)
    names = sorted(f for f in os.listdir(src) if f.startswith('wx_') and f.endswith('.png'))
    thumbs = []
    for n in names:
        im = key(f'{src}/{n}').resize((384, 384), Image.LANCZOS)
        im.save(f'{RES}/drawable-nodpi/{n[:-4]}.webp', format='WEBP', lossless=True, quality=100, method=6)
        thumbs.append((n, im))
    cols = 4
    rows = (len(thumbs) + cols - 1) // cols
    sheet = Image.new('RGB', (cols * 2 * 200, rows * 220), (242, 242, 242))
    dark = Image.new('RGB', (cols * 200, rows * 220), (21, 23, 26))
    sheet.paste(dark, (cols * 200, 0))
    for i, (n, im) in enumerate(thumbs):
        t = im.resize((180, 180), Image.LANCZOS)
        x, y = (i % cols) * 200 + 10, (i // cols) * 220 + 10
        sheet.paste(t, (x, y), t.getchannel('A')); sheet.paste(t, (x + cols * 200, y), t.getchannel('A'))
    sheet.save('design/weather/review_sheet.png')
    print(len(thumbs), 'weather assets')


def repair_dim_sparkles(im, cool=False):
    """ChatGPT dims sparkles/stars by DARKENING them to grey-brown, which looks like a dirty speck on a transparent widget.
    Recolour those dull low-saturation pixels (warm hue 30-60 deg, saturation < 0.45, value 0.25-0.7) to the warm white of the
    approved sparkle; with cool=True also grey-blue night stars (hue 190-250 deg, saturation < 0.35, value 0.25-0.7)."""
    a = np.asarray(im).astype(np.float64)
    r, g, b, al = a[..., 0] / 255, a[..., 1] / 255, a[..., 2] / 255, a[..., 3]
    mx = np.maximum(np.maximum(r, g), b); mn = np.minimum(np.minimum(r, g), b); d = mx - mn
    sat = np.where(mx > 0, d / np.maximum(mx, 1e-9), 0.0)
    h = np.zeros_like(mx)
    nz = d > 1e-9
    rc = nz & (mx == r); gc = nz & (mx == g) & ~rc; bc = nz & ~rc & ~gc
    h[rc] = (60 * ((g - b)[rc] / d[rc])) % 360
    h[gc] = 60 * ((b - r)[gc] / d[gc]) + 120
    h[bc] = 60 * ((r - g)[bc] / d[bc]) + 240
    dull = (mx >= 0.25) & (mx <= 0.70) & (al > 20)
    warm = dull & (h >= 30) & (h <= 60) & (sat >= 0.08) & (sat < 0.45)
    mask = warm
    if cool: mask = mask | (dull & (h >= 190) & (h <= 250) & (sat < 0.35))
    out = a.copy()
    out[mask, 0] = 255; out[mask, 1] = 244; out[mask, 2] = 205
    if cool: out[dull & ~warm & mask, :3] = (232, 240, 255)
    return Image.fromarray(out.astype(np.uint8), 'RGBA'), int(mask.sum())


def anim(src):
    """Animation loops. ChatGPT delivers 12 frames per loop but only ~7 are different (ping-pong); we keep the unique
    frames (320 px, lossy WebP with alpha) and a 12-entry sequence that indexes them. Loops whose frames barely differ
    (changed pixels < 300) are skipped: they would only cost battery for no visible motion."""
    import hashlib, re
    names = sorted({re.match(r'wxa_(.+)_\d\d\.png$', f).group(1) for f in os.listdir(src) if re.match(r'wxa_.+_\d\d\.png$', f)})
    table, skipped = {}, []
    for n in names:
        raw = [np.asarray(Image.open(f'{src}/wxa_{n}_{i:02d}.png').convert('RGB')).astype(int) for i in range(1, 13)]
        moved = max(int((np.abs(r - raw[0]).sum(axis=2) > 40).sum()) for r in raw)
        if moved < 300:
            skipped.append(n); continue
        uniq, seq = [], []
        for i, r in enumerate(raw):
            h = hashlib.md5(r.astype(np.uint8).tobytes()).hexdigest()
            if h not in uniq:
                uniq.append(h)
                im = key(f'{src}/wxa_{n}_{i + 1:02d}.png')
                if n not in NO_REPAIR: im, _ = repair_dim_sparkles(im, cool=n.startswith('clear_night'))
                im = im.resize((320, 320), Image.LANCZOS)
                im.save(f'{RES}/drawable-nodpi/wxa_{n}_{len(uniq)}.webp', format='WEBP', quality=90, alpha_quality=100, method=6)
            seq.append(uniq.index(h) + 1)
        table[n] = seq
    lines = ['package com.clockweather.core.weather', '', '/** GENERATED by scripts/make-art-assets.py anim - do not edit. Unique-frame index (1-based) for each of the 12 frames of a loop. */',
             'internal object WeatherAnimTable {', '    val SEQUENCES: Map<String, IntArray> = mapOf(']
    for n, seq in table.items(): lines.append(f'        "{n}" to intArrayOf({", ".join(map(str, seq))}),')
    lines += ['    )', '}', '']
    open('core/src/main/kotlin/com/clockweather/core/weather/WeatherAnimTable.kt', 'w').write('\n'.join(lines))
    print('animated:', list(table), 'skipped (too little motion):', skipped)


def _flood(mask, seed):
    """Connected region of True pixels containing seed (4-neighbour), numpy only."""
    reg = np.zeros_like(mask); reg[seed] = mask[seed]
    while True:
        n = reg.copy()
        n[1:, :] |= reg[:-1, :]; n[:-1, :] |= reg[1:, :]; n[:, 1:] |= reg[:, :-1]; n[:, :-1] |= reg[:, 1:]
        n &= mask
        if (n == reg).all(): return reg
        reg = n


def _circle(im):
    """Outer circle of a round object from its alpha: widest left/right extremes and the lowest point (crown/bow are narrower)."""
    m = np.asarray(im.getchannel('A')) > 128
    xs = np.where(m.any(0))[0]; ys = np.where(m.any(1))[0]
    r = (xs.max() - xs.min()) / 2.0
    return (xs.min() + xs.max()) / 2.0, ys.max() - r, r


def pocket():
    """Aligns the painted pocket-watch pieces: the dial is scaled into the bezel hole, then the whole open face is scaled and moved so
    its outer circle coincides with the closed case; the crown/bow above the hinge are removed from the face (the case art supplies
    them in every state). Hands are trimmed. Writes res webp files and a generated geometry object (all values are fractions)."""
    O = 'design/pocketwatch/originals'
    case = key(f'{O}/pw_case_closed.png'); bez = key(f'{O}/pw_bezel_gold.png')
    ccx, ccy, cr = _circle(case); bcx, bcy, br = _circle(bez)
    A = np.asarray(bez.getchannel('A'))
    hole = _flood(A < 40, (int(bcy), int(bcx)))
    ys, xs = np.where(hole)
    hcx, hcy, hr = (xs.min() + xs.max()) / 2.0, (ys.min() + ys.max()) / 2.0, ((xs.max() - xs.min()) + (ys.max() - ys.min())) / 4.0
    k2 = cr / br
    hinge_y = ccy - cr * 0.985
    # union box of case + transformed face, same canvas for both
    def place(img, k, ox, oy):
        sz = (int(round(img.width * k)), int(round(img.height * k)))
        return img.resize(sz, Image.LANCZOS), ox, oy
    cw, ch = case.size
    pad = 24
    box = (0, 0, cw, ch)
    def build_face(d, bezel):
        dcx, dcy, dr = _circle(d)
        kd = (hr * 1.04) / dr
        d2 = d.resize((int(round(d.width * kd)), int(round(d.height * kd))), Image.LANCZOS)
        face = Image.new('RGBA', bezel.size, (0, 0, 0, 0))
        face.alpha_composite(d2, (int(round(hcx - dcx * kd)), int(round(hcy - dcy * kd))))
        face.alpha_composite(bezel)
        f2 = face.resize((int(round(face.width * k2)), int(round(face.height * k2))), Image.LANCZOS)
        canvas = Image.new('RGBA', case.size, (0, 0, 0, 0))
        canvas.alpha_composite(f2, (int(round(ccx - bcx * k2)), int(round(ccy - bcy * k2))))
        arr = np.asarray(canvas).copy(); arr[:int(hinge_y), :, 3] = 0   # crown/bow come from the case art
        return Image.fromarray(arr, 'RGBA')
    dial_imgs = {dial: key(f'{O}/pw_dial_{dial}.png') for dial in ('cream', 'ivory', 'black')}
    faces = {dial: build_face(d, bez) for dial, d in dial_imgs.items()}
    # common crop: bbox of the case, plus margin
    cb = case.getchannel('A').point(lambda v: 255 if v > 8 else 0).getbbox()
    crop = (max(cb[0] - pad, 0), max(cb[1] - pad, 0), min(cb[2] + pad, cw), min(cb[3] + pad, ch))
    W, H = crop[2] - crop[0], crop[3] - crop[1]
    out_w = 640; sc = out_w / W; out_h = int(round(H * sc))
    def save(im, name, lossless=False):
        im = im.crop(crop).resize((out_w, out_h), Image.LANCZOS)
        im.save(f'{RES}/drawable-nodpi/{name}.webp', format='WEBP', quality=92, alpha_quality=100, lossless=lossless, method=6)
    save(case, 'pw_case')
    for dial, f in faces.items(): save(f, f'pw_open_{dial}')
    # silver / rose-gold recolours (round 6C): identical silhouettes, so the gold geometry is reused as is
    for metal in ('silver', 'rose'):
        mcase = key(f'{O}/pw_case_{metal}.png'); mbez = key(f'{O}/pw_bezel_{metal}.png')
        save(mcase, f'pw_case_{metal}')
        for dial, d in dial_imgs.items(): save(build_face(d, mbez), f'pw_open_{dial}_{metal}')
    fx = lambda v: round(float(v - crop[0]) * sc / out_w, 5)
    fy = lambda v: round(float(v - crop[1]) * sc / out_h, 5)
    # dial centre = hole centre transformed
    dcx_f = fx(ccx + (hcx - bcx) * k2); dcy_f = fy(ccy + (hcy - bcy) * k2); dr_f = round(float(hr * k2 * sc) / out_w, 5)
    hands = {}
    for n in ('hour', 'minute', 'second'):
        h = key(f'{O}/pw_hand_{n}.png'); bb = h.getchannel('A').point(lambda v: 255 if v > 8 else 0).getbbox()
        t = h.crop(bb); a = np.asarray(t.getchannel('A')) > 128
        widths = a.sum(1); lower = np.arange(len(widths)) > len(widths) * 0.55
        py = int(np.argmax(np.where(lower, widths, -1))) if n != 'second' else int(len(widths) * 0.9)
        px = (a[py].nonzero()[0].min() + a[py].nonzero()[0].max()) / 2.0 if a[py].any() else t.width / 2.0
        t.save(f'{RES}/drawable-nodpi/pw_hand_{n}.webp', format='WEBP', lossless=True, method=6)
        hands[n] = (round(px / t.width, 4), round(py / t.height, 4), t.width, t.height)
    lines = ['package com.clockweather.core.widget', '', '/** GENERATED by scripts/make-art-assets.py pocket - do not edit. Fractions of the pw_case / pw_open_* canvas (they share one canvas). */',
             'object PocketWatchGeometry {',
             f'    const val ASPECT = {out_w / out_h:.5f}f // width / height of the canvas',
             f'    const val HINGE_Y = {fy(hinge_y)}f',
             f'    const val DIAL_CX = {dcx_f}f', f'    const val DIAL_CY = {dcy_f}f', f'    const val DIAL_R = {dr_f}f // of the canvas WIDTH', '']
    for n, (px, py, w, h) in hands.items():
        lines.append(f'    /** pw_hand_{n}: pivot as a fraction of the hand image; image is {w} x {h} px. */')
        lines += [f'    const val {n.upper()}_PIVOT_X = {px}f', f'    const val {n.upper()}_PIVOT_Y = {py}f', f'    const val {n.upper()}_ASPECT = {w / h:.5f}f']
    lines += ['}', '']
    open('core/src/main/kotlin/com/clockweather/core/widget/PocketWatchGeometry.kt', 'w').write('\n'.join(lines))
    # review: open face with hands at 10:10 + closed
    prev = Image.new('RGB', (out_w * 2, out_h), (60, 66, 90))
    for i, nm in enumerate(('cream', 'black')):
        f = faces[nm].crop(crop).resize((out_w, out_h), Image.LANCZOS)
        cvs = Image.new('RGBA', (out_w, out_h), (0, 0, 0, 0)); cvs.alpha_composite(f)
        cvs.alpha_composite(case.crop(crop).resize((out_w, out_h), Image.LANCZOS).crop((0, 0, out_w, int(hinge_y - crop[1]) * out_w // W)).resize((out_w, int(hinge_y - crop[1]) * out_w // W)), (0, 0)) if False else None
        # hands
        import math
        R = dr_f * out_w
        for n, ang, ln in (('hour', 305, 0.55), ('minute', 60, 0.82)):
            px, py, w, h = hands[n]
            t = Image.open(f'{RES}/drawable-nodpi/pw_hand_{n}.webp').convert('RGBA')
            top = py * t.height; s2 = (ln * R) / top
            t2 = t.resize((max(1, int(t.width * s2)), max(1, int(t.height * s2))), Image.LANCZOS)
            pad2 = max(t2.size) * 2
            big = Image.new('RGBA', (pad2, pad2), (0, 0, 0, 0)); big.alpha_composite(t2, (int(pad2 / 2 - px * t2.width), int(pad2 / 2 - py * t2.height)))
            rot = big.rotate(-ang, resample=Image.BICUBIC)
            cvs.alpha_composite(rot, (int(dcx_f * out_w - pad2 / 2), int(dcy_f * out_h - pad2 / 2)))
        prev.paste(cvs.convert('RGB') if False else cvs, (i * out_w, 0), cvs.getchannel('A'))
    prev.save('design/pocketwatch/review_open.png')
    print('pocket assets ok', dcx_f, dcy_f, dr_f, hands)


def drop_specks(im, min_share=0.02):
    """Removes small disconnected fragments (cut-out debris at the sheet edge): components under min_share of the total opaque area."""
    a = np.asarray(im).copy(); m = a[..., 3] > 20; total = int(m.sum()); left = m.copy()
    while left.any():
        ys, xs = np.where(left); seed = (ys[0], xs[0])
        comp = _flood(left, seed)
        if comp.sum() < min_share * total: a[comp, 3] = 0
        left &= ~comp
    return Image.fromarray(a, 'RGBA')


def ui():
    """Widget-picker previews (keyed, tight crop, width 480) and onboarding spot art (keyed, tight crop, width 420)."""
    src = 'design/ui/originals'
    for n in sorted(os.listdir(src)):
        if not n.endswith('.png'): continue
        im = key(f'{src}/{n}')
        if n.startswith('spot_'): im = drop_specks(im)
        im = crop(im, pad=0.02)
        w = 480 if n.startswith('preview_') else 420
        im = im.resize((w, max(1, round(im.height * w / im.width))), Image.LANCZOS)
        im.save(f'{RES}/drawable-nodpi/{n[:-4]}.webp', format='WEBP', quality=90, alpha_quality=100, method=6)
    print('ui assets ok')


def holidays():
    """13 holiday badges: keyed, debris removed, kept on the full 256 canvas (same scale and margin for all)."""
    src = 'design/holidays/originals'
    ims = []
    for n in sorted(os.listdir(src)):
        if not n.endswith('.png'): continue
        im = drop_specks(key(f'{src}/{n}'))
        im.save(f'{RES}/drawable-nodpi/{n[:-4]}.webp', format='WEBP', quality=92, alpha_quality=100, method=6)
        ims.append(im)
    sheet = Image.new('RGB', (256 * 7, 256 * 4), (242, 242, 242))
    sheet.paste(Image.new('RGB', (256 * 7, 256 * 2), (21, 23, 26)), (0, 256 * 2))
    for i, im in enumerate(ims):
        for row in (0, 2):
            sheet.paste(im, ((i % 7) * 256, (row + i // 7) * 256), im.getchannel('A'))
    sheet.save('design/holidays/review_sheet.png')
    print(len(ims), 'holiday badges')


def main():
    if len(sys.argv) > 1 and sys.argv[1] == 'holidays':
        return holidays()
    if len(sys.argv) > 1 and sys.argv[1] == 'ui':
        return ui()
    if len(sys.argv) > 1 and sys.argv[1] == 'pocket':
        return pocket()
    if len(sys.argv) > 2 and sys.argv[1] == 'anim':
        return anim(sys.argv[2])
    if len(sys.argv) > 1 and sys.argv[1] == 'weather':
        return weather()
    os.makedirs(OUT, exist_ok=True)
    mark = crop(key(f'{SRC}/logo_mark.png'))
    lock_l = crop(key(f'{SRC}/logo_lockup_light.png'))
    lock_d = crop(key(f'{SRC}/logo_lockup_dark.png'))
    word_l = crop(key(f'{SRC}/logo_wordmark_light.png'))
    word_d = crop(key(f'{SRC}/logo_wordmark_dark.png'))
    for name, im in (('mark', mark), ('lockup_light', lock_l), ('lockup_dark', lock_d), ('wordmark_light', word_l), ('wordmark_dark', word_d)):
        im.save(f'{OUT}/{name}.png')

    # in-app brand lockups (width 640)
    for name, im in (('brand_lockup_light', lock_l), ('brand_lockup_dark', lock_d)):
        fit(im, 640, 640).save(f'{RES}/drawable-nodpi/{name}.png', optimize=True)

    # adaptive icon foreground: mark inside the 66 % safe zone of 432 x 432
    fg = Image.new('RGBA', (432, 432), (0, 0, 0, 0))
    m = fit(mark, 276, 276)
    fg.alpha_composite(m, ((432 - m.width) // 2, (432 - m.height) // 2))
    fg.save(f'{RES}/drawable-nodpi/ic_launcher_fg.png', optimize=True)

    # monochrome layer: white silhouette of the mark, digits and hinge seam cut out so it stays readable
    arr = np.asarray(m).astype(np.float64)
    dark = (arr[..., :3].sum(axis=2) < 200) & (arr[..., 3] > 128)        # navy digits
    alpha = arr[..., 3].copy(); alpha[dark] = 0
    mono_core = np.dstack([np.full(alpha.shape, 255.0), np.full(alpha.shape, 255.0), np.full(alpha.shape, 255.0), alpha]).astype(np.uint8)
    mono = Image.new('RGBA', (432, 432), (0, 0, 0, 0))
    mono.alpha_composite(Image.fromarray(mono_core, 'RGBA'), ((432 - m.width) // 2, (432 - m.height) // 2))
    mono.save(f'{RES}/drawable-nodpi/ic_launcher_mono.png', optimize=True)

    # review sheet: light, dark, wallpaper gradient
    sheet = Image.new('RGB', (1500, 900), (240, 240, 240))
    wall = np.zeros((900, 500, 3), np.uint8)
    for y in range(900):
        wall[y] = (np.array([27, 42, 68]) * (1 - y / 900) + np.array([224, 138, 77]) * (y / 900)).astype(np.uint8)
    sheet.paste(Image.new('RGB', (500, 900), (242, 242, 242)), (0, 0))
    sheet.paste(Image.new('RGB', (500, 900), (21, 23, 26)), (500, 0))
    sheet.paste(Image.fromarray(wall), (1000, 0))
    for i, bg in enumerate(((242, 242, 242, 255), (21, 23, 26, 255), None)):
        x0 = i * 500
        for im, y, w in ((fit(lock_l if i == 0 else lock_d, 460, 200), 40, 460), (fit(mark, 300, 300), 300, 300), (fit(mark, 96, 96), 640, 96), (fit(mark, 48, 48), 640, 48)):
            sheet.paste(im.convert('RGB') if False else Image.alpha_composite(Image.new('RGBA', im.size, bg if bg else (0, 0, 0, 0)), im).convert('RGB') if bg else im, (x0 + 20, y), im.getchannel('A'))
    # icon preview: circle mask over gradient background
    sheet.save(f'{OUT}/review_sheet.png')
    print('ok')


if __name__ == '__main__':
    sys.exit(main())
