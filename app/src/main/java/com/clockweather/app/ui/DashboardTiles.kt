package com.clockweather.app.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.clockweather.app.R
import com.clockweather.app.data.SourceApps
import com.clockweather.app.widget.RenderState
import com.clockweather.core.agenda.Agenda
import com.clockweather.core.alarm.AlarmIndicator
import com.clockweather.core.clock.WorldClock
import com.clockweather.core.hebrew.HebrewDateFormat
import com.clockweather.core.hebrew.Holidays
import com.clockweather.core.sun.SunTimes
import com.clockweather.core.sun.Zmanim
import com.clockweather.core.weather.TempFormat
import kotlinx.coroutines.delay
import java.time.format.DateTimeFormatter
import java.util.Locale

/** One dashboard card: slides in (staggered, skipped in calm mode), tap opens the drill-down, an optional button opens the source app. */
@Composable
internal fun DashCard(
    index: Int, animate: Boolean, title: String, onClick: (() -> Unit)?,
    sourceLabel: String? = null, onSource: (() -> Unit)? = null, content: @Composable ColumnScope.() -> Unit,
) {
    var shown by remember { mutableStateOf(!animate) }
    LaunchedEffect(Unit) { if (!shown) { delay(index * 80L); shown = true } }
    AnimatedVisibility(shown, enter = fadeIn(tween(400)) + slideInVertically(tween(400)) { it / 5 }) {
        Card(Modifier.fillMaxWidth().let { if (onClick != null) it.clickable(role = Role.Button, onClick = onClick) else it }) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                content()
                if (sourceLabel != null && onSource != null) OutlinedButton(onClick = onSource, modifier = Modifier.heightIn(min = 48.dp)) { Text(sourceLabel) }
            }
        }
    }
}

