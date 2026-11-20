package com.clockweather.app.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.clockweather.app.R
import com.clockweather.app.data.SourceApps
import com.clockweather.app.widget.Snapshot
import com.clockweather.core.agenda.Agenda
import com.clockweather.core.alarm.AlarmIndicator
import com.clockweather.core.dashboard.DashTile
import com.clockweather.core.hebrew.HebrewDateFormat
import com.clockweather.core.hebrew.Holidays
import com.clockweather.core.sun.SunTimes
import com.clockweather.core.sun.Zmanim
import com.clockweather.core.widget.WidgetSettings
import java.time.format.DateTimeFormatter
import java.util.Locale

/** Drill-down for a dashboard card (date, events, alarm, sun). Each has a button to the app the data comes from. */
@Composable
fun DashDetailScreen(tile: DashTile, settings: WidgetSettings, onBack: () -> Unit) {
    val ctx = LocalContext.current
    val st = remember(tile, settings) { Snapshot.build(ctx) }
    val hebrew = st.hebrew
    val loc = if (hebrew) Locale("he") else Locale.ENGLISH
    Column(Modifier.verticalScroll(rememberScrollState()).padding(bottom = 24.dp)) {
        ScreenHeader(DashTitles.of(tile, hebrew), stringResource(R.string.back), onBack)
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            val today = st.now.atZone(st.zone).toLocalDate()
            when (tile) {
                DashTile.DATE -> {
                    val hd = HebrewDateFormat.hebrewDateAt(today, st.now.atZone(st.zone).toLocalTime(), st.forecast?.today(st.now)?.sunset?.toLocalTime(), settings.rollHebrewAtSunset)
                    Text(HebrewDateFormat.full(hd, hebrew), style = MaterialTheme.typography.headlineSmall)
                    Text(stringResource(R.string.next_holidays), style = MaterialTheme.typography.titleMedium)
                    val fmt = DateTimeFormatter.ofPattern(if (hebrew) "EEEE, d בMMMM" else "EEEE, d MMMM", loc)
                    val upcoming = (0..90).flatMap { d -> val day = today.plusDays(d.toLong()); Holidays.on(day, settings.inIsrael).filter { it.kind != com.clockweather.core.hebrew.HolidayKind.SHABBAT }.take(1).map { day to it } }.take(12)
                    upcoming.forEach { (day, h) -> Text("${day.format(fmt)} · ${h.name(hebrew)}", style = MaterialTheme.typography.bodyLarge) }
                    OutlinedButton(onClick = { SourceApps.openCalendar(ctx) }, modifier = Modifier.heightIn(min = 48.dp)) { Text(stringResource(R.string.open_calendar)) }
                }
                DashTile.AGENDA -> {
                    val events = Agenda.forToday(st.agenda, st.now, st.zone)
                    if (events.isEmpty()) Text(stringResource(R.string.no_events_today), style = MaterialTheme.typography.bodyLarge)
                    events.forEach { e ->
                        Row(Modifier.fillMaxWidth().heightIn(min = 48.dp).clickable(role = Role.Button) { SourceApps.openEvent(ctx, e.id) }, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text(Agenda.bannerText(e, st.now, st.zone, settings.use24h, hebrew), style = MaterialTheme.typography.bodyLarge)
                                e.location?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
                            }
                        }
                    }
                    OutlinedButton(onClick = { SourceApps.openCalendar(ctx) }, modifier = Modifier.heightIn(min = 48.dp)) { Text(stringResource(R.string.open_calendar)) }
                }
                DashTile.ALARM -> {
                    val alarm = st.nextAlarm?.takeIf { AlarmIndicator.visible(it, st.now, 0, settings.alarmPackage) }
                    Text(alarm?.let { "⏰ " + AlarmIndicator.label(it, st.now, st.zone, settings.use24h, hebrew) } ?: stringResource(R.string.no_alarm_soon), style = MaterialTheme.typography.headlineSmall)
                    alarm?.ownerPackage?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
                    Text(stringResource(R.string.alarm_detail_hint), style = MaterialTheme.typography.bodyMedium)
                    OutlinedButton(onClick = { SourceApps.openAlarmApp(ctx) }, modifier = Modifier.heightIn(min = 48.dp)) { Text(stringResource(R.string.open_alarm_app)) }
                }
                DashTile.SUN -> {
                    val place = settings.places.firstOrNull { it.id == settings.activePlaceId }
                    if (place == null) Text(stringResource(R.string.no_place_hint), style = MaterialTheme.typography.bodyLarge)
                    else {
                        val tf = DateTimeFormatter.ofPattern(if (settings.use24h) "HH:mm" else "h:mm a", Locale.ENGLISH)
                        val df = DateTimeFormatter.ofPattern(if (hebrew) "EEE d/M" else "EEE d/M", loc)
                        Text(place.name, style = MaterialTheme.typography.titleMedium)
                        (0..6).forEach { d ->
                            val day = today.plusDays(d.toLong())
                            val sun = SunTimes.compute(day, place.lat, place.lon, st.zone)
                            val z = Zmanim.of(day, place.lat, place.lon, st.zone)
                            if (sun != null && z != null) Text(
                                "${day.format(df)}   ☀↑ ${tf.format(sun.sunrise)}  ☀↓ ${tf.format(sun.sunset)}" +
                                    (z.nightfall?.let { "  🌙 ${tf.format(it)}" } ?: "") + (if (Zmanim.isCandleLightingDay(day, settings.inIsrael)) "  🕯 ${tf.format(z.candleLighting)}" else ""),
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }
                    }
                }
                else -> Text(DashTitles.of(tile, hebrew), style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}
