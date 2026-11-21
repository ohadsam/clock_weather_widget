package com.clockweather.app.widget

import android.content.Context
import com.clockweather.app.data.AlarmSource
import com.clockweather.core.auto.AutoContent
import com.clockweather.core.timers.DateCountdowns
import com.clockweather.core.timers.TimerList
import com.clockweather.core.widget.Appearance
import com.clockweather.core.widget.Season
import com.clockweather.core.auto.Facts
import com.clockweather.app.data.CalendarSource
import com.clockweather.app.data.SettingsStore
import com.clockweather.app.data.SpecialEventsSource
import com.clockweather.app.data.StepSource
import com.clockweather.core.agenda.SpecialEvents
import com.clockweather.app.data.TimerStore
import com.clockweather.app.data.WeatherRepository
import com.clockweather.core.agenda.AgendaEvent
import com.clockweather.core.widget.WidgetKind
import java.time.Instant
import java.time.ZoneId
import java.util.Locale

/** Collects the inputs for one frame (settings, cached forecast, next alarm, today's events). Cheap: nothing here hits the network. */
object Snapshot {
    private var agendaAt = 0L
    private var agendaCache: List<AgendaEvent> = emptyList()

    fun isHebrew(ctx: Context, language: String): Boolean = when (language) {
        "he" -> true
        "en" -> false
        else -> Locale.getDefault().language.let { it == "he" || it == "iw" }
    }

    fun build(ctx: Context, now: Instant = Instant.now(), flipFrom: IntArray? = null, progress: Float = 1f, kind: WidgetKind = WidgetKind.CLASSIC, animMs: Long = -1L, lidOpen: Float = 1f, forDashboard: Boolean = false, widgetId: Int? = null): RenderState {
        val s = SettingsStore.load(ctx).forWidget(widgetId)
        val zone = ZoneId.systemDefault()
        val forecast = s.activePlaceId?.let { WeatherRepository.cached(ctx, it) }
        // the in-app dashboard always has the data, whatever the widget is set to hide
        val shaped = if (forDashboard) s.copy(showAgenda = true, showAlarm = true, showSteps = true) else kind.shape(s)
        val agenda = if (shaped.showAgenda) agenda(ctx, s.agendaCalendarIds, now, zone) else emptyList()
        val alarm = if (shaped.showAlarm) AlarmSource.next(ctx) else null
        val steps = if (shaped.showSteps) StepSource.today(ctx) else null
        // Smart content: drop what is empty or irrelevant right now (the layout then gives the space to the rest)
        val effective = if (s.autoContent && !forDashboard) AutoContent.apply(s, Facts(now, zone, agenda, alarm, forecast, steps, s.places.size, s.worldClocks.size)) else s
        val hebrewNow = isHebrew(ctx, s.language)
        val today = now.atZone(zone).toLocalDate()
        val specialText = if (effective.showSpecialEvents) SpecialEvents.widgetLine(special(ctx, now, zone, effective.specialEventsDays), hebrewNow) else null
        val lines = (listOfNotNull(specialText) +
            (if (effective.showTimers) listOfNotNull(DateCountdowns.widgetLine(TimerStore.dates(ctx), today, hebrewNow)) + TimerList.widgetLines(TimerStore.items(ctx), now.toEpochMilli(), hebrewNow, 3) else emptyList())).take(3)
        // night palette / holiday look, chosen from the clock (the widget and the in-app preview use the same result)
        val local = now.atZone(zone)
        val fday = forecast?.today(now)
        val night = Appearance.isNight(local.hour, local.minute, fday?.sunrise?.let { it.hour * 60 + it.minute }, fday?.sunset?.let { it.hour * 60 + it.minute })
        val seasonDay = Season.effectiveDay(today, local.toLocalTime(), fday?.sunset?.toLocalTime())
        val withTimers = effective.copy(timerRowActive = lines.isNotEmpty(), paletteId = Appearance.paletteId(effective, seasonDay, night))
        return RenderState(
            timerLines = lines,
            settings = withTimers, kind = kind, now = now, zone = zone, hebrew = isHebrew(ctx, s.language), forecast = forecast,
            nextAlarm = alarm, agenda = agenda, steps = steps,
            flipFrom = flipFrom, flipProgress = progress, animMs = animMs, lidOpen = lidOpen,
        )
    }

    @Synchronized
    private fun agenda(ctx: Context, ids: List<Long>, now: Instant, zone: ZoneId): List<AgendaEvent> {
        if (now.toEpochMilli() - agendaAt > 3 * 60_000L) {
            agendaCache = CalendarSource.today(ctx, ids, now, zone)
            agendaAt = now.toEpochMilli()
        }
        return agendaCache
    }

    private var specialCache: List<SpecialEvents.Item> = emptyList()
    private var specialAt = 0L
    private var specialDays = -1

    /** Special events within [days] days; the calendar query is cached for 3 minutes (the widget redraws every minute). */
    @Synchronized
    fun special(ctx: Context, now: Instant, zone: ZoneId, days: Int): List<SpecialEvents.Item> {
        if (now.toEpochMilli() - specialAt > 3 * 60_000L || days != specialDays) {
            specialCache = SpecialEventsSource.upcoming(ctx, now, zone, days)
            specialAt = now.toEpochMilli(); specialDays = days
        }
        return specialCache
    }

    @Synchronized
    fun invalidateAgenda() { agendaAt = 0L; specialAt = 0L }
}
