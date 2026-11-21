package com.clockweather.app.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import com.clockweather.core.agenda.Agenda
import com.clockweather.core.alarm.AlarmIndicator
import com.clockweather.core.clock.ClockZone
import com.clockweather.core.clock.FlipMath
import com.clockweather.core.clock.Freshness
import com.clockweather.core.clock.WorldClock
import com.clockweather.core.hebrew.HebrewDateFormat
import com.clockweather.core.hebrew.HolidayBadge
import com.clockweather.core.hebrew.Holidays
import com.clockweather.core.sun.SunTimes
import com.clockweather.core.timers.TimerFormat
import com.clockweather.core.widget.PocketMetal
import com.clockweather.core.sun.Zmanim
import com.clockweather.core.weather.TempFormat
import com.clockweather.core.weather.WeatherOutlook
import com.clockweather.core.widget.ClockStyle
import com.clockweather.core.widget.Palette
import com.clockweather.core.widget.Palettes
import com.clockweather.core.widget.PocketWatchGeometry
import com.clockweather.core.widget.Section
import com.clockweather.core.widget.WidgetLayout
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

/**
 * Draws one frame of the widget into a Bitmap (transparent canvas). The home-screen widget and the in-app preview both
 * call this, which is what keeps them identical (parity rule). Decisions about WHAT to show come from :core
 * (WidgetLayout, Agenda, AlarmIndicator, Holidays); this class only paints. It must not read settings or data itself.
 */
object WidgetRenderer {
    private const val MAX_PIXELS = 1_600_000

    fun render(ctx: Context, widthPx: Int, heightPx: Int, density: Float, st: RenderState): Bitmap {
        var w = widthPx.coerceAtLeast(2); var h = heightPx.coerceAtLeast(2); var d = density
        val px = w.toLong() * h
        if (px > MAX_PIXELS) { val k = Math.sqrt(MAX_PIXELS.toDouble() / px).toFloat(); w = (w * k).toInt(); h = (h * k).toInt(); d *= k }
        val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        Painter(ctx, Canvas(bmp), d, st, w / d, h / d).paintAll()
        return bmp
    }

    private class Painter(val ctx: Context, val c: Canvas, val d: Float, val st: RenderState, val wDp: Float, val hDp: Float) {
        val s = st.kind.shape(st.settings)
        val pal: Palette = Palettes.byId(s.paletteId) ?: Palettes.DEFAULT
        val rtl = st.hebrew
        val local = st.now.atZone(st.zone)
        val p = Paint(Paint.ANTI_ALIAS_FLAG)
        val use24 = s.use24h
        val ink = pal.panelText

        fun dp(v: Float) = v * d

        fun paintAll() {
            val plan = WidgetLayout.plan(wDp.toInt(), hDp.toInt(), st.settings, st.kind)
            if (s.backgroundAlphaPct > 0) {
                p.style = Paint.Style.FILL; p.color = withAlpha(pal.panel, s.backgroundAlphaPct)
                c.drawRoundRect(RectF(0f, 0f, c.width.toFloat(), c.height.toFloat()), dp(s.cornerRadiusDp.toFloat()), dp(s.cornerRadiusDp.toFloat()), p)
            }
            var y = 0f
            for (sec in plan.sections) {
                val hh = if (sec == plan.flex) plan.clockHeightDp.toFloat() else WidgetLayout.costDp(sec).toFloat()
                val r = RectF(dp(4f), dp(y), dp(wDp - 4f), dp(y + hh))
                when (sec) {
                    Section.CLOCK -> drawClock(r)
                    Section.DATE_LINE -> drawDateLine(r)
                    Section.WEATHER_PANEL -> drawWeatherPanel(r)
                    Section.ALARM_CHIP -> drawAlarm(r)
                    Section.TIMERS -> drawTimers(r)
                    Section.AGENDA_BANNER -> drawAgenda(r)
                    Section.WORLD_CLOCKS -> drawWorld(r)
                    Section.FOOTER -> drawFooter(r)
                    Section.STRIP -> drawStrip(r)
                }
                y += hh
            }
            if (s.showSettingsButton && wDp >= 150f && hDp >= 90f) drawGear()
        }

