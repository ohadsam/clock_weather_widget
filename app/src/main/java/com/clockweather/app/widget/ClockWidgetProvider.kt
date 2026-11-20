package com.clockweather.app.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PowerManager
import android.widget.RemoteViews
import com.clockweather.app.MainActivity
import com.clockweather.app.R
import com.clockweather.app.data.SettingsStore
import com.clockweather.app.data.WeatherRepository
import com.clockweather.app.data.WeatherWorker
import com.clockweather.core.clock.FlipMath
import com.clockweather.core.clock.WorldClock
import com.clockweather.core.weather.WeatherAnim
import com.clockweather.core.widget.ClockStyle
import com.clockweather.core.widget.WidgetKind
import java.time.Instant
import kotlin.concurrent.thread

/**
 * The home-screen widgets. One class per WidgetKind (the launcher lists each in its picker); all share this code.
 * Thin on purpose: ask Snapshot for data, WidgetRenderer for pixels, push the bitmap. Every minute a TICK alarm
 * triggers a short burst of frames (the flip) for clock kinds when the screen is on.
 */
open class ClockWidgetProvider : AppWidgetProvider() {
    protected open val kind: WidgetKind = WidgetKind.CLASSIC

    override fun onEnabled(context: Context) {
        WeatherWorker.schedule(context)
        com.clockweather.app.data.CalendarChangeWorker.schedule(context)
        TickScheduler.scheduleNext(context)
    }

