package com.clockweather.app.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.clockweather.core.weather.Sky
import com.clockweather.core.weather.WeatherAnim

/**
 * Weather illustration for a [Sky]. Uses the ChatGPT-made PNG `wx_<sky>_<day|night>` from res/drawable-nodpi when it
 * exists (DESIGN_LANGUAGE asset contract); until then it draws a plain geometric PLACEHOLDER (not final art).
 * Must not touch settings or data: it only paints.
 */
object WeatherArt {
    private val cache = HashMap<String, Bitmap?>()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    /** [animMs] >= 0 plays the loop (`wxa_<key>_<n>`) when one exists for this sky, else the still `wx_<key>`. */
    fun draw(ctx: Context, c: Canvas, sky: Sky, isDay: Boolean, box: RectF, animMs: Long = -1L) {
        val key = WeatherAnim.key(sky, isDay)
        val frame = if (animMs >= 0) WeatherAnim.frameAt(key, animMs) else null
        val name = if (frame != null) "wxa_${key}_$frame" else "wx_$key"
        val night = !isDay && sky in setOf(Sky.CLEAR, Sky.MOSTLY_CLEAR, Sky.PARTLY_CLOUDY)
        val bmp = cache.getOrPut(name) { load(ctx, name) }
        if (bmp != null) { c.drawBitmap(bmp, null, box, paint); return }
        placeholder(c, sky, night, box)
    }

    private fun load(ctx: Context, name: String): Bitmap? {
        val id = ctx.resources.getIdentifier(name, "drawable", ctx.packageName)
        if (id == 0) return null
        return runCatching { BitmapFactory.decodeResource(ctx.resources, id) }.getOrNull()
    }

    private fun placeholder(c: Canvas, sky: Sky, night: Boolean, b: RectF) {
        val cx = b.centerX(); val cy = b.centerY(); val r = b.width().coerceAtMost(b.height()) / 2f
        val sunVisible = sky in setOf(Sky.CLEAR, Sky.MOSTLY_CLEAR, Sky.PARTLY_CLOUDY)
        if (sunVisible) {
            paint.color = if (night) 0xFFFFF3C4.toInt() else 0xFFFFB300.toInt()
            val off = if (sky == Sky.PARTLY_CLOUDY) -r * 0.18f else 0f
            c.drawCircle(cx + off, cy + off, r * (if (sky == Sky.CLEAR) 0.5f else 0.38f), paint)
        }
        val cloud = sky != Sky.CLEAR
        if (cloud) {
            val shade = if (sky.isWet || sky == Sky.OVERCAST) 0xFFC9D3DD.toInt() else 0xFFF4F7FA.toInt()
            paint.color = shade
            val base = RectF(cx - r * 0.62f, cy - r * 0.05f, cx + r * 0.62f, cy + r * 0.38f)
            c.drawRoundRect(base, r * 0.2f, r * 0.2f, paint)
            c.drawCircle(cx - r * 0.15f, cy - r * 0.08f, r * 0.3f, paint)
            c.drawCircle(cx + r * 0.2f, cy - r * 0.02f, r * 0.24f, paint)
        }
        if (sky.isWet) {
            paint.color = if (sky == Sky.SNOW) 0xFFEAF4FF.toInt() else 0xFF4FA3E0.toInt()
            for (i in -1..1) c.drawCircle(cx + i * r * 0.32f, cy + r * 0.62f, r * 0.06f, paint)
        }
    }
}