        /** A small, quiet gear in the top corner (the matching tap zone opens Settings). */
        fun drawGear() {
            p.reset(); p.isAntiAlias = true
            p.color = withAlpha(ink, 55); p.textSize = dp(14f); p.textAlign = if (rtl) Paint.Align.LEFT else Paint.Align.RIGHT
            c.drawText("\u2699", if (rtl) dp(8f) else c.width - dp(8f), dp(17f), p)
        }

        // ---------- clock ----------
        fun digits() = WorldClock.digits(local.hour, local.minute, use24)

        fun drawClock(r: RectF) {
            when (s.clockStyle) {
                ClockStyle.FLIP -> drawFlip(r)
                ClockStyle.DIGITAL -> drawBigText(r, timeText(), Typeface.create("sans-serif-thin", Typeface.NORMAL))
                ClockStyle.MINIMAL -> drawBigText(RectF(r.left, r.top, r.right, r.top + r.height() * 0.7f), timeText(), Typeface.create("sans-serif-light", Typeface.NORMAL))
                ClockStyle.ANALOG -> drawAnalog(r)
                ClockStyle.POCKET -> drawPocketWatch(r)
            }
        }

        fun timeText(): String {
            val dg = digits()
            val hh = if (use24) "${dg[0]}${dg[1]}" else "${dg[0] * 10 + dg[1]}"
            return "$hh:${dg[2]}${dg[3]}"
        }

        fun drawBigText(r: RectF, text: String, tf: Typeface) {
            p.reset(); p.isAntiAlias = true; p.typeface = tf; p.color = ink
            p.setShadowLayer(dp(3f), 0f, dp(1.5f), 0x66000000)
            p.textAlign = Paint.Align.CENTER
            var size = r.height() * 0.82f
            p.textSize = size
            val maxW = r.width() * 0.96f
            if (p.measureText(text) > maxW) { size *= maxW / p.measureText(text); p.textSize = size }
            val fm = p.fontMetrics
            c.drawText(text, r.centerX(), r.centerY() - (fm.ascent + fm.descent) / 2f, p)
        }

        fun drawFlip(r: RectF) {
            val (cwDp, chDp) = WidgetLayout.cardSizeDp((r.width() / d).toInt(), (r.height() / d).toInt(), s.scalePct)
            val cw = dp(cwDp.toFloat()); val ch = dp(chDp.toFloat()).coerceAtMost(r.height())
            val gap = dp(8f)
            val left = r.centerX() - (cw * 2 + gap) / 2f
            val top = r.centerY() - ch / 2f
            val now = digits()
            val from = st.flipFrom
            val pairs = listOf(0 to 1, 2 to 3)
            pairs.forEachIndexed { i, (a, b) ->
                val box = RectF(left + i * (cw + gap), top, left + i * (cw + gap) + cw, top + ch)
                val newText = if (use24 || i == 1) "${now[a]}${now[b]}" else "${now[a] * 10 + now[b]}".padStart(2, ' ').trim()
                val animate = from != null && st.flipProgress < 1f && (from[a] != now[a] || from[b] != now[b])
                if (!animate) { drawHalf(box, newText, top = true, scale = 1f); drawHalf(box, newText, top = false, scale = 1f); drawHinge(box) }
                else {
                    val oldText = if (use24 || i == 1) "${from!![a]}${from[b]}" else "${from!![a] * 10 + from[b]}"
                    // static halves: new top, old bottom; moving halves: old top folding down, new bottom unfolding
                    drawHalf(box, newText, top = true, scale = 1f)
                    drawHalf(box, oldText, top = false, scale = 1f)
                    val pr = st.flipProgress
                    val fold = FlipMath.oldTopScale(pr); val unfold = FlipMath.newBottomScale(pr)
                    if (fold > 0f) drawHalf(box, oldText, top = true, scale = fold, shade = 1f - fold)
                    if (unfold > 0f) drawHalf(box, newText, top = false, scale = unfold, shade = 1f - unfold)
                    drawHinge(box)
                }
            }
            if (!use24 && s.showAmPm) {
                p.reset(); p.isAntiAlias = true; p.color = ink; p.textSize = dp(11f); p.textAlign = Paint.Align.LEFT; p.setShadowLayer(dp(2f), 0f, dp(1f), 0x66000000)
                c.drawText(if (local.hour < 12) "AM" else "PM", left + dp(6f), top + ch - dp(6f), p)
            }
        }

