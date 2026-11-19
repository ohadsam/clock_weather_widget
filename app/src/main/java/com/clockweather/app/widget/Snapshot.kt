package com.clockweather.app.widget

import android.content.Context
import com.clockweather.app.data.AlarmSource
import com.clockweather.app.data.CalendarSource
import com.clockweather.app.data.SettingsStore
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

    fun build(ctx: Context, now: Instant = Instant.now(), flipFrom: IntArray? = null, progress: Float = 1f, kind: WidgetKind = WidgetKind.CLASSIC): RenderState {
        val s = SettingsStore.load(ctx)
        val zone = ZoneId.systemDefault()
        val forecast = s.activePlaceId?.let { WeatherRepository.cached(ctx, it) }
        val shaped = kind.shape(s)
        val agenda = if (shaped.showAgenda) agenda(ctx, s.agendaCalendarIds, now, zone) else emptyList()
        return RenderState(
            settings = s, kind = kind, now = now, zone = zone, hebrew = isHebrew(ctx, s.language), forecast = forecast,
            nextAlarm = if (shaped.showAlarm) AlarmSource.next(ctx) else null, agenda = agenda,
            flipFrom = flipFrom, flipProgress = progress,
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
