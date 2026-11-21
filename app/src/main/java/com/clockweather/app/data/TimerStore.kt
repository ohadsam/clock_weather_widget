package com.clockweather.app.data

import android.content.Context
import com.clockweather.core.timers.Countdown
import com.clockweather.core.timers.DateCountdown
import com.clockweather.core.timers.DateCountdowns
import com.clockweather.core.timers.Stopwatch
import com.clockweather.core.timers.TimerItem
import com.clockweather.core.timers.TimerList

/** Persists the list of stopwatches/countdowns and the date countdowns (timestamps only, so they keep running with the app closed). */
object TimerStore {
    private const val PREFS = "timers"
    private fun p(ctx: Context) = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    @Synchronized
    fun items(ctx: Context): List<TimerItem> {
        val raw = p(ctx).getString("items", null)
        if (raw != null) return TimerList.decode(raw)
        // one-time migration from the first version (one stopwatch + one countdown)
        val sw = Stopwatch.decode(p(ctx).getString("stopwatch", null))
        val cd = Countdown.decode(p(ctx).getString("countdown", null))
        val migrated = buildList {
            if (sw.active) add(TimerItem("sw1", false).with(sw))
            if (cd.active) add(TimerItem("cd1", true).with(cd))
        }
        p(ctx).edit().putString("items", TimerList.encode(migrated)).remove("stopwatch").remove("countdown").apply()
        return migrated
    }

    @Synchronized
    fun save(ctx: Context, list: List<TimerItem>) { p(ctx).edit().putString("items", TimerList.encode(list)).apply() }

    /** Read-modify-write of one timer; returns the stored result (null when the timer no longer exists). */
    @Synchronized
    fun update(ctx: Context, id: String, change: (TimerItem) -> TimerItem): TimerItem? {
        val list = items(ctx)
        val old = list.firstOrNull { it.id == id } ?: return null
        val new = change(old)
        save(ctx, TimerList.replace(list, new))
        return new
    }

    fun dates(ctx: Context): List<DateCountdown> = DateCountdowns.decode(p(ctx).getString("dates", null))
    fun saveDates(ctx: Context, list: List<DateCountdown>) { p(ctx).edit().putString("dates", DateCountdowns.encode(list)).apply() }
}