        fun drawHalf(box: RectF, text: String, top: Boolean, scale: Float, shade: Float = 0f) {
            if (scale <= 0f) return
            val cy = box.centerY()
            c.save()
            c.scale(1f, scale, box.centerX(), cy)
            val clip = if (top) RectF(box.left, box.top, box.right, cy) else RectF(box.left, cy, box.right, box.bottom)
            c.clipRect(clip)
            val rad = box.width() * 0.12f
            p.reset(); p.isAntiAlias = true
            p.setShadowLayer(dp(4f), 0f, dp(2f), 0x55000000)
            p.color = if (top) pal.cardTop else pal.cardBottom
            c.drawRoundRect(box, rad, rad, p)
            p.clearShadowLayer()
            p.shader = if (top) LinearGradient(0f, box.top, 0f, cy, withAlpha(0xFFFFFFFF.toInt(), 18), 0x00FFFFFF, Shader.TileMode.CLAMP)
            else LinearGradient(0f, cy, 0f, box.bottom, 0x00000000, withAlpha(0xFF000000.toInt(), 12), Shader.TileMode.CLAMP)
            c.drawRoundRect(box, rad, rad, p)
            p.shader = null
            p.typeface = Typeface.create("sans-serif-light", Typeface.NORMAL)
            p.color = pal.digit; p.textAlign = Paint.Align.CENTER
            p.textSize = box.height() * 0.74f
            while (p.measureText(text) > box.width() * 0.9f && p.textSize > dp(8f)) p.textSize *= 0.95f
            val fm = p.fontMetrics
            c.drawText(text, box.centerX(), cy - (fm.ascent + fm.descent) / 2f, p)
            if (shade > 0f) { p.color = withAlpha(0xFF000000.toInt(), (shade * 45).toInt()); c.drawRect(clip, p) }
            c.restore()
        }

        fun drawHinge(box: RectF) {
            p.reset(); p.isAntiAlias = true; p.color = pal.hinge; p.strokeWidth = dp(1.5f)
            c.drawLine(box.left, box.centerY(), box.right, box.centerY(), p)
        }

        fun drawAnalog(r: RectF) {
            val rad = (minOf(r.width(), r.height()) / 2f) * 0.92f * (s.scalePct.coerceIn(50, 150) / 100f).coerceAtMost(1.08f)
            val cx = r.centerX(); val cy = r.centerY()
            p.reset(); p.isAntiAlias = true; p.color = withAlpha(pal.cardTop, 92); p.setShadowLayer(dp(5f), 0f, dp(2f), 0x55000000)
            c.drawCircle(cx, cy, rad, p); p.clearShadowLayer()
            p.color = pal.digit; p.strokeCap = Paint.Cap.ROUND
            for (i in 0 until 12) {
                val a = Math.toRadians(i * 30.0 - 90.0); val big = i % 3 == 0
                p.strokeWidth = dp(if (big) 2.4f else 1.2f)
                val r1 = rad * (if (big) 0.8f else 0.88f); val r2 = rad * 0.95f
                c.drawLine(cx + cos(a).toFloat() * r1, cy + sin(a).toFloat() * r1, cx + cos(a).toFloat() * r2, cy + sin(a).toFloat() * r2, p)
            }
            val min = local.minute + local.second / 60f
            val hr = (local.hour % 12) + min / 60f
            hand(cx, cy, rad * 0.5f, hr * 30f, dp(4.5f))
            hand(cx, cy, rad * 0.78f, min * 6f, dp(3f))
            p.color = pal.accent; c.drawCircle(cx, cy, dp(4.5f), p)
        }

