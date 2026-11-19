package com.clockweather.app.widget

import com.clockweather.core.agenda.AgendaEvent
import com.clockweather.core.alarm.NextAlarm
import com.clockweather.core.weather.Forecast
import com.clockweather.core.widget.WidgetKind
import com.clockweather.core.widget.WidgetSettings
import java.time.Instant
import java.time.ZoneId

/** Everything one rendered frame of the widget depends on. Plain data: the renderer has no other inputs. */
data class RenderState(
    val settings: WidgetSettings,
    val kind: WidgetKind = WidgetKind.CLASSIC,
    val now: Instant,
    val zone: ZoneId,
    val hebrew: Boolean,
    val forecast: Forecast?,
    val nextAlarm: NextAlarm?,
    val agenda: List<AgendaEvent>,
    val steps: Int? = null,
    /** Flip animation: digits shown before the change and progress 0..1 (null = static). */
    val flipFrom: IntArray? = null,
    val flipProgress: Float = 1f,
)
