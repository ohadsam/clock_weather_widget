package com.clockweather.core.weather

import java.time.Instant
import java.time.ZoneId

/** "Rain is about to start": the first wet hour in the next hours, when it is dry now. Pure; the worker shows the notification. */
object RainAlert {
    data class Alert(val inMinutes: Int, val chancePct: Int)

    const val LOOKAHEAD_HOURS = 3
    const val MIN_GAP_HOURS = 6

    private fun wet(h: HourPoint, thresholdPct: Int) = (h.rainChancePct ?: 0) >= thresholdPct || h.sky.isWet

    fun next(hours: List<HourPoint>, now: Instant, zone: ZoneId, thresholdPct: Int): Alert? {
        val local = now.atZone(zone).toLocalDateTime()
        val current = hours.lastOrNull { !it.time.isAfter(local) }
        if (current != null && wet(current, thresholdPct)) return null // already raining or likely: nothing new to announce
        val soon = hours.firstOrNull { it.time.isAfter(local) && it.time.isBefore(local.plusHours(LOOKAHEAD_HOURS.toLong())) && wet(it, thresholdPct) } ?: return null
        val minutes = java.time.Duration.between(local, soon.time).toMinutes().toInt().coerceAtLeast(1)
        return Alert(minutes, soon.rainChancePct ?: 100)
    }

    fun mayNotify(lastAlertMs: Long, nowMs: Long): Boolean = nowMs - lastAlertMs >= MIN_GAP_HOURS * 3_600_000L
}