        fun hand(cx: Float, cy: Float, len: Float, deg: Float, width: Float, color: Int = pal.digit) {
            val a = Math.toRadians(deg - 90.0)
            p.color = color; p.strokeWidth = width; p.strokeCap = Paint.Cap.ROUND
            c.drawLine(cx, cy, cx + cos(a).toFloat() * len, cy + sin(a).toFloat() * len, p)
        }

        /** Dial variant per palette: dark palettes get the black dial, cool/green the ivory one, the rest cream. */
        fun dialName(): String = when (pal.id) { "graphite", "midnight" -> "black"; "ocean", "forest", "classic_white" -> "ivory"; else -> "cream" }

        /**
         * The golden pocket watch from the approved painted art (pw_case = closed hunter case, pw_open_<dial> = open face on the SAME
         * canvas, hands are separate images rotated here). Geometry fractions come from the generated PocketWatchGeometry.
         * The lid opens on the hinge by squashing the lower part of pw_case towards the hinge line (lidOpen 0 -> 1).
         */
        fun drawPocketWatch(r: RectF) {
            val metal = PocketMetal.byId(s.pocketMetal).suffix
            val face = ArtCache.get(ctx, "pw_open_" + dialName() + metal) ?: ArtCache.get(ctx, "pw_open_" + dialName())
            val case = ArtCache.get(ctx, "pw_case" + metal) ?: ArtCache.get(ctx, "pw_case")
            if (face == null || case == null) { drawPocketPlaceholder(r); return }
            val g = PocketWatchGeometry
            var h = r.height(); var w = h * g.ASPECT
            if (w > r.width()) { w = r.width(); h = w / g.ASPECT }
            val dst = RectF(r.centerX() - w / 2f, r.centerY() - h / 2f, r.centerX() + w / 2f, r.centerY() + h / 2f)
            p.reset(); p.isAntiAlias = true; p.isFilterBitmap = true
            val hingeY = dst.top + g.HINGE_Y * h
            // open face (without crown/bow) + live hands
            c.drawBitmap(face, null, dst, p)
            val dcx = dst.left + g.DIAL_CX * w; val dcy = dst.top + g.DIAL_CY * h; val dr = g.DIAL_R * w
            val minute = local.minute + local.second / 60f
            val hour = (local.hour % 12) + minute / 60f
            if (st.lidOpen >= 0.5f) {
                drawHand("pw_hand_hour", g.HOUR_PIVOT_X, g.HOUR_PIVOT_Y, dcx, dcy, hour * 30f, dr * 0.58f)
                drawHand("pw_hand_minute", g.MINUTE_PIVOT_X, g.MINUTE_PIVOT_Y, dcx, dcy, minute * 6f, dr * 0.86f)
            }
            // crown + bow ring (the part of the case above the hinge) always stay
            c.save(); c.clipRect(dst.left, dst.top, dst.right, hingeY); c.drawBitmap(case, null, dst, p); c.restore()
            // the lid: lower part of the case squashed towards the hinge
            val open = st.lidOpen.coerceIn(0f, 1f)
            val squash = Math.cos(open * Math.PI / 2.0).toFloat() // 1 closed -> 0 open
            if (squash > 0.02f) {
                c.save(); c.clipRect(dst.left, hingeY, dst.right, dst.bottom)
                c.translate(0f, hingeY); c.scale(1f, squash); c.translate(0f, -hingeY)
                c.drawBitmap(case, null, dst, p)
                c.restore()
            }
        }

        fun drawHand(name: String, pivotX: Float, pivotY: Float, cx: Float, cy: Float, deg: Float, lengthPx: Float) {
            val bmp = ArtCache.get(ctx, name) ?: return
            val sc = lengthPx / (pivotY * bmp.height)
            p.reset(); p.isAntiAlias = true; p.isFilterBitmap = true
            p.setShadowLayer(dp(1.5f), dp(0.5f), dp(1f), 0x55000000)
            c.save(); c.translate(cx, cy); c.rotate(deg); c.scale(sc, sc)
            c.drawBitmap(bmp, -pivotX * bmp.width, -pivotY * bmp.height, p)
            c.restore()
        }

