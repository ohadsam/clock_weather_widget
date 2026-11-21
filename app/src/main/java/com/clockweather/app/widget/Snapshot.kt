package com.clockweather.app.widget

import android.content.Context
import com.clockweather.app.data.AlarmSource
import com.clockweather.core.auto.AutoContent
import com.clockweather.core.auto.Facts
import com.clockweather.app.data.CalendarSource
import com.clockweather.app.data.SettingsStore
import com.clockweather.app.data.StepSource
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

    fun build(ctx: Context, now: Instant = Instant.now(), flipFrom: IntArray? = null, progress: Float = 1f, kind: WidgetKind = WidgetKind.CLASSIC, animMs: Long = -1L, lidOpen: Float = 1f, forDashboard: Boolean = false): RenderState {
        val s = SettingsStore.load(ctx)
        val zone = ZoneId.systemDefault()
        val forecast = s.activePlaceId?.let { WeatherRepository.cached(ctx, it) }
        // the in-app dashboard always has the data, whatever the widget is set to hide
        val shaped = if (forDashboard) s.copy(showAgenda = true, showAlarm = true, showSteps = true) else kind.shape(s)
        val agenda = if (shaped.showAgenda) agenda(ctx, s.agendaCalendarIds, now, zone) else emptyList()
        val alarm = if (shaped.showAlarm) AlarmSource.next(ctx) else null
        val steps = if (shaped.showSteps) StepSource.today(ctx) else null
        // Smart content: drop what is empty or irrelevant right now (the layout then gives the space to the rest)
        val effective = if (s.autoContent && !forDashboard) AutoContent.apply(s, Facts(now, zone, agenda, alarm, forecast, steps, s.places.size, s.worldClocks.size)) else s
        return RenderState(
            settings = effective, kind = kind, now = now, zone = zone, hebrew = isHebrew(ctx, s.language), forecast = forecast,
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

    @Synchronized
    fun invalidateAgenda() { agendaAt = 0L }
}
