package com.clockweather.app.data

import android.content.Context
import com.clockweather.core.timers.Countdown
import com.clockweather.core.timers.Stopwatch

/** Persists the stopwatch and the countdown (timestamps only, so they keep running with the app closed). */
object TimerStore {
    private const val PREFS = "timers"
    private fun p(ctx: Context) = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun stopwatch(ctx: Context): Stopwatch = Stopwatch.decode(p(ctx).getString("stopwatch", null))
    fun countdown(ctx: Context): Countdown = Countdown.decode(p(ctx).getString("countdown", null))
    fun save(ctx: Context, s: Stopwatch) { p(ctx).edit().putString("stopwatch", s.encode()).apply() }
    fun save(ctx: Context, c: Countdown) { p(ctx).edit().putString("countdown", c.encode()).apply() }
}