        /** Used only if the art is missing from the build: a plain gold disc watch. */
        fun drawPocketPlaceholder(r: RectF) {
            val gold = 0xFFE0A800.toInt(); val cream = 0xFFFFF4D6.toInt(); val brown = 0xFF3B2F1E.toInt()
            val rad = minOf(r.width(), r.height()) / 2f * 0.92f
            val cx = r.centerX(); val cy = r.centerY()
            p.reset(); p.isAntiAlias = true; p.color = gold; c.drawCircle(cx, cy, rad, p)
            p.color = cream; c.drawCircle(cx, cy, rad * 0.88f, p)
            val min = local.minute + local.second / 60f
            val hr = (local.hour % 12) + min / 60f
            hand(cx, cy, rad * 0.45f, hr * 30f, dp(4f), brown); hand(cx, cy, rad * 0.68f, min * 6f, dp(2.6f), brown)
        }

        // ---------- text helpers ----------
        fun fitText(text: String, box: RectF, color: Int, sizeDp: Float, bold: Boolean = false, align: Paint.Align = Paint.Align.CENTER, shadow: Boolean = true) {
            p.reset(); p.isAntiAlias = true; p.color = color
            p.typeface = Typeface.create("sans-serif", if (bold) Typeface.BOLD else Typeface.NORMAL)
            p.textSize = dp(sizeDp)
            if (shadow) p.setShadowLayer(dp(2f), 0f, dp(1f), 0x66000000)
            while (p.measureText(text) > box.width() && p.textSize > dp(8f)) p.textSize *= 0.94f
            var t = text
            if (p.measureText(t) > box.width()) { while (t.length > 1 && p.measureText("$t…") > box.width()) t = t.dropLast(1); t = "$t…" }
            p.textAlign = align
            val fm = p.fontMetrics
            val x = when (align) { Paint.Align.LEFT -> box.left; Paint.Align.RIGHT -> box.right; else -> box.centerX() }
            c.drawText(t, x, box.centerY() - (fm.ascent + fm.descent) / 2f, p)
        }

        fun startAlign() = if (rtl) Paint.Align.RIGHT else Paint.Align.LEFT
        fun endAlign() = if (rtl) Paint.Align.LEFT else Paint.Align.RIGHT

        fun pill(r: RectF, alphaPct: Int = s.panelAlphaPct) {
            p.reset(); p.isAntiAlias = true; p.color = withAlpha(pal.panel, alphaPct)
            c.drawRoundRect(r, r.height() / 2f, r.height() / 2f, p)
        }

        // ---------- sections ----------
        fun drawDateLine(r: RectF) {
            val loc = if (st.hebrew) Locale("he") else Locale.ENGLISH
            val parts = ArrayList<String>()
            if (s.showGregorianDate) parts += DateTimeFormatter.ofPattern(if (st.hebrew) "EEEE, d בMMMM" else "EEE, d MMM", loc).format(local)
            val sunset = st.forecast?.today(st.now)?.sunset?.toLocalTime()
            val hd = HebrewDateFormat.hebrewDateAt(local.toLocalDate(), local.toLocalTime(), sunset, s.rollHebrewAtSunset)
            if (s.showHebrewDate) parts += HebrewDateFormat.full(hd, st.hebrew, withYear = false)
            val box = RectF(r.left, r.top, r.right, r.bottom)
            val holiday = if (s.showHolidays) Holidays.on(local.toLocalDate(), s.inIsrael).firstOrNull() else null
            val line = (parts + listOfNotNull(holiday?.name(st.hebrew))).joinToString("  ·  ")
            val badge = holiday?.let { h -> HolidayBadge.drawableKey(h.id)?.let { ArtCache.get(ctx, "hol_$it") } }
            if (badge == null) { fitText(line, box, ink, 12f, bold = false); return }
            // the holiday badge sits at the start of the line; the text fits in the remaining width
            val side = minOf(box.height(), dp(22f)); val gap = dp(6f)
            val textW = p.also { it.reset(); it.textSize = dp(12f) }.measureText(line).coerceAtMost(box.width() - side - gap)
            val total = side + gap + textW
            val left = box.centerX() - total / 2f
            val badgeRect = if (rtl) RectF(left + total - side, box.centerY() - side / 2f, left + total, box.centerY() + side / 2f)
            else RectF(left, box.centerY() - side / 2f, left + side, box.centerY() + side / 2f)
            p.reset(); p.isAntiAlias = true; p.isFilterBitmap = true
            c.drawBitmap(badge, null, badgeRect, p)
            val textBox = if (rtl) RectF(left, box.top, left + total - side - gap, box.bottom) else RectF(left + side + gap, box.top, left + total, box.bottom)
            fitText(line, textBox, ink, 12f, bold = false, align = startAlign())
        }

