package com.clockweather.core.timers

import com.clockweather.core.text.Untrusted
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import java.time.LocalDate

/** "N days until <event>": a label and a calendar day. Pure; today's date is a parameter. */
@Serializable
data class DateCountdown(val id: String, val label: String, val epochDay: Long) {
    fun daysLeft(today: LocalDate): Long = epochDay - today.toEpochDay()
    val date: LocalDate get() = LocalDate.ofEpochDay(epochDay)
}

object DateCountdowns {
    const val MAX_ITEMS = 8
    private val json = Json { ignoreUnknownKeys = true; coerceInputValues = true }

    fun encode(list: List<DateCountdown>): String = json.encodeToString(ListSerializer(DateCountdown.serializer()), clean(list))
    fun decode(raw: String?): List<DateCountdown> {
        if (raw.isNullOrBlank() || raw.length > 32 * 1024) return emptyList()
        return runCatching { clean(json.decodeFromString(ListSerializer(DateCountdown.serializer()), raw)) }.getOrDefault(emptyList())
    }

    private fun clean(list: List<DateCountdown>) =
        list.map { it.copy(label = Untrusted.clean(it.label, 30)) }.filter { it.id.isNotEmpty() && it.label.isNotEmpty() }.distinctBy { it.id }.take(MAX_ITEMS)

    /** Upcoming first; events that passed more than a day ago drop out. */
    fun upcoming(list: List<DateCountdown>, today: LocalDate): List<DateCountdown> =
        list.filter { it.daysLeft(today) >= 0 }.sortedBy { it.epochDay }

    fun text(item: DateCountdown, today: LocalDate, hebrew: Boolean): String {
        val d = item.daysLeft(today)
        return when {
            d == 0L -> if (hebrew) "היום · ${item.label}" else "Today · ${item.label}"
            d == 1L -> if (hebrew) "מחר · ${item.label}" else "Tomorrow · ${item.label}"
            hebrew -> "$d ימים · ${item.label}"
            else -> "$d days · ${item.label}"
        }
    }

    /** The widget line: only the nearest event, and only within a month. */
    fun widgetLine(list: List<DateCountdown>, today: LocalDate, hebrew: Boolean): String? =
        upcoming(list, today).firstOrNull()?.takeIf { it.daysLeft(today) <= 30 }?.let { "📅 " + text(it, today, hebrew) }
}
