package com.clockweather.app.widget

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.clockweather.app.MainActivity
import com.clockweather.app.data.SourceApps
import com.clockweather.core.widget.TapTarget
import com.clockweather.core.widget.WidgetSettings

/** What each part of the widget opens: the matching phone app where there is one (calendar, alarm, weather), else the right screen of this app. */
object WidgetTaps {
    fun pending(ctx: Context, target: TapTarget, s: WidgetSettings, widgetId: Int): PendingIntent? {
        val code = widgetId * 16 + target.ordinal
        return when (target) {
            TapTarget.CLOCK -> ClockWidgetProvider.open(ctx, "home", code)
            TapTarget.DATE, TapTarget.AGENDA -> activity(ctx, SourceApps.calendarIntent(), code) ?: ClockWidgetProvider.open(ctx, "detail:date", code)
            TapTarget.WEATHER -> s.weatherAppPackage.takeIf { it.isNotEmpty() }
                ?.let { pkg -> ctx.packageManager.getLaunchIntentForPackage(pkg)?.let { activity(ctx, it, code) } }
                ?: ClockWidgetProvider.open(ctx, "weather", code)
            TapTarget.ALARM -> SourceApps.alarmPending(ctx) ?: ClockWidgetProvider.open(ctx, "detail:alarm", code)
            TapTarget.WORLD, TapTarget.TIMERS -> ClockWidgetProvider.open(ctx, "home", code)
            TapTarget.SUN_STEPS -> ClockWidgetProvider.open(ctx, "detail:sun", code)
        }
    }

    private fun activity(ctx: Context, intent: Intent, code: Int): PendingIntent? =
        runCatching { PendingIntent.getActivity(ctx, code, intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK), PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT) }.getOrNull()
}
