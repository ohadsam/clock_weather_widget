package com.clockweather.app.data

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.clockweather.app.R
import com.clockweather.app.widget.ClockWidgetProvider

/** One exact alarm at the moment the countdown ends; the receiver marks it finished, notifies and redraws the widget. */
object CountdownAlarm {
    private fun pending(ctx: Context): PendingIntent =
        PendingIntent.getBroadcast(ctx, 4001, Intent(ctx, CountdownReceiver::class.java), PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

    fun schedule(ctx: Context, atMs: Long) {
        val am = ctx.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val exactOk = Build.VERSION.SDK_INT < Build.VERSION_CODES.S || am.canScheduleExactAlarms()
        if (exactOk) am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, atMs, pending(ctx)) else am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, atMs, pending(ctx))
    }

    fun cancel(ctx: Context) { (ctx.getSystemService(Context.ALARM_SERVICE) as? AlarmManager)?.cancel(pending(ctx)) }

    /** After a reboot or an app update: arm again for a countdown that is still running (or finish it if it ended meanwhile). */
    fun restore(ctx: Context) {
        val c = TimerStore.countdown(ctx)
        if (!c.running) return
        if (c.isDue(System.currentTimeMillis())) CountdownReceiver.finish(ctx) else schedule(ctx, c.endsAtMs)
    }
}

class CountdownReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) { finish(context) }

    companion object {
        fun finish(ctx: Context) {
            val c = TimerStore.countdown(ctx)
            if (!c.running || !c.isDue(System.currentTimeMillis() + 1500)) return
            TimerStore.save(ctx, c.finish())
            Notifier.post(ctx, Notifier.CH_TIMER, 4001, ctx.getString(R.string.countdown_done_title), ctx.getString(R.string.countdown_done_text))
            ClockWidgetProvider.updateAll(ctx)
        }
    }
}