    override fun onDisabled(context: Context) {
        if (allIds(context).isEmpty()) TickScheduler.cancel(context)
    }

    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        pushAll(context, manager, ids, kind)
        TickScheduler.scheduleNext(context)
    }

    override fun onAppWidgetOptionsChanged(context: Context, manager: AppWidgetManager, id: Int, newOptions: Bundle) {
        pushAll(context, manager, intArrayOf(id), kind)
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == TickScheduler.ACTION_TICK) {
            val pending = goAsync()
            thread(name = "clock-tick") { try { tick(context) } finally { pending.finish() } }
            return
        }
        super.onReceive(context, intent)
        if (intent.action in RESCHEDULE_ACTIONS) {
            TickScheduler.scheduleNext(context)
            updateAll(context)
        }
    }

    private fun tick(context: Context) {
        // Re-arm FIRST: if anything below throws or the process dies, the chain of minute ticks must survive.
        TickScheduler.scheduleNext(context)
        val manager = AppWidgetManager.getInstance(context)
        if (allIds(context).isEmpty()) return
        val now = Instant.now()
        val settings = SettingsStore.load(context)
        val zone = java.time.ZoneId.systemDefault()
        val screenOn = (context.getSystemService(Context.POWER_SERVICE) as? PowerManager)?.isInteractive != false
        val before = now.minusSeconds(60).atZone(zone)
        val from = WorldClock.digits(before.hour, before.minute, settings.use24h)
        val flipKinds = if (screenOn && !settings.calmAnimations && settings.clockStyle == ClockStyle.FLIP) setOf(WidgetKind.CLASSIC, WidgetKind.CLOCK) else emptySet()
        for ((k, ids) in idsByKind(context, manager)) if (k !in flipKinds) pushAll(context, manager, ids, k, now)
        if (flipKinds.isNotEmpty()) {
            for (p in FlipMath.burst()) {
                for ((k, ids) in idsByKind(context, manager)) if (k in flipKinds) pushAll(context, manager, ids, k, now, from, p)
                if (p < 1f) Thread.sleep(FlipMath.DURATION_MS / FlipMath.FRAMES.toLong())
            }
        }
        playLidOpening(context, manager, settings, screenOn, now)
        playWeatherLoop(context, manager, settings, screenOn, now)
    }

    /** On the hour the pocket watch lid opens (1 s). Only for clock kinds with the pocket style, screen on, animations not calmed. */
    private fun playLidOpening(context: Context, manager: AppWidgetManager, settings: com.clockweather.core.widget.WidgetSettings, screenOn: Boolean, now: Instant) {
        if (!screenOn || settings.calmAnimations || settings.clockStyle != ClockStyle.POCKET) return
        if (now.atZone(java.time.ZoneId.systemDefault()).minute != 0) return
        val targets = idsByKind(context, manager).filter { it.first == WidgetKind.CLASSIC || it.first == WidgetKind.CLOCK }
        if (targets.isEmpty()) return
        val steps = 12
        for (i in 0..steps) {
            val t = i / steps.toFloat(); val eased = (1f - Math.cos(Math.PI * t).toFloat()) / 2f
            for ((k, ids) in targets) pushAll(context, manager, ids, k, now, lidOpen = eased)
            Thread.sleep(80L)
        }
    }

    /** One 1.5 s loop of the animated weather illustration, only for kinds that show weather and skies that have a loop. */
    private fun playWeatherLoop(context: Context, manager: AppWidgetManager, settings: com.clockweather.core.widget.WidgetSettings, screenOn: Boolean, now: Instant) {
        if (!screenOn || settings.calmAnimations || !settings.animateWeather || !settings.showWeather) return
        val f = settings.activePlaceId?.let { WeatherRepository.cached(context, it) } ?: return
        val sky = f.current?.sky ?: f.today(now)?.sky ?: return
        if (!WeatherAnim.has(WeatherAnim.key(sky, f.current?.isDay ?: true))) return
        val weatherKinds = setOf(WidgetKind.CLASSIC, WidgetKind.WEATHER, WidgetKind.STRIP)
        val targets = idsByKind(context, manager).filter { it.first in weatherKinds }
        if (targets.isEmpty()) return
        for (i in 0 until WeatherAnim.FRAMES) {
            for ((k, ids) in targets) pushAll(context, manager, ids, k, now, animMs = i * 1000L / WeatherAnim.FPS)
            Thread.sleep(1000L / WeatherAnim.FPS)
        }
        for ((k, ids) in targets) pushAll(context, manager, ids, k, now) // back to the still
    }

    companion object {
        private val RESCHEDULE_ACTIONS = setOf(
            Intent.ACTION_TIME_CHANGED, Intent.ACTION_TIMEZONE_CHANGED, Intent.ACTION_LOCALE_CHANGED,
            Intent.ACTION_BOOT_COMPLETED, Intent.ACTION_MY_PACKAGE_REPLACED,
            android.app.AlarmManager.ACTION_NEXT_ALARM_CLOCK_CHANGED,
            "android.app.action.SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED",
        )

        /** Every provider class and the kind it shows (keep in sync with AndroidManifest.xml). */
        private val PROVIDERS: List<Pair<Class<out ClockWidgetProvider>, WidgetKind>> = listOf(
            ClockWidgetProvider::class.java to WidgetKind.CLASSIC,
            ClockOnlyWidgetProvider::class.java to WidgetKind.CLOCK,
            WeatherWidgetProvider::class.java to WidgetKind.WEATHER,
            AgendaWidgetProvider::class.java to WidgetKind.AGENDA,
            StripWidgetProvider::class.java to WidgetKind.STRIP,
        )

        private fun idsByKind(context: Context, manager: AppWidgetManager): List<Pair<WidgetKind, IntArray>> =
            PROVIDERS.map { (cls, k) -> k to manager.getAppWidgetIds(ComponentName(context, cls)) }.filter { it.second.isNotEmpty() }

        fun hasWidgets(context: Context): Boolean = allIds(context).isNotEmpty()

        private fun allIds(context: Context): List<Int> =
            idsByKind(context, AppWidgetManager.getInstance(context)).flatMap { it.second.toList() }

        /** Redraw every placed widget of every kind now (after Settings change or fresh weather data). */
        fun updateAll(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            for ((k, ids) in idsByKind(context, manager)) pushAll(context, manager, ids, k)
        }

        private fun pushAll(context: Context, manager: AppWidgetManager, ids: IntArray, kind: WidgetKind, now: Instant = Instant.now(), flipFrom: IntArray? = null, progress: Float = 1f, animMs: Long = -1L, lidOpen: Float = 1f) {
            val density = context.resources.displayMetrics.density
            val st = Snapshot.build(context, now, flipFrom, progress, kind, animMs, lidOpen)
            for (id in ids) {
                val (wDp, hDp) = sizeDp(manager.getAppWidgetOptions(id), context)
                val bmp = WidgetRenderer.render(context, (wDp * density).toInt(), (hDp * density).toInt(), density, st)
                val rv = RemoteViews(context.packageName, R.layout.widget_clock)
                rv.setImageViewBitmap(R.id.widget_image, bmp)
                rv.setOnClickPendingIntent(R.id.tap_clock, open(context, if (kind == WidgetKind.WEATHER) "weather" else "settings", id * 2))
                rv.setOnClickPendingIntent(R.id.tap_weather, open(context, "weather", id * 2 + 1))
                manager.updateAppWidget(id, rv)
            }
        }

        private fun sizeDp(o: Bundle, context: Context): Pair<Int, Int> {
            val portrait = context.resources.configuration.orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT
            val w = if (portrait) o.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 250) else o.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH, 250)
            val h = if (portrait) o.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT, 150) else o.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 150)
            return w.coerceIn(60, 800) to h.coerceIn(40, 800)
        }

        private fun open(context: Context, screen: String, code: Int): PendingIntent {
            val i = Intent(context, MainActivity::class.java).putExtra(MainActivity.EXTRA_SCREEN, screen).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            return PendingIntent.getActivity(context, code, i, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }
}

/** Clock-only widget (any clock style). */
class ClockOnlyWidgetProvider : ClockWidgetProvider() { override val kind = WidgetKind.CLOCK }

/** Weather-first widget. */
class WeatherWidgetProvider : ClockWidgetProvider() { override val kind = WidgetKind.WEATHER }

/** Today's events and next alarm. */
class AgendaWidgetProvider : ClockWidgetProvider() { override val kind = WidgetKind.AGENDA }

/** One slim row: time, date, temperature. */
class StripWidgetProvider : ClockWidgetProvider() { override val kind = WidgetKind.STRIP }
