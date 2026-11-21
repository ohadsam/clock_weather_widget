package com.clockweather.core.timers

import com.clockweather.core.text.Untrusted
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

/** What plays when a countdown ends. Combine freely with [TimerItem.vibrate] (silent = NONE + no vibration). */
enum class SoundKind(val he: String, val en: String) {
    NONE("בלי צלצול", "No sound"), ALARM("צלצול שעון מעורר", "Alarm ringtone"), NOTIFICATION("צליל התראה", "Notification sound"), RINGTONE("צלצול טלפון", "Phone ringtone");

    companion object { fun byName(n: String): SoundKind = values().firstOrNull { it.name == n } ?: ALARM }
}

/**
 * One stopwatch or one countdown, with its own label and end-of-time alert. The state of the clock itself is the encoded
 * [Stopwatch] / [Countdown] (timestamps), so any number of them keep running with the app closed.
 */
@Serializable
data class TimerItem(
    val id: String,
    val countdown: Boolean,
    val label: String = "",
    val sw: String = "",
    val cd: String = "",
    val sound: String = SoundKind.ALARM.name,
    val vibrate: Boolean = true,
) {
    val stopwatch: Stopwatch get() = Stopwatch.decode(sw)
    val countdownState: Countdown get() = Countdown.decode(cd)
    val soundKind: SoundKind get() = SoundKind.byName(sound)

    /** Worth showing in the widget: running, paused with progress, or finished and not dismissed. */
    val active: Boolean get() = if (countdown) countdownState.let { it.running || it.finished || (it.active && it.pausedRemainingMs in 1 until it.totalMs) } else stopwatch.active

    fun with(s: Stopwatch): TimerItem = copy(sw = s.encode())
    fun with(c: Countdown): TimerItem = copy(cd = c.encode())
}

/** The list of timers: add, remove, replace, persist as JSON, and the short lines the widget shows. */
object TimerList {
    const val MAX_ITEMS = 12
    const val MAX_LABEL = 24
    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true; coerceInputValues = true }

    fun encode(list: List<TimerItem>): String = json.encodeToString(ListSerializer(TimerItem.serializer()), clean(list))

    fun decode(raw: String?): List<TimerItem> {
        if (raw.isNullOrBlank() || raw.length > 64 * 1024) return emptyList()
        return runCatching { clean(json.decodeFromString(ListSerializer(TimerItem.serializer()), raw)) }.getOrDefault(emptyList())
    }

    private fun clean(list: List<TimerItem>): List<TimerItem> =
        list.map { it.copy(label = Untrusted.clean(it.label, MAX_LABEL), sound = SoundKind.byName(it.sound).name) }
            .filter { it.id.isNotEmpty() }.distinctBy { it.id }.take(MAX_ITEMS)

    fun add(list: List<TimerItem>, item: TimerItem): List<TimerItem> = if (list.size >= MAX_ITEMS) list else list + item
    fun remove(list: List<TimerItem>, id: String): List<TimerItem> = list.filterNot { it.id == id }
    fun replace(list: List<TimerItem>, item: TimerItem): List<TimerItem> = list.map { if (it.id == item.id) item else it }

    fun nextId(list: List<TimerItem>, nowMs: Long): String {
        var n = nowMs.toString(36)
        while (list.any { it.id == n }) n += "x"
        return n
    }

    /** `⏱ Eggs 12 min` / `⏳ Pasta 3 min` for the widget; most urgent first (running countdowns by time left, then stopwatches), at most [max]. */
    fun widgetLines(list: List<TimerItem>, nowMs: Long, hebrew: Boolean, max: Int = 3): List<String> {
        val active = list.filter { it.active }
        val sorted = active.sortedWith(compareBy({ !it.countdown }, { if (it.countdown) it.countdownState.remaining(nowMs) else Long.MAX_VALUE }))
        return sorted.take(max).map { item ->
            val base = if (item.countdown) TimerFormat.widgetCountdown(item.countdownState, nowMs, hebrew) else TimerFormat.widgetStopwatch(item.stopwatch.elapsed(nowMs), hebrew)
            val label = item.label.take(10).trim()
            if (label.isEmpty()) base else base.replaceFirst(" ", " $label ")
        }
    }

    /** Earliest running countdown (the next exact alarm to arm), or null. */
    fun nextDue(list: List<TimerItem>): TimerItem? = list.filter { it.countdown && it.countdownState.running }.minByOrNull { it.countdownState.endsAtMs }
}
