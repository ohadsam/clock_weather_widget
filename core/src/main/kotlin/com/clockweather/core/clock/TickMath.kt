package com.clockweather.core.clock

/** When the widget must wake next. Pure so the schedule is proven by tests, not by waiting for a phone. */
object TickMath {
    const val MINUTE_MS = 60_000L

    /** First minute boundary strictly after [nowMs] (epoch ms; minute boundaries are the same in every time zone and across DST). */
    fun nextMinuteMs(nowMs: Long): Long = (Math.floorDiv(nowMs, MINUTE_MS) + 1) * MINUTE_MS

    /** Milliseconds until the next boundary, 1..60000. */
    fun msUntilNextMinute(nowMs: Long): Long = nextMinuteMs(nowMs) - nowMs
}

/** How old the cached weather is, and the short phrase the widget shows when it is no longer fresh. */
object Freshness {
    enum class Level { FRESH, STALE, OLD }

    fun ageMinutes(fetchedAtEpochSec: Long, nowEpochSec: Long): Long = ((nowEpochSec - fetchedAtEpochSec) / 60L).coerceAtLeast(0L)

    /** Fresh under 90 minutes (the refresh runs every 30), stale under 6 hours, old beyond. */
    fun level(ageMin: Long): Level = when {
        ageMin < 90 -> Level.FRESH
        ageMin < 360 -> Level.STALE
        else -> Level.OLD
    }

    /** `לפני 45 ד׳`, `לפני 3 שע׳`, `לפני 2 ימים` / `45 min ago`, `3 h ago`, `2 days ago`. */
    fun label(ageMin: Long, hebrew: Boolean): String {
        val (n, unitHe, unitEn) = when {
            ageMin < 60 -> Triple(ageMin, "ד׳", "min")
            ageMin < 60 * 48 -> Triple(ageMin / 60, "שע׳", "h")
            else -> Triple(ageMin / (60 * 24), "ימים", "days")
        }
        return if (hebrew) "לפני $n $unitHe" else "$n $unitEn ago"
    }
}
