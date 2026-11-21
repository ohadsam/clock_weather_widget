package com.clockweather.core.agenda

import com.clockweather.core.text.Untrusted
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/**
 * Birthdays, anniversaries and other special events (written by the Special Events app into its own device calendar) as a short list
 * of "N days until ..." lines for the widget and the dashboard. Pure: events and today's date are parameters.
 */
object SpecialEvents {
    data class Item(val title: String, val date: LocalDate, val daysLeft: Long, val emoji: String)

    /** A fitting symbol from the title (Hebrew or English). */
    fun emojiFor(title: String): String {
        val t = title.lowercase()
        return when {
            "הולדת" in t || "birthday" in t -> "🎂"
            "נישואין" in t || "anniversary" in t || "חתונה" in t -> "💍"
            "אזכרה" in t || "yahrzeit" in t || "ז״ל" in t || "ז\"ל" in t -> "🕯"
            "מצווה" in t || "מצוה" in t || "mitzvah" in t -> "🎉"
            else -> "⭐"
        }
    }

    fun upcoming(events: List<AgendaEvent>, today: LocalDate, zone: ZoneId, withinDays: Int, max: Int = 8): List<Item> =
        events.asSequence()
            .map { e -> Triple(e, Instant.ofEpochMilli(e.startMs).atZone(zone).toLocalDate(), Untrusted.clean(e.title, 40)) }
            .filter { (_, d, t) -> t.isNotEmpty() && d.toEpochDay() - today.toEpochDay() in 0..withinDays.toLong() }
            .map { (_, d, t) -> Item(t, d, d.toEpochDay() - today.toEpochDay(), emojiFor(t)) }
            .distinctBy { it.title to it.date }
            .sortedWith(compareBy({ it.daysLeft }, { it.title }))
            .take(max).toList()

    fun text(i: Item, hebrew: Boolean): String {
        val when_ = when {
            i.daysLeft == 0L -> if (hebrew) "היום" else "today"
            i.daysLeft == 1L -> if (hebrew) "מחר" else "tomorrow"
            hebrew -> "בעוד ${i.daysLeft} ימים"
            else -> "in ${i.daysLeft} days"
        }
        return "${i.emoji} ${i.title} · $when_"
    }

    /** The widget shows only the nearest one. */
    fun widgetLine(items: List<Item>, hebrew: Boolean): String? = items.firstOrNull()?.let { text(it, hebrew) }
}
