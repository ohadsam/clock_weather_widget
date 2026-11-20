package com.clockweather.app.widget

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.clockweather.core.clock.TickMath

/** Wakes the widget on the next minute boundary so the flip happens on time. One alarm at a time, re-armed by each tick. */
object TickScheduler {
    const val ACTION_TICK = "com.clockweather.app.TICK"

    private fun pending(ctx: Context): PendingIntent {
        val i = Intent(ctx, ClockWidgetProvider::class.java).setAction(ACTION_TICK)
        return PendingIntent.getBroadcast(ctx, 0, i, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
    }

    fun scheduleNext(ctx: Context, nowMs: Long = System.currentTimeMillis()) {
        val am = ctx.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val at = TickMath.nextMinuteMs(nowMs)
        val pi = pending(ctx)
        val exactOk = Build.VERSION.SDK_INT < Build.VERSION_CODES.S || am.canScheduleExactAlarms()
        if (exactOk) am.setExactAndAllowWhileIdle(AlarmManager.RTC, at, pi)
        else am.setAndAllowWhileIdle(AlarmManager.RTC, at, pi)
    }

    fun cancel(ctx: Context) {
        (ctx.getSystemService(Context.ALARM_SERVICE) as? AlarmManager)?.cancel(pending(ctx))
    }
}