@Composable
internal fun DateTile(st: RenderState, index: Int, animate: Boolean, onOpen: () -> Unit) {
    val s = st.settings
    val local = st.now.atZone(st.zone)
    val loc = if (st.hebrew) Locale("he") else Locale.ENGLISH
    val sunset = st.forecast?.today(st.now)?.sunset?.toLocalTime()
    val hd = HebrewDateFormat.hebrewDateAt(local.toLocalDate(), local.toLocalTime(), sunset, s.rollHebrewAtSunset)
    val holiday = Holidays.on(local.toLocalDate(), s.inIsrael).firstOrNull()
    DashCard(index, animate, DashTitles.of(com.clockweather.core.dashboard.DashTile.DATE, st.hebrew), onOpen) {
        Text(DateTimeFormatter.ofPattern(if (st.hebrew) "EEEE, d בMMMM yyyy" else "EEEE, d MMMM yyyy", loc).format(local), style = MaterialTheme.typography.titleLarge)
        Text(HebrewDateFormat.full(hd, st.hebrew), style = MaterialTheme.typography.bodyLarge)
        if (holiday != null) Text("✡ " + holiday.name(st.hebrew), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
    }
}

@Composable
internal fun WeatherTile(st: RenderState, index: Int, animate: Boolean, onOpen: () -> Unit) {
    val s = st.settings
    val f = st.forecast
    val day = f?.today(st.now)
    DashCard(index, animate, DashTitles.of(com.clockweather.core.dashboard.DashTile.WEATHER, st.hebrew), onOpen) {
        if (f == null || day == null) Text(stringResource(R.string.no_weather_yet), style = MaterialTheme.typography.bodyMedium)
        else Row(verticalAlignment = Alignment.CenterVertically) {
            SkyIcon(f.current?.sky ?: day.sky, f.current?.isDay ?: true, sizeDp = 72, animate = animate && s.animateWeather)
            Column(Modifier.padding(start = 12.dp)) {
                Text(s.places.firstOrNull { it.id == s.activePlaceId }?.name ?: "", style = MaterialTheme.typography.bodyMedium)
                f.current?.let { Text(TempFormat.short(it.tempC, s.fahrenheit), style = MaterialTheme.typography.displaySmall) }
                Text("${TempFormat.short(day.maxC, s.fahrenheit)} / ${TempFormat.short(day.minC, s.fahrenheit)}" + (day.rainChancePct?.let { "   ☂ $it%" } ?: ""), style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
internal fun AlarmTile(st: RenderState, index: Int, animate: Boolean, onOpen: () -> Unit) {
    val ctx = LocalContext.current
    val alarm = st.nextAlarm?.takeIf { AlarmIndicator.visible(it, st.now, 0, st.settings.alarmPackage) }
    DashCard(index, animate, DashTitles.of(com.clockweather.core.dashboard.DashTile.ALARM, st.hebrew), onOpen, stringResource(R.string.open_alarm_app), { SourceApps.openAlarmApp(ctx) }) {
        Text(
            alarm?.let { "⏰ " + AlarmIndicator.label(it, st.now, st.zone, st.settings.use24h, st.hebrew) } ?: stringResource(R.string.no_alarm_soon),
            style = MaterialTheme.typography.titleLarge,
        )
    }
}

@Composable
internal fun AgendaTile(st: RenderState, index: Int, animate: Boolean, calendarAllowed: Boolean, onOpen: () -> Unit) {
    val ctx = LocalContext.current
    val events = Agenda.forToday(st.agenda, st.now, st.zone)
    DashCard(index, animate, DashTitles.of(com.clockweather.core.dashboard.DashTile.AGENDA, st.hebrew), onOpen, stringResource(R.string.open_calendar), { SourceApps.openCalendar(ctx) }) {
        when {
            !calendarAllowed -> Text(stringResource(R.string.calendar_not_allowed), style = MaterialTheme.typography.bodyMedium)
            events.isEmpty() -> Text(stringResource(R.string.no_events_today), style = MaterialTheme.typography.bodyMedium)
            else -> {
                events.take(3).forEach { Text(Agenda.bannerText(it, st.now, st.zone, st.settings.use24h, st.hebrew), style = MaterialTheme.typography.bodyLarge) }
                if (events.size > 3) Text("+${events.size - 3}", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
internal fun SunTile(st: RenderState, index: Int, animate: Boolean, onOpen: () -> Unit) {
    val s = st.settings
    val place = s.places.firstOrNull { it.id == s.activePlaceId }
    val date = st.now.atZone(st.zone).toLocalDate()
    val fmt = DateTimeFormatter.ofPattern(if (s.use24h) "HH:mm" else "h:mm a", Locale.ENGLISH)
    val sun = place?.let { SunTimes.compute(date, it.lat, it.lon, st.zone) }
    val z = place?.let { Zmanim.of(date, it.lat, it.lon, st.zone) }
    DashCard(index, animate, DashTitles.of(com.clockweather.core.dashboard.DashTile.SUN, st.hebrew), onOpen) {
        if (sun == null || z == null) Text(stringResource(R.string.no_place_hint), style = MaterialTheme.typography.bodyMedium)
        else {
            Text("☀↑ ${fmt.format(sun.sunrise)}    ☀↓ ${fmt.format(sun.sunset)}", style = MaterialTheme.typography.titleMedium)
            val extra = listOfNotNull(
                if (Zmanim.isCandleLightingDay(date, s.inIsrael)) "🕯 ${fmt.format(z.candleLighting)}" else null,
                z.nightfall?.let { "🌙 ${fmt.format(it)}" },
            )
            if (extra.isNotEmpty()) Text(extra.joinToString("    "), style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
internal fun StepsTile(st: RenderState, index: Int, animate: Boolean, allowed: Boolean) {
    val steps = st.steps
    val shown by animateIntAsState(steps ?: 0, tween(if (animate) 900 else 0), label = "steps")
    DashCard(index, animate, DashTitles.of(com.clockweather.core.dashboard.DashTile.STEPS, st.hebrew), null) {
        if (steps == null || !allowed) Text(stringResource(R.string.steps_enable_hint), style = MaterialTheme.typography.bodyMedium)
        else Text("👣 " + "%,d".format(shown), style = MaterialTheme.typography.displaySmall)
    }
}

@Composable
internal fun WorldTile(st: RenderState, index: Int, animate: Boolean) {
    val lines = st.settings.clockZones().mapNotNull { WorldClock.line(st.now, st.zone, it, st.settings.use24h) }
    DashCard(index, animate, DashTitles.of(com.clockweather.core.dashboard.DashTile.WORLD, st.hebrew), null) {
        if (lines.isEmpty()) Text(stringResource(R.string.no_world_clocks), style = MaterialTheme.typography.bodyMedium)
        lines.forEach { l ->
            val shift = WorldClock.shiftText(l.dayShift, st.hebrew)
            Text("${l.label}  ${l.time}" + if (shift.isNotEmpty()) " ($shift)" else "", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

internal object DashTitles {
    fun of(t: com.clockweather.core.dashboard.DashTile, hebrew: Boolean): String = if (hebrew) t.he else t.en
}

@Composable
internal fun WorldPlacesTile(st: RenderState, index: Int, animate: Boolean, onOpenWeather: (String) -> Unit, onAdd: () -> Unit) {
    val ctx = LocalContext.current
    val s = st.settings
    val tf = DateTimeFormatter.ofPattern(if (s.use24h) "HH:mm" else "h:mm a", Locale.ENGLISH)
    DashCard(index, animate, DashTitles.of(com.clockweather.core.dashboard.DashTile.WORLD_PLACES, st.hebrew), null, stringResource(R.string.add_world_place), onAdd) {
        if (s.worldPlaces.isEmpty()) Text(stringResource(R.string.world_places_hint), style = MaterialTheme.typography.bodyMedium)
        s.worldPlaces.forEach { p ->
            val f = remember(p.id, st.now.epochSecond / 60) { com.clockweather.app.data.WeatherRepository.cached(ctx, p.id) }
            val day = f?.today(st.now)
            Row(Modifier.fillMaxWidth().heightIn(min = 56.dp).clickable(role = Role.Button) { onOpenWeather(p.id) }, verticalAlignment = Alignment.CenterVertically) {
                if (f != null && day != null) SkyIcon(f.current?.sky ?: day.sky, f.current?.isDay ?: true, sizeDp = 44, animate = false)
                Column(Modifier.weight(1f).padding(start = 10.dp)) {
                    Text(p.name, style = MaterialTheme.typography.titleSmall)
                    if (day != null) Text("${TempFormat.short(day.maxC, s.fahrenheit)} / ${TempFormat.short(day.minC, s.fahrenheit)}" + (day.rainChancePct?.let { "   ☂ $it%" } ?: ""), style = MaterialTheme.typography.bodySmall)
                    else Text(stringResource(R.string.no_weather_yet), style = MaterialTheme.typography.bodySmall)
                }
                if (f != null) Column(horizontalAlignment = Alignment.End) {
                    Text(st.now.atZone(f.zone).format(tf), style = MaterialTheme.typography.titleMedium)
                    f.current?.let { Text(TempFormat.short(it.tempC, s.fahrenheit), style = MaterialTheme.typography.bodyLarge) }
                }
            }
        }
    }
}

@Composable
internal fun MapTile(st: RenderState, index: Int, animate: Boolean, onOpen: () -> Unit) {
    val s = st.settings
    DashCard(index, animate, DashTitles.of(com.clockweather.core.dashboard.DashTile.MAP, st.hebrew), onOpen, stringResource(R.string.open_map), onOpen) {
        Text(stringResource(R.string.map_tile_hint, s.places.size, s.worldPlaces.size), style = MaterialTheme.typography.bodyMedium)
    }
}
