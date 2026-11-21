package com.clockweather.app.data

import android.content.Context

/** When the minute tick last redrew the widget. Written by the tick, read by the Settings "Widget status" card and the heartbeat. */
object TickState {
    private const val PREFS = "tick"
    fun mark(ctx: Context, nowMs: Long = System.currentTimeMillis()) { ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putLong("last", nowMs).apply() }
    fun last(ctx: Context): Long = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getLong("last", 0L)
}
