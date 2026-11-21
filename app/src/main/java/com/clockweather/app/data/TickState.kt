package com.clockweather.app.data

import android.content.Context

/** When the minute tick last redrew the widget. Written by the tick, read by the Settings "Widget status" card and the heartbeat. */
object TickState {
    private const val PREFS = "tick"
    fun mark(ctx: Context, nowMs: Long = System.currentTimeMillis()) {
        val p = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        // how many seconds after the minute boundary this tick ran (only ticks within 30 s count; a manual repair is not a tick)
        val late = (nowMs % 60_000L).takeIf { it < 30_000L }?.div(1000L)
        val log = (p.getString("late", "") ?: "").split(',').filter { it.isNotEmpty() }.toMutableList()
        if (late != null) { log += late.toString(); while (log.size > 30) log.removeAt(0) }
        p.edit().putLong("last", nowMs).putString("late", log.joinToString(",")).apply()
    }

    /** Seconds each of the last ticks ran after its minute boundary (oldest first). */
    fun lateness(ctx: Context): List<Long> = (ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString("late", "") ?: "").split(',').mapNotNull { it.toLongOrNull() }
    fun last(ctx: Context): Long = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getLong("last", 0L)
}
