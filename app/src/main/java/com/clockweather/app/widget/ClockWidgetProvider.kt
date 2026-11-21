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
import com.clockweather.app.data.HeartbeatWorker
import com.clockweather.app.data.SettingsStore
import com.clockweather.app.data.TickState
import com.clockweather.app.data.StepWorker
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
        if (SettingsStore.load(context).showSteps) StepWorker.schedule(context)
        com.clockweather.app.data.CalendarChangeWorker.schedule(context)
        HeartbeatWorker.schedule(context)
        TickScheduler.scheduleNext(context)
    }

    override fun onDisabled(context: Context) {
        if (allIds(context).isEmpty()) TickScheduler.cancel(context)
    }

    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        pushAll(context, manager, ids, kind)
        TickScheduler.scheduleNext(context)
        HeartbeatWorker.schedule(context)
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
        // A new time zone (or a clock change) must be picked up by this long-lived process, or the widget would keep the old zone.
        if (intent.action == Intent.ACTION_TIMEZONE_CHANGED || intent.action == Intent.ACTION_TIME_CHANGED) java.util.TimeZone.setDefault(null)
        super.onReceive(context, intent)
        if (intent.action in RESCHEDULE_ACTIONS) {
            TickScheduler.scheduleNext(context)
            com.clockweather.app.data.CountdownAlarm.restore(context)
            updateAll(context)
        }
    }

    private fun tick(context: Context) {
        // Re-arm FIRST: if anything below throws or the process dies, the chain of minute ticks must survive.
        TickScheduler.scheduleNext(context)
        TickState.mark(context)
        val manager = AppWidgetManager.getInstance(context)
        if (allIds(context).isEmpty()) return
        val now = Instant.now()
        val settings = SettingsStore.load(context)
        val zone = java.time.ZoneId.systemDefault()
        val screenOn = (context.getSystemService(Context.POWER_SERVICE) as? PowerManager)?.isInteractive != false
        // quiet hours (night mode) and "calm animations" both mean: only the plain minute update, no flip / lid / weather loop
        val calm = settings.calmAnimations || com.clockweather.core.widget.QuietHours.isQuiet(settings.quietHoursEnabled, settings.quietFromHour, settings.quietToHour, now.atZone(zone).toLocalTime())
        val before = now.minusSeconds(60).atZone(zone)
        val from = WorldClock.digits(before.hour, before.minute, settings.use24h)
        val flipKinds = if (screenOn && !calm) kindsWithStyle(context, manager, settings, ClockStyle.FLIP) else emptySet()
        for ((k, ids) in idsByKind(context, manager)) if (k !in flipKinds) pushAll(context, manager, ids, k, now)
        if (flipKinds.isNotEmpty()) {
            for (p in FlipMath.burst()) {
                for ((k, ids) in idsByKind(context, manager)) if (k in flipKinds) pushAll(context, manager, ids, k, now, from, p)
                if (p < 1f) Thread.sleep(FlipMath.DURATION_MS / FlipMath.FRAMES.toLong())
            }
        }
        playLidOpening(context, manager, settings, screenOn && !calm, now)
        playWeatherLoop(context, manager, settings, screenOn && !calm, now)
    }

    /** Clock kinds that have at least one placed widget whose effective clock style (global + its own override) is [style]. */
    private fun kindsWithStyle(context: Context, manager: AppWidgetManager, settings: com.clockweather.core.widget.WidgetSettings, style: ClockStyle): Set<WidgetKind> =
        idsByKind(context, manager).filter { (k, ids) -> (k == WidgetKind.CLASSIC || k == WidgetKind.CLOCK) && ids.any { settings.forWidget(it).clockStyle == style } }.map { it.first }.toSet()

    /** On the hour the pocket watch lid opens (1 s). Only for clock kinds with the pocket style, screen on, animations not calmed. */
    private fun playLidOpening(context: Context, manager: AppWidgetManager, settings: com.clockweather.core.widget.WidgetSettings, screenOn: Boolean, now: Instant) {
        if (!screenOn || settings.calmAnimations) return
        if (now.atZone(java.time.ZoneId.systemDefault()).minute != 0) return
        val pocketKinds = kindsWithStyle(context, manager, settings, ClockStyle.POCKET)
        val targets = idsByKind(context, manager).filter { it.first in pocketKinds }
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

        /** Every placed widget as (appWidgetId, kind), for the "Widgets on my home screen" settings. */
        fun placed(context: Context): List<Pair<Int, WidgetKind>> =
            idsByKind(context, AppWidgetManager.getInstance(context)).flatMap { (k, ids) -> ids.map { it to k } }.sortedBy { it.first }

        private fun allIds(context: Context): List<Int> =
            idsByKind(context, AppWidgetManager.getInstance(context)).flatMap { it.second.toList() }

        /** Re-arm the minute tick and redraw. With [onlyIfStale] the redraw happens only when the last tick is over 2 minutes old. */
        fun repair(context: Context, onlyIfStale: Boolean = false) {
            TickScheduler.scheduleNext(context)
            if (onlyIfStale && System.currentTimeMillis() - TickState.last(context) < 2 * 60_000L) return
            TickState.mark(context)
            updateAll(context)
        }

        /** Redraw every placed widget of every kind now (after Settings change or fresh weather data). */
        fun updateAll(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            for ((k, ids) in idsByKind(context, manager)) pushAll(context, manager, ids, k)
        }

        private fun pushAll(context: Context, manager: AppWidgetManager, ids: IntArray, kind: WidgetKind, now: Instant = Instant.now(), flipFrom: IntArray? = null, progress: Float = 1f, animMs: Long = -1L, lidOpen: Float = 1f) {
            val density = context.resources.displayMetrics.density
            val global = SettingsStore.load(context)
            val shared by lazy { Snapshot.build(context, now, flipFrom, progress, kind, animMs, lidOpen) }
            for (id in ids) {
                // a widget the owner customised gets its own snapshot; all others share one
                val st = if (global.perWidget.containsKey(id.toString())) Snapshot.build(context, now, flipFrom, progress, kind, animMs, lidOpen, widgetId = id) else shared
                val (wDp, hDp) = sizeDp(manager.getAppWidgetOptions(id), context)
                val bmp = WidgetRenderer.render(context, (wDp * density).toInt(), (hDp * density).toInt(), density, st)
                val rv = RemoteViews(context.packageName, R.layout.widget_clock)
                rv.setImageViewBitmap(R.id.widget_image, bmp)
                val plan = com.clockweather.core.widget.WidgetLayout.plan(wDp, hDp, st.settings, kind)
                val intents = HashMap<com.clockweather.core.widget.TapTarget, PendingIntent?>()
                com.clockweather.core.widget.TapZones.rows(plan).forEachIndexed { i, target ->
                    rv.setOnClickPendingIntent(ROW_IDS[i], intents.getOrPut(target) { WidgetTaps.pending(context, target, st.settings, id) })
                }
                rv.setViewVisibility(R.id.tap_settings, if (st.settings.showSettingsButton) android.view.View.VISIBLE else android.view.View.GONE)
                rv.setOnClickPendingIntent(R.id.tap_settings, open(context, "settings", id * 16 + 15))
                manager.updateAppWidget(id, rv)
            }
        }

        private val ROW_IDS = intArrayOf(
            R.id.tap_r0, R.id.tap_r1, R.id.tap_r2, R.id.tap_r3, R.id.tap_r4, R.id.tap_r5, R.id.tap_r6, R.id.tap_r7, R.id.tap_r8, R.id.tap_r9,
            R.id.tap_r10, R.id.tap_r11, R.id.tap_r12, R.id.tap_r13, R.id.tap_r14, R.id.tap_r15, R.id.tap_r16, R.id.tap_r17, R.id.tap_r18, R.id.tap_r19,
        )

        private fun sizeDp(o: Bundle, context: Context): Pair<Int, Int> {
            val portrait = context.resources.configuration.orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT
            val w = if (portrait) o.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 250) else o.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH, 250)
            val h = if (portrait) o.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT, 150) else o.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 150)
            return w.coerceIn(60, 800) to h.coerceIn(40, 800)
        }

        internal fun open(context: Context, screen: String, code: Int): PendingIntent {
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
