"""Turns the ChatGPT logo sheets (flat #FF00FF background, design/logo/originals) into transparent PNGs and
the Android launcher / brand drawables. Run:  python3 -I scripts/make-logo-assets.py
Chroma key with despill: alpha from how magenta a pixel is, colour = observation minus the magenta share (so soft
shadows and glows become clean semi-transparent navy/yellow instead of pink halos). Needs Pillow + numpy."""
import os, sys
import numpy as np
from PIL import Image, ImageFilter

SRC = 'design/logo/originals'
OUT = 'design/logo'
RES = 'app/src/main/res'
T0, T1 = 30.0, 190.0
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


def main():
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
