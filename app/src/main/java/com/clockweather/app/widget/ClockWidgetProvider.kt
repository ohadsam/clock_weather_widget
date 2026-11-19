package com.clockweather.app.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.app.PendingIntent
import android.os.Bundle
import android.os.PowerManager
import android.widget.RemoteViews
import com.clockweather.app.MainActivity
import com.clockweather.app.R
import com.clockweather.app.data.WeatherWorker
import com.clockweather.core.clock.FlipMath
import com.clockweather.core.clock.WorldClock
import java.time.Instant
import kotlin.concurrent.thread

/**
 * The home-screen widget. Thin on purpose: it asks Snapshot for data, WidgetRenderer for pixels and pushes the bitmap.
 * Every minute a TICK alarm triggers a short burst of frames (the flip) when the minute changed and the screen is on.
 */
class ClockWidgetProvider : AppWidgetProvider() {

    override fun onEnabled(context: Context) {
        WeatherWorker.schedule(context)
        TickScheduler.scheduleNext(context)
    }

    override fun onDisabled(context: Context) {
        TickScheduler.cancel(context)
    }

    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        pushAll(context, manager, ids, Snapshot.build(context))
        TickScheduler.scheduleNext(context)
    }

    override fun onAppWidgetOptionsChanged(context: Context, manager: AppWidgetManager, id: Int, newOptions: Bundle) {
        pushAll(context, manager, intArrayOf(id), Snapshot.build(context))
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == TickScheduler.ACTION_TICK) {
            val pending = goAsync()
            thread(name = "clock-tick") {
                try { tick(context) } finally { pending.finish() }
            }
            return
        }
        super.onReceive(context, intent)
        if (intent.action in RESCHEDULE_ACTIONS) {
            TickScheduler.scheduleNext(context)
            updateAll(context)
        }
    }

    private fun tick(context: Context) {
        val manager = AppWidgetManager.getInstance(context)
        val ids = manager.getAppWidgetIds(ComponentName(context, ClockWidgetProvider::class.java))
        if (ids.isEmpty()) return
        val now = Instant.now()
        val st = Snapshot.build(context, now)
        val screenOn = (context.getSystemService(Context.POWER_SERVICE) as? PowerManager)?.isInteractive != false
        val zone = st.zone
        val before = now.minusSeconds(60).atZone(zone)
        val cur = now.atZone(zone)
        if (screenOn && !st.settings.calmAnimations && st.settings.clockStyle == com.clockweather.core.widget.ClockStyle.FLIP) {
            val from = WorldClock.digits(before.hour, before.minute, st.settings.use24h)
            for (p in FlipMath.burst()) {
                pushAll(context, manager, ids, Snapshot.build(context, now, from, p))
                if (p < 1f) Thread.sleep(FlipMath.DURATION_MS / FlipMath.FRAMES.toLong())
            }
        } else {
            pushAll(context, manager, ids, st)
        }
        TickScheduler.scheduleNext(context, cur.toInstant().toEpochMilli())
    }

    companion object {
        private val RESCHEDULE_ACTIONS = setOf(
            Intent.ACTION_TIME_CHANGED, Intent.ACTION_TIMEZONE_CHANGED, Intent.ACTION_LOCALE_CHANGED,
            Intent.ACTION_BOOT_COMPLETED, Intent.ACTION_MY_PACKAGE_REPLACED,
        )

        /** Redraw every placed widget now (called after Settings change or fresh weather data). */
        fun updateAll(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(ComponentName(context, ClockWidgetProvider::class.java))
            if (ids.isNotEmpty()) pushAll(context, manager, ids, Snapshot.build(context))
        }

        private fun pushAll(context: Context, manager: AppWidgetManager, ids: IntArray, st: RenderState) {
            val density = context.resources.displayMetrics.density
            for (id in ids) {
                val (wDp, hDp) = sizeDp(manager.getAppWidgetOptions(id), context)
                val bmp = WidgetRenderer.render(context, (wDp * density).toInt(), (hDp * density).toInt(), density, st)
                val rv = RemoteViews(context.packageName, R.layout.widget_clock)
                rv.setImageViewBitmap(R.id.widget_image, bmp)
                rv.setOnClickPendingIntent(R.id.tap_clock, open(context, "settings", id * 2))
                rv.setOnClickPendingIntent(R.id.tap_weather, open(context, "weather", id * 2 + 1))
                manager.updateAppWidget(id, rv)
            }
        }

        private fun sizeDp(o: Bundle, context: Context): Pair<Int, Int> {
            val portrait = context.resources.configuration.orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT
            val w = if (portrait) o.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 250) else o.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH, 250)
            val h = if (portrait) o.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT, 150) else o.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 150)
            return w.coerceIn(60, 800) to h.coerceIn(60, 800)
        }

        private fun open(context: Context, screen: String, code: Int): PendingIntent {
            val i = Intent(context, MainActivity::class.java).putExtra(MainActivity.EXTRA_SCREEN, screen).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            return PendingIntent.getActivity(context, code, i, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }
}
