package com.clockweather.core.wear

import com.clockweather.core.text.Untrusted
import com.clockweather.core.timers.TimerItem
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/** One countdown as the watch shows it: running (ends at [end]) or paused with [rem] left. */
@Serializable
data class WearTimer(val l: String, val run: Boolean, val end: Long = 0L, val rem: Long = 0L)

/**
 * What the phone sends the watch over the Data Layer (already formatted, so the watch needs no logic of its own):
 * the active place's weather line and the running / paused countdowns. Pure: encode/decode clamp every field (it is stored data on the watch).
 */
@Serializable
data class WearState(
    val at: Long, val place: String, val temp: String, val hiLo: String, val rain: Int?, val sky: String, val hebrew: Boolean,
    val timers: List<WearTimer> = emptyList(), val v: Int = 1, val events: List<String> = emptyList(),
) {
    fun encode(): String = JSON.encodeToString(serializer(), this)

    /** Weather lines for the tile: place, temperature + sky, high/low (+ rain chance). Empty when the phone had no forecast yet. */
    fun weatherLines(): List<String> {
        if (temp.isEmpty()) return emptyList()
        val rainPart = rain?.let { "  ☂ $it%" }.orEmpty()
        return listOf(place, "$temp  $sky", "$hiLo$rainPart")
    }

    /** Up to 2 upcoming special events as ready-made lines ("🎂 Dana, today"). */
    fun eventLines(): List<String> = events.take(MAX_EVENTS)

    /** `Eggs 8 min` style lines for the countdowns at [nowMs]; running ones count down (minutes rounded up), paused ones say so. */
    fun timerLines(nowMs: Long): List<String> = timers.take(MAX_TIMERS).map { t ->
        val left = if (t.run) (t.end - nowMs).coerceAtLeast(0L) else t.rem
        val text = if (t.run && left <= 0L) (if (hebrew) "הסתיים" else "Done") else minutes((left + 59_999L) / 60_000L, hebrew) + if (!t.run) (if (hebrew) " (מושהה)" else " (paused)") else ""
        (if (t.l.isEmpty()) "⏳" else "⏳ ${t.l}") + " " + text
    }

    /** True when the phone has not sent anything for a while (the watch says so instead of showing old numbers as fresh). */
    fun isStale(nowMs: Long): Boolean = nowMs - at > STALE_MS

    companion object {
        const val PATH = "/clockwise/state"
        const val MAX_TIMERS = 3
        const val MAX_EVENTS = 2
        const val STALE_MS = 3L * 3_600_000L
        private val JSON = Json { ignoreUnknownKeys = true; encodeDefaults = true; coerceInputValues = true }

        private fun minutes(m: Long, hebrew: Boolean): String = when {
            m >= 60 -> "%d:%02d %s".format(m / 60, m % 60, if (hebrew) "שע׳" else "h")
            else -> "${m.coerceAtLeast(1L)} ${if (hebrew) "דק׳" else "min"}"
        }

        fun decode(text: String?): WearState? {
            if (text.isNullOrBlank() || text.length > 8 * 1024) return null
            val s = runCatching { JSON.decodeFromString(serializer(), text) }.getOrNull() ?: return null
            if (s.v != 1) return null
            return s.copy(
                place = Untrusted.clean(s.place, 30), temp = Untrusted.clean(s.temp, 8), hiLo = Untrusted.clean(s.hiLo, 16), sky = Untrusted.clean(s.sky, 20),
                rain = s.rain?.takeIf { it in 0..100 },
                events = s.events.take(MAX_EVENTS).map { Untrusted.clean(it, 40) }.filter { it.isNotEmpty() },
                timers = s.timers.take(MAX_TIMERS).map { it.copy(l = Untrusted.clean(it.l, 24), rem = it.rem.coerceIn(0L, 86_400_000L)) },
            )
        }

        /** Builds the state from what the phone already has (formatted weather strings and the countdown list). */
        fun of(nowMs: Long, place: String, temp: String, hiLo: String, rain: Int?, sky: String, hebrew: Boolean, items: List<TimerItem>, events: List<String> = emptyList()): WearState {
            val timers = items.filter { it.countdown && it.countdownState.let { c -> c.running || (c.active && c.pausedRemainingMs in 1 until c.totalMs) } }.map { item ->
                val c = item.countdownState
                val label = item.interval?.label(hebrew) ?: item.label
                WearTimer(label, c.running, if (c.running) c.endsAtMs else 0L, if (c.running) 0L else c.pausedRemainingMs)
            }
            return WearState(nowMs, place, temp, hiLo, rain, sky, hebrew, timers.take(MAX_TIMERS), events = events.take(MAX_EVENTS))
        }
    }
}