        fun drawWeatherPanel(r: RectF) {
            val f = st.forecast
            val box = RectF(r.left, r.top + dp(2f), r.right, r.bottom - dp(2f))
            p.reset(); p.isAntiAlias = true; p.color = withAlpha(pal.panel, s.panelAlphaPct)
            c.drawRoundRect(box, dp(14f), dp(14f), p)
            val day = f?.today(st.now)
            val cur = f?.current
            if (f == null || day == null) {
                fitText(if (st.hebrew) "אין נתוני מזג אוויר" else "No weather data", box, pal.panelText, 12f, shadow = false)
                return
            }
            val iconSize = minOf(box.height() - dp(10f), box.width() * 0.34f)
            val iconTop = box.centerY() - iconSize / 2f
            val iconBox = if (rtl) RectF(box.right - iconSize - dp(6f), iconTop, box.right - dp(6f), iconTop + iconSize)
            else RectF(box.left + dp(6f), iconTop, box.left + dp(6f) + iconSize, iconTop + iconSize)
            WeatherArt.draw(ctx, c, cur?.sky ?: day.sky, cur?.isDay ?: true, iconBox, st.animMs)
            val textLeft = if (rtl) box.left + dp(8f) else iconBox.right + dp(6f)
            val textRight = if (rtl) iconBox.left - dp(6f) else box.right - dp(8f)
            val tempW = dp(70f)
            val tempBox = if (rtl) RectF(textLeft, box.top, textLeft + tempW, box.centerY() + dp(4f)) else RectF(textRight - tempW, box.top, textRight, box.centerY() + dp(4f))
            val nameBox = if (rtl) RectF(textLeft + tempW + dp(4f), box.top + dp(4f), textRight, box.centerY()) else RectF(textLeft, box.top + dp(4f), textRight - tempW - dp(4f), box.centerY())
            val place = s.places.firstOrNull { it.id == s.activePlaceId }?.name ?: ""
            val now = cur?.tempC ?: day.maxC
            fitText(TempFormat.short(now, s.fahrenheit), tempBox, pal.panelText, 26f, bold = true, align = endAlign(), shadow = false)
            fitText(place, nameBox, pal.panelText, 15f, bold = true, align = startAlign(), shadow = false)
            val lowerLeft = if (rtl) RectF(textLeft + tempW + dp(4f), box.centerY(), textRight, box.bottom - dp(3f)) else RectF(textLeft, box.centerY(), textRight - tempW - dp(4f), box.bottom - dp(3f))
            val outlook = WeatherOutlook.of(day, s.rainLikelyFromPct)
            val age = Freshness.ageMinutes(f.fetchedAtEpochSec, st.now.epochSecond)
            val staleNote = if (Freshness.level(age) != Freshness.Level.FRESH && f.fetchedAtEpochSec > 0) " · " + Freshness.label(age, st.hebrew) else ""
            val line = (if (s.showOutlook) WeatherOutlook.text(outlook, day.rainChancePct, st.hebrew) else (cur?.sky ?: day.sky).label(st.hebrew)) + staleNote
            fitText(line, lowerLeft, withAlpha(pal.panelText, 85), 11.5f, align = startAlign(), shadow = false)
            if (s.showHiLo) {
                val hiLo = RectF(if (rtl) textLeft else textRight - tempW, box.centerY(), if (rtl) textLeft + tempW else textRight, box.bottom - dp(3f))
                val txt = "${TempFormat.short(day.maxC, s.fahrenheit)}/${TempFormat.short(day.minC, s.fahrenheit)}"
                fitText(txt, hiLo, withAlpha(pal.panelText, 85), 12f, align = endAlign(), shadow = false)
            }
        }

