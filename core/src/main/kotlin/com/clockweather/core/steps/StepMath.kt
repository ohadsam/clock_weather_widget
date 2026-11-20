package com.clockweather.core.steps

import java.time.LocalDate

/**
 * Daily steps from the device's cumulative step counter (which counts since boot and restarts at 0 after a reboot).
 * Pure: the caller passes the sensor reading and today's date and stores the returned state.
 */
object StepMath {
    /** [baseline] is the counter value at the start of [date]; [last] the previous reading (to notice a reboot). */
    data class State(val date: LocalDate, val baseline: Long, val last: Long)

    data class Result(val state: State, val stepsToday: Int)

    fun update(prev: State?, reading: Long, today: LocalDate): Result {
        val r = reading.coerceAtLeast(0L)
        val base = when {
            prev == null || prev.date != today -> if (prev != null && r < prev.last) 0L else r // first reading of a new day starts at 0
            r < prev.last -> 0L // reboot: the counter restarted, everything since boot is today's
            else -> prev.baseline
        }
        return Result(State(today, base, r), (r - base).coerceIn(0L, 200_000L).toInt())
    }

    fun encode(s: State): String = "${s.date}|${s.baseline}|${s.last}"

    fun decode(text: String?): State? {
        val p = text?.split('|') ?: return null
        if (p.size != 3) return null
        return runCatching { State(LocalDate.parse(p[0]), p[1].toLong(), p[2].toLong()) }.getOrNull()
    }
}
