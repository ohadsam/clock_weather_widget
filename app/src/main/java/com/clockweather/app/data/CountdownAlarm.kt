package com.clockweather.app.data

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.clockweather.app.widget.ClockWidgetProvider
import com.clockweather.core.timers.Countdown
import com.clockweather.core.timers.TimerItem

/** One exact alarm per running countdown, at the moment it ends; the receiver marks it finished, rings / vibrates as configured and redraws the widget. */
object CountdownAlarm {
    private fun pending(ctx: Context, id: String): PendingIntent =
        PendingIntent.getBroadcast(ctx, 6000 + (id.hashCode() and 0xFFFF), Intent(ctx, CountdownReceiver::class.java).putExtra("id", id), PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

    fun schedule(ctx: Context, item: TimerItem) {
        val c = item.countdownState
        if (!item.countdown || !c.running) return
        val am = ctx.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val exactOk = Build.VERSION.SDK_INT < Build.VERSION_CODES.S || am.canScheduleExactAlarms()
        if (exactOk) am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.endsAtMs, pending(ctx, item.id)) else am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.endsAtMs, pending(ctx, item.id))
    }

    fun cancel(ctx: Context, id: String) { (ctx.getSystemService(Context.ALARM_SERVICE) as? AlarmManager)?.cancel(pending(ctx, id)) }

    /** After a reboot or an app update: arm again every running countdown (or finish the ones that ended meanwhile). */
    fun restore(ctx: Context) {
        val now = System.currentTimeMillis()
        for (item in TimerStore.items(ctx)) {
            val c = item.countdownState
            if (!item.countdown || !c.running) continue
            if (c.isDue(now)) CountdownReceiver.finish(ctx, item.id) else schedule(ctx, item)
        }
    }
}

class CountdownReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) { intent.getStringExtra("id")?.let { finish(context, it) } }

    companion object {
        fun finish(ctx: Context, id: String) {
            val item = TimerStore.items(ctx).firstOrNull { it.id == id } ?: return
            val c = item.countdownState
            if (!c.running || !c.isDue(System.currentTimeMillis() + 1500)) return
            val plan = item.interval
            val next = plan?.next()
            if (next != null) { // interval timer: start the next work/rest segment right where this one ended (no drift), then a short signal
                val advanced = TimerStore.update(ctx, id) { it.copy(iv = next.encode()).with(Countdown().withDuration(next.durationMs).start(c.endsAtMs)) } ?: return
                CountdownAlarm.schedule(ctx, advanced)
                Notifier.postPhase(ctx, advanced, next)
                ClockWidgetProvider.updateAll(ctx)
                return
            }
            val done = TimerStore.update(ctx, id) { it.with(c.finish()) } ?: return
            Notifier.postTimerDone(ctx, done)
            ClockWidgetProvider.updateAll(ctx)
        }
    }
}