        fun drawAlarm(r: RectF) {
            val a = st.nextAlarm
            if (!AlarmIndicator.visible(a, st.now, s.alarmShowWithinHours, s.alarmPackage) || a == null) return
            val label = "⏰ " + AlarmIndicator.label(a, st.now, st.zone, use24, st.hebrew)
            fitText(label, RectF(r.left, r.top, r.right, r.bottom), ink, 12f, bold = true)
        }

        /** One quiet row: stopwatch and/or countdown. The widget redraws once a minute, so only minutes are shown. */
        fun drawTimers(r: RectF) {
            val nowMs = st.now.toEpochMilli()
            val parts = listOfNotNull(
                st.stopwatch?.let { TimerFormat.widgetStopwatch(it.elapsed(nowMs), st.hebrew) },
                st.countdown?.let { TimerFormat.widgetCountdown(it, nowMs, st.hebrew) },
            )
            if (parts.isNotEmpty()) fitText(parts.joinToString("    "), RectF(r.left, r.top, r.right, r.bottom), ink, 12f, bold = true)
        }

        fun drawAgenda(r: RectF) {
            val today = Agenda.forToday(st.agenda, st.now, st.zone, s.agendaIncludeAllDay)
            if (r.height() > dp(64f)) { drawAgendaList(r, today); return }
            val e = Agenda.current(today, st.now, s.agendaRotateSeconds) ?: return
            val box = RectF(r.left + dp(6f), r.top + dp(2f), r.right - dp(6f), r.bottom - dp(2f))
            pill(box)
            var text = Agenda.bannerText(e, st.now, st.zone, use24, st.hebrew)
            val more = Agenda.moreCount(today)
            if (more > 0) text += "   +$more"
            fitText(text, RectF(box.left + dp(10f), box.top, box.right - dp(10f), box.bottom), pal.panelText, 12f, shadow = false)
        }

        /** Tall agenda widget: the day's events as rows, ongoing first. */
        fun drawAgendaList(r: RectF, today: List<com.clockweather.core.agenda.AgendaEvent>) {
            val rowH = dp(30f)
            val rows = (r.height() / rowH).toInt().coerceAtLeast(1)
            if (today.isEmpty()) { fitText(if (st.hebrew) "אין אירועים להיום" else "No events today", r, ink, 13f); return }
            today.take(rows).forEachIndexed { i, e ->
                val box = RectF(r.left + dp(6f), r.top + i * rowH + dp(2f), r.right - dp(6f), r.top + (i + 1) * rowH - dp(2f))
                pill(box)
                fitText(Agenda.bannerText(e, st.now, st.zone, use24, st.hebrew), RectF(box.left + dp(10f), box.top, box.right - dp(10f), box.bottom), pal.panelText, 12f, align = startAlign(), shadow = false)
            }
        }

