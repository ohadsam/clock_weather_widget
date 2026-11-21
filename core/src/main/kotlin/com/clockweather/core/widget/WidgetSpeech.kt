package com.clockweather.core.widget

/** One spoken sentence for TalkBack describing the whole widget ("Thursday 17:05, 25 degrees, rain expected, alarm 06:30"). Pure join of ready texts. */
object WidgetSpeech {
    fun sentence(hebrew: Boolean, timeText: String, dateText: String?, weatherText: String?, extras: List<String>): String {
        val head = listOfNotNull(dateText?.takeIf { it.isNotBlank() }, timeText).joinToString(" ")
        return (listOf(head) + listOfNotNull(weatherText?.takeIf { it.isNotBlank() }) + extras.filter { it.isNotBlank() }).joinToString(if (hebrew) ", " else ", ")
    }
}
