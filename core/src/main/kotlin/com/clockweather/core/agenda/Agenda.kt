package com.clockweather.core.agenda

import com.clockweather.core.text.Untrusted
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/** One calendar event as read from the device calendar (Outlook syncs into it). Titles are untrusted text. */
data class AgendaEvent(
    val id: Long,
    val title: String,
    val startMs: Long,
    val endMs: Long,
    val allDay: Boolean,
    val location: String? = null,
)

object Agenda {
    /** Events that are still relevant today (not yet ended, starting before local midnight), ongoing first, then by start. */
    fun forToday(all: List<AgendaEvent>, now: Instant, zone: ZoneId, includeAllDay: Boolean = true): List<AgendaEvent> {
        val nowMs = now.toEpochMilli()
        val endOfDay = now.atZone(zone).toLocalDate().plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()
        return all.asSequence()
            .filter { includeAllDay || !it.allDay }
            .filter { it.endMs > nowMs && it.startMs < endOfDay }
            .map { it.copy(title = Untrusted.clean(it.title, 80), location = it.location?.let { l -> Untrusted.clean(l, 60) }) }
            .filter { it.title.isNotEmpty() }
            .sortedWith(compareBy<AgendaEvent> { !(it.allDay || it.startMs <= nowMs) }.thenBy { it.startMs })
            .take(20).toList()
    }

    /** The banner rotates through [events]: the same event for [rotateSeconds], then the next (deterministic from the clock). */
    fun current(events: List<AgendaEvent>, now: Instant, rotateSeconds: Int): AgendaEvent? {
        if (events.isEmpty()) return null
        val slot = (now.epochSecond / rotateSeconds.coerceAtLeast(3)).toInt()
        return events[Math.floorMod(slot, events.size)]
    }

    /** `10:30 · Title`, `Now · Title`, `In 25 min · Title`, `All day · Title`. */
    fun bannerText(e: AgendaEvent, now: Instant, zone: ZoneId, use24h: Boolean, hebrew: Boolean): String {
        val nowMs = now.toEpochMilli()
        val prefix = when {
            e.allDay -> if (hebrew) "כל היום" else "All day"
            e.startMs <= nowMs -> if (hebrew) "עכשיו" else "Now"
            e.startMs - nowMs <= 60 * 60_000L -> {
                val min = ((e.startMs - nowMs + 59_999) / 60_000).toInt().coerceAtLeast(1)
                if (hebrew) "בעוד $min ד׳" else "In $min min"
            }
            else -> DateTimeFormatter.ofPattern(if (use24h) "HH:mm" else "h:mm a", Locale.ENGLISH).format(Instant.ofEpochMilli(e.startMs).atZone(zone))
        }
        return "$prefix · ${e.title}"
    }

    fun moreCount(events: List<AgendaEvent>): Int = (events.size - 1).coerceAtLeast(0)
}
