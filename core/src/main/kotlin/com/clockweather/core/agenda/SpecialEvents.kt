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

    /** "today" / "tomorrow" / "in 3 days" for the card and the watch. */
    fun whenText(daysLeft: Long, hebrew: Boolean): String = when {
        daysLeft <= 0L -> if (hebrew) "היום" else "today"
        daysLeft == 1L -> if (hebrew) "מחר" else "tomorrow"
        else -> if (hebrew) "בעוד $daysLeft ימים" else "in $daysLeft days"
    }

    /** The symbol for an event type name written by the Special Events app (`EventType`), else guessed from the title. */
    fun emojiForType(type: String, title: String): String = when (type) {
        "BIRTHDAY" -> "🎂"; "ANNIVERSARY" -> "💍"; "YAHRZEIT" -> "🕯"; "BAR_BAT_MITZVAH" -> "🎉"; else -> emojiFor(title)
    }

    /** A row read from the other app's provider (already the next occurrence). */
    fun fromRow(title: String, type: String, epochDay: Long, today: LocalDate): Item? {
        val clean = Untrusted.clean(title, 40)
        val days = epochDay - today.toEpochDay()
        return if (clean.isEmpty() || days < 0) null else Item(clean, LocalDate.ofEpochDay(epochDay), days, emojiForType(type, clean))
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
