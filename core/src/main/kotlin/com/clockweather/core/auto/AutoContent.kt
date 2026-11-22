package com.clockweather.core.auto

import com.clockweather.core.agenda.Agenda
import com.clockweather.core.agenda.AgendaEvent
import com.clockweather.core.alarm.AlarmIndicator
import com.clockweather.core.alarm.NextAlarm
import com.clockweather.core.dashboard.DashTile
import com.clockweather.core.sun.Zmanim
import com.clockweather.core.weather.Forecast
import com.clockweather.core.widget.WidgetSettings
import java.time.Instant
import java.time.ZoneId

/** What is true right now, as far as "what is worth showing" is concerned. Plain data, built by the app from its sources. */
data class Facts(
    val now: Instant,
    val zone: ZoneId,
    val events: List<AgendaEvent>,
    val alarm: NextAlarm?,
    val forecast: Forecast?,
    val steps: Int?,
    val placeCount: Int,
    val worldClockCount: Int,
    val worldPlaceCount: Int = 0,
)

/**
 * "Smart content" (owner request): the widget and the dashboard show what matters now and drop what is empty or irrelevant,
 * and the layout engine then gives the freed space to the rest. Pure rules, tested; the user's own switches are the upper limit
 * (auto never shows something the user switched off).
 */
object AutoContent {
    private const val ALARM_HOURS = 12
    private const val EVENT_SOON_MIN = 120

    private fun upcomingEvents(f: Facts) = Agenda.forToday(f.events, f.now, f.zone)
    private fun alarmSoon(s: WidgetSettings, f: Facts): Boolean =
        AlarmIndicator.visible(f.alarm, f.now, ALARM_HOURS, s.alarmPackage)

    /** The widget's settings after the rules; use the result for layout and drawing. */
    fun apply(s: WidgetSettings, f: Facts): WidgetSettings {
        val local = f.now.atZone(f.zone)
        val hour = local.hour
        val candle = Zmanim.isCandleLightingDay(local.toLocalDate(), s.inIsrael)
        val sunWindow = hour in 4..9 || hour in 15..22 || (candle && hour >= 12)
        val rainy = f.forecast?.today(f.now)?.let { (it.rainChancePct ?: 0) >= s.rainLikelyFromPct } == true
        return s.copy(
            showAgenda = s.showAgenda && upcomingEvents(f).isNotEmpty(),
            showAlarm = s.showAlarm && alarmSoon(s, f),
            showWeather = s.showWeather && (f.forecast != null),
            showOutlook = s.showOutlook || (s.showWeather && rainy),
            showWorldClocks = s.showWorldClocks && f.worldClockCount > 0,
            showSunTimes = s.showSunTimes && sunWindow && f.placeCount > 0,
            showSteps = s.showSteps && f.steps != null,
        )
    }
}

/** Dashboard cards in the order that matters now; empty cards are left out. */
object AutoDashboard {
    fun arrange(chosen: List<DashTile>, s: WidgetSettings, f: Facts): List<DashTile> {
        val events = Agenda.forToday(f.events, f.now, f.zone)
        val local = f.now.atZone(f.zone)
        val candle = Zmanim.isCandleLightingDay(local.toLocalDate(), s.inIsrael) && local.hour >= 12
        val nowMs = f.now.toEpochMilli()
        val eventSoon = events.any { it.allDay.not() && it.startMs - nowMs <= 120 * 60_000L } || events.any { it.startMs <= nowMs && !it.allDay }
        val alarmSoon = AlarmIndicator.visible(f.alarm, f.now, 3, s.alarmPackage)
        val rainy = f.forecast?.today(f.now)?.let { (it.rainChancePct ?: 0) >= s.rainLikelyFromPct } == true

        val present = chosen.filter {
            when (it) {
                DashTile.AGENDA -> events.isNotEmpty()
                DashTile.ALARM -> AlarmIndicator.visible(f.alarm, f.now, 0, s.alarmPackage)
                DashTile.PLACES -> f.placeCount >= 2
                DashTile.STEPS -> f.steps != null
                DashTile.WORLD -> f.worldClockCount > 0
                DashTile.STOPWATCH, DashTile.COUNTDOWN, DashTile.DATE_COUNTDOWN, DashTile.SPECIAL_EVENTS -> true
                DashTile.WORLD_PLACES -> f.worldPlaceCount > 0
                DashTile.SUN -> f.placeCount > 0
                DashTile.WEATHER -> f.forecast != null || f.placeCount == 0
                else -> true
            }
        }
        val hot = buildList {
            if (alarmSoon) add(DashTile.ALARM)
            if (eventSoon) add(DashTile.AGENDA)
            if (rainy) add(DashTile.WEATHER)
            if (candle) add(DashTile.SUN)
        }.filter { it in present }
        val head = present.filter { it == DashTile.WIDGET || it == DashTile.TODAY }
        return head + hot + present.filter { it !in head && it !in hot }
    }
}