        /** The slim strip: time on the start side, date + outlook in the middle, icon and temperature on the end side. */
        fun drawStrip(r: RectF) {
            val f = st.forecast; val day = f?.today(st.now); val cur = f?.current
            val third = r.width() * 0.34f
            val timeBox = if (rtl) RectF(r.right - third, r.top, r.right, r.bottom) else RectF(r.left, r.top, r.left + third, r.bottom)
            val endBox = if (rtl) RectF(r.left, r.top, r.left + third, r.bottom) else RectF(r.right - third, r.top, r.right, r.bottom)
            val mid = RectF(if (rtl) endBox.right else timeBox.right, r.top, if (rtl) timeBox.left else endBox.left, r.bottom)
            drawBigText(timeBox, timeText(), Typeface.create("sans-serif-light", Typeface.NORMAL))
            val dateRows = ArrayList<String>()
            if (s.showGregorianDate) dateRows += DateTimeFormatter.ofPattern(if (st.hebrew) "EEE, d בMMM" else "EEE, d MMM", if (st.hebrew) Locale("he") else Locale.ENGLISH).format(local)
            if (s.showHebrewDate) dateRows += HebrewDateFormat.full(HebrewDateFormat.hebrewDateAt(local.toLocalDate(), local.toLocalTime(), day?.sunset?.toLocalTime(), s.rollHebrewAtSunset), st.hebrew, withYear = false)
            val half = mid.height() / 2f
            dateRows.take(2).forEachIndexed { i, t -> fitText(t, RectF(mid.left, mid.top + i * half, mid.right, mid.top + (i + 1) * half), ink, 11.5f) }
            if (day != null && s.showWeather) {
                val icon = RectF(endBox.left, endBox.top + dp(4f), endBox.left + endBox.height() - dp(8f), endBox.bottom - dp(4f))
                val iconBox = if (rtl) RectF(endBox.left, icon.top, endBox.left + icon.height(), icon.bottom) else RectF(endBox.right - icon.height(), icon.top, endBox.right, icon.bottom)
                WeatherArt.draw(ctx, c, cur?.sky ?: day.sky, cur?.isDay ?: true, iconBox, st.animMs)
                val tBox = if (rtl) RectF(iconBox.right, endBox.top, endBox.right, endBox.bottom) else RectF(endBox.left, endBox.top, iconBox.left, endBox.bottom)
                fitText(TempFormat.short(cur?.tempC ?: day.maxC, s.fahrenheit), tBox, ink, 20f, bold = true)
            }
        }

        fun drawWorld(r: RectF) {
            val zones = s.worldClocks.map { ClockZone(it.zoneId, it.label) }
            val lines = zones.mapNotNull { WorldClock.line(st.now, st.zone, it, use24) }
            if (lines.isEmpty()) return
            val cell = r.width() / lines.size
            lines.forEachIndexed { i, ln ->
                val shift = WorldClock.shiftText(ln.dayShift, st.hebrew)
                val txt = "${ln.label} ${ln.time}" + if (shift.isNotEmpty()) " ($shift)" else ""
                fitText(txt, RectF(r.left + i * cell, r.top, r.left + (i + 1) * cell, r.bottom), ink, 11.5f)
            }
        }

        fun drawFooter(r: RectF) {
            val parts = ArrayList<String>()
            if (s.showSunTimes) {
                val day = st.forecast?.today(st.now)
                val sr = day?.sunrise?.toLocalTime(); val ss = day?.sunset?.toLocalTime()
                val calc = if (sr == null || ss == null) s.places.firstOrNull { it.id == s.activePlaceId }?.let { SunTimes.compute(local.toLocalDate(), it.lat, it.lon, st.zone) } else null
                val rise = sr ?: calc?.sunrise; val set = ss ?: calc?.sunset
                val fmt = DateTimeFormatter.ofPattern(if (use24) "HH:mm" else "h:mm a", Locale.ENGLISH)
                if (rise != null && set != null) parts += "☀↑ ${fmt.format(rise)}   ☀↓ ${fmt.format(set)}"
                if (s.showNightfall) s.places.firstOrNull { it.id == s.activePlaceId }?.let { p ->
                    Zmanim.of(local.toLocalDate(), p.lat, p.lon, st.zone)?.let { z ->
                        if (Zmanim.isCandleLightingDay(local.toLocalDate())) parts += "🕯 ${fmt.format(z.candleLighting)}"
                        z.nightfall?.let { parts += "🌙 ${fmt.format(it)}" }
                    }
                }
            }
            if (s.showSteps && st.steps != null) parts += "👣 ${"%,d".format(st.steps)}"
            if (parts.isNotEmpty()) fitText(parts.joinToString("    "), r, ink, 11.5f)
        }

        fun withAlpha(color: Int, pct: Int): Int = (color and 0x00FFFFFF) or ((pct.coerceIn(0, 100) * 255 / 100) shl 24)
    }
}
