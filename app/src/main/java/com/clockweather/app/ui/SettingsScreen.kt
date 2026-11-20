package com.clockweather.app.ui

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.clockweather.app.R
import com.clockweather.app.data.AlarmSource
import com.clockweather.app.data.CalendarSource
import com.clockweather.app.data.LocationSource
import com.clockweather.app.data.StepSource
import com.clockweather.app.data.StepWorker
import com.clockweather.app.data.WeatherRepository
import com.clockweather.core.clock.WorldClock
import com.clockweather.core.weather.OpenMeteo
import com.clockweather.core.widget.ClockStyle
import com.clockweather.core.widget.ClockZoneSetting
import com.clockweather.core.widget.Palettes
import com.clockweather.core.widget.PlaceSetting
import com.clockweather.core.widget.Preset
import com.clockweather.core.widget.WidgetSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** All options, grouped in collapsed sections with a one-line summary each. Every change is applied immediately. */
@Composable
fun SettingsScreen(s: WidgetSettings, onChange: (WidgetSettings) -> Unit, onBack: () -> Unit) {
    val ctx = LocalContext.current
    val stepPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { ok ->
        if (ok) { StepWorker.schedule(ctx); Thread { StepSource.refresh(ctx); com.clockweather.app.widget.ClockWidgetProvider.updateAll(ctx) }.start() }
    }
    val hebrew = androidx.compose.ui.platform.LocalConfiguration.current.locales[0].language.let { it == "he" || it == "iw" }
    Column(Modifier.verticalScroll(rememberScrollState()).padding(bottom = 32.dp)) {
        ScreenHeader(stringResource(R.string.settings), stringResource(R.string.back), onBack)

        SectionCard(stringResource(R.string.sec_look), stringResource(R.string.sec_look_sum, stringResource(styleName(s.clockStyle)), s.backgroundAlphaPct), startOpen = true) {
            ChipRow(ClockStyle.values().map { it to stringResource(styleName(it)) }, { it == s.clockStyle }, { onChange(s.copy(clockStyle = it)) })
            Text(stringResource(R.string.presets), style = MaterialTheme.typography.titleSmall)
            ChipRow(Preset.values().map { it to (if (hebrew) it.he else it.en) }, { false }, { onChange(it.apply(s)) })
            Text(stringResource(R.string.palette), style = MaterialTheme.typography.titleSmall)
            ChipRow(Palettes.ALL.map { it.id to (if (hebrew) it.he else it.en) }, { it == s.paletteId }, { onChange(s.copy(paletteId = it)) })
            SliderRow(stringResource(R.string.bg_transparency), 100 - s.backgroundAlphaPct, 0..100, "${100 - s.backgroundAlphaPct}%") { onChange(s.copy(backgroundAlphaPct = 100 - it)) }
            SliderRow(stringResource(R.string.panel_opacity), s.panelAlphaPct, 0..100, "${s.panelAlphaPct}%") { onChange(s.copy(panelAlphaPct = it)) }
            SliderRow(stringResource(R.string.corner_radius), s.cornerRadiusDp, 0..36, "${s.cornerRadiusDp} dp") { onChange(s.copy(cornerRadiusDp = it)) }
            SliderRow(stringResource(R.string.clock_size), s.scalePct, 50..150, "${s.scalePct}%") { onChange(s.copy(scalePct = it)) }
            SwitchRow(stringResource(R.string.use_24h), s.use24h) { onChange(s.copy(use24h = it)) }
            SwitchRow(stringResource(R.string.calm_animations), s.calmAnimations) { onChange(s.copy(calmAnimations = it)) }
            SwitchRow(stringResource(R.string.animate_weather), s.animateWeather) { onChange(s.copy(animateWeather = it)) }
            Text(stringResource(R.string.widget_language), style = MaterialTheme.typography.titleSmall)
            ChipRow(listOf("system" to R.string.lang_system, "he" to R.string.lang_he, "en" to R.string.lang_en).map { it.first to stringResource(it.second) }, { it == s.language }, { onChange(s.copy(language = it)) })
        }

        SectionCard(stringResource(R.string.sec_show), stringResource(R.string.sec_show_sum)) {
            SwitchRow(stringResource(R.string.show_clock), s.showClock) { onChange(s.copy(showClock = it)) }
            SwitchRow(stringResource(R.string.show_weather), s.showWeather) { onChange(s.copy(showWeather = it)) }
            SwitchRow(stringResource(R.string.show_hilo), s.showHiLo) { onChange(s.copy(showHiLo = it)) }
            SwitchRow(stringResource(R.string.show_outlook), s.showOutlook) { onChange(s.copy(showOutlook = it)) }
            SwitchRow(stringResource(R.string.show_gregorian), s.showGregorianDate) { onChange(s.copy(showGregorianDate = it)) }
            SwitchRow(stringResource(R.string.show_hebrew), s.showHebrewDate) { onChange(s.copy(showHebrewDate = it)) }
            SwitchRow(stringResource(R.string.show_holidays), s.showHolidays) { onChange(s.copy(showHolidays = it)) }
            SwitchRow(stringResource(R.string.show_alarm), s.showAlarm) { onChange(s.copy(showAlarm = it)) }
            SwitchRow(stringResource(R.string.show_agenda), s.showAgenda) { onChange(s.copy(showAgenda = it)) }
            SwitchRow(stringResource(R.string.show_world), s.showWorldClocks) { onChange(s.copy(showWorldClocks = it)) }
            SwitchRow(stringResource(R.string.show_sun), s.showSunTimes) { onChange(s.copy(showSunTimes = it)) }
            if (s.showSunTimes) SwitchRow(stringResource(R.string.show_nightfall), s.showNightfall) { onChange(s.copy(showNightfall = it)) }
            SwitchRow(stringResource(R.string.show_steps), s.showSteps) { on ->
                onChange(s.copy(showSteps = on))
                if (!on) StepWorker.cancel(ctx)
                else if (!StepSource.supported(ctx)) Unit
                else if (!StepSource.granted(ctx)) stepPermission.launch(Manifest.permission.ACTIVITY_RECOGNITION)
                else { StepWorker.schedule(ctx); Thread { StepSource.refresh(ctx); com.clockweather.app.widget.ClockWidgetProvider.updateAll(ctx) }.start() }
            }
            if (s.showSteps && !StepSource.supported(ctx)) Text(stringResource(R.string.steps_unsupported), style = MaterialTheme.typography.bodySmall)
            Text(stringResource(R.string.show_hint), style = MaterialTheme.typography.bodySmall)
        }

        PlacesSection(s, onChange)

        SectionCard(stringResource(R.string.sec_clocks), stringResource(R.string.sec_clocks_sum, s.worldClocks.size)) {
            Text(stringResource(R.string.clocks_hint, WidgetSettings.MAX_WORLD_CLOCKS), style = MaterialTheme.typography.bodySmall)
            ChipRow(WorldClock.POPULAR.map { it to it.substringAfterLast('/').replace('_', ' ') }, { id -> s.worldClocks.any { it.zoneId == id } }) { id ->
                val has = s.worldClocks.any { it.zoneId == id }
                val next = if (has) s.worldClocks.filterNot { it.zoneId == id } else s.worldClocks + ClockZoneSetting(id, id.substringAfterLast('/').replace('_', ' '))
                onChange(s.copy(worldClocks = next))
            }
        }

        SectionCard(stringResource(R.string.sec_alarm), stringResource(R.string.sec_alarm_sum, alarmName(s.alarmPackage))) {
            Text(stringResource(R.string.alarm_hint), style = MaterialTheme.typography.bodySmall)
            ChipRow(
                listOf("" to stringResource(R.string.alarm_any)) + AlarmSource.KNOWN_PACKAGES.map { it to alarmName(it) },
                { it == s.alarmPackage }, { onChange(s.copy(alarmPackage = it)) },
            )
            SliderRow(stringResource(R.string.alarm_within), s.alarmShowWithinHours, 0..48, if (s.alarmShowWithinHours == 0) stringResource(R.string.always) else "${s.alarmShowWithinHours} h") { onChange(s.copy(alarmShowWithinHours = it)) }
        }

        CalendarSection(s, onChange)

        SectionCard(stringResource(R.string.sec_weather_opts), stringResource(R.string.sec_weather_opts_sum)) {
            SwitchRow(stringResource(R.string.fahrenheit), s.fahrenheit) { onChange(s.copy(fahrenheit = it)) }
            SliderRow(stringResource(R.string.rain_threshold), s.rainLikelyFromPct, 10..90, "${s.rainLikelyFromPct}%") { onChange(s.copy(rainLikelyFromPct = it)) }
            SwitchRow(stringResource(R.string.hebrew_sunset), s.rollHebrewAtSunset) { onChange(s.copy(rollHebrewAtSunset = it)) }
            SwitchRow(stringResource(R.string.in_israel), s.inIsrael) { onChange(s.copy(inIsrael = it)) }
        }

        PermissionsSection()
        UpdatesSection(hebrew)

        val version = remember { runCatching { ctx.packageManager.getPackageInfo(ctx.packageName, 0).versionName }.getOrNull() ?: "" }
        SectionCard(stringResource(R.string.sec_privacy), stringResource(R.string.sec_privacy_sum, version)) {
            Text(stringResource(R.string.privacy_body), style = MaterialTheme.typography.bodyMedium)
        }
        SectionCard(stringResource(R.string.sec_whats_new), stringResource(R.string.sec_whats_new_sum, version)) {
            Text(stringResource(R.string.whats_new_body), style = MaterialTheme.typography.bodyMedium)
        }
    }
}

private fun styleName(c: ClockStyle): Int = when (c) {
    ClockStyle.FLIP -> R.string.style_flip
    ClockStyle.DIGITAL -> R.string.style_digital
    ClockStyle.ANALOG -> R.string.style_analog
    ClockStyle.MINIMAL -> R.string.style_minimal
    ClockStyle.POCKET -> R.string.style_pocket
}

@Composable
private fun alarmName(pkg: String): String = when (pkg) {
    "" -> stringResource(R.string.alarm_any)
    "com.smartring.app" -> stringResource(R.string.alarm_smartring)
    "com.google.android.deskclock" -> stringResource(R.string.alarm_google)
    "com.sec.android.app.clockpackage" -> stringResource(R.string.alarm_samsung)
    else -> pkg
}

@Composable
private fun PlacesSection(s: WidgetSettings, onChange: (WidgetSettings) -> Unit) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    var query by remember { mutableStateOf("") }
    var searching by remember { mutableStateOf(false) }
    val results = remember { mutableStateListOf<OpenMeteo.GeoHit>() }
    val myLocation = stringResource(R.string.my_location)
    val language = androidx.compose.ui.platform.LocalConfiguration.current.locales[0].language
    val permission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { ok ->
        if (ok && s.places.size < WidgetSettings.MAX_PLACES && s.places.none { it.isCurrent }) {
            val p = PlaceSetting("current", myLocation, 0.0, 0.0, isCurrent = true)
            onChange(s.copy(places = s.places + p, activePlaceId = s.activePlaceId ?: p.id))
        }
    }
    SectionCard(stringResource(R.string.sec_places), stringResource(R.string.sec_places_sum, s.places.size, WidgetSettings.MAX_PLACES), startOpen = s.places.isEmpty()) {
        s.places.forEach { p ->
            Row(Modifier.fillMaxWidth().heightIn(min = 48.dp), verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = p.id == s.activePlaceId, onClick = { onChange(s.copy(activePlaceId = p.id)) })
                Text(p.name, Modifier.weight(1f).clickable { onChange(s.copy(activePlaceId = p.id)) })
                IconButton(onClick = { onChange(s.copy(places = s.places.filterNot { it.id == p.id })) }, modifier = Modifier.semantics { contentDescription = ctx.getString(R.string.remove) }) {
                    Icon(Icons.Filled.Close, contentDescription = null)
                }
            }
        }
        if (s.places.size < WidgetSettings.MAX_PLACES) {
            OutlinedTextField(query, { query = it }, label = { Text(stringResource(R.string.search_place)) }, singleLine = true, modifier = Modifier.fillMaxWidth())
            Button(
                onClick = {
                    searching = true
                    scope.launch {
                        val hits = withContext(Dispatchers.IO) { WeatherRepository.search(query, language) }
                        results.clear(); results.addAll(hits); searching = false
                    }
                },
                enabled = query.trim().length >= 2 && !searching, modifier = Modifier.heightIn(min = 48.dp),
            ) { Text(stringResource(R.string.search)) }
            results.forEach { h ->
                val label = listOfNotNull(h.name, h.admin, h.country).joinToString(", ")
                Text(label, Modifier.fillMaxWidth().heightIn(min = 48.dp).clickable {
                    val id = "p" + (h.lat * 1000).toInt() + "_" + (h.lon * 1000).toInt()
                    onChange(s.copy(places = s.places + PlaceSetting(id, h.name, h.lat, h.lon), activePlaceId = s.activePlaceId ?: id))
                    results.clear(); query = ""
                }.padding(vertical = 12.dp), style = MaterialTheme.typography.bodyLarge)
            }
            if (s.places.none { it.isCurrent }) OutlinedButton(
                onClick = { if (LocationSource.granted(ctx)) permission.launch(Manifest.permission.ACCESS_COARSE_LOCATION) else permission.launch(Manifest.permission.ACCESS_COARSE_LOCATION) },
                modifier = Modifier.heightIn(min = 48.dp),
            ) { Text(stringResource(R.string.use_my_location)) }
        }
    }
}

@Composable
private fun CalendarSection(s: WidgetSettings, onChange: (WidgetSettings) -> Unit) {
    val ctx = LocalContext.current
    var granted by remember { mutableStateOf(CalendarSource.granted(ctx)) }
    val permission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted = it; if (it) com.clockweather.app.data.CalendarChangeWorker.schedule(ctx) }
    val calendars = remember(granted) { CalendarSource.calendars(ctx) }
    SectionCard(stringResource(R.string.sec_calendar), if (granted) stringResource(R.string.sec_calendar_sum_on, calendars.size) else stringResource(R.string.sec_calendar_sum_off)) {
        Text(stringResource(R.string.calendar_hint), style = MaterialTheme.typography.bodySmall)
        if (!granted) {
            androidx.compose.foundation.Image(androidx.compose.ui.res.painterResource(R.drawable.spot_calendar), contentDescription = null, modifier = Modifier.heightIn(max = 110.dp))
            Button(onClick = { permission.launch(Manifest.permission.READ_CALENDAR) }, modifier = Modifier.heightIn(min = 48.dp)) { Text(stringResource(R.string.allow_calendar)) }
        } else {
            calendars.forEach { cal ->
                val checked = s.agendaCalendarIds.isEmpty() || cal.id in s.agendaCalendarIds
                Row(Modifier.fillMaxWidth().heightIn(min = 48.dp).clickable {
                    val all = calendars.map { it.id }
                    val cur = if (s.agendaCalendarIds.isEmpty()) all else s.agendaCalendarIds
                    val next = if (checked) cur - cal.id else cur + cal.id
                    onChange(s.copy(agendaCalendarIds = if (next.size == all.size) emptyList() else next))
                }, verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = checked, onCheckedChange = null)
                    Column(Modifier.padding(start = 8.dp)) {
                        Text(cal.name, style = MaterialTheme.typography.bodyLarge)
                        Text(cal.account, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            SwitchRow(stringResource(R.string.include_all_day), s.agendaIncludeAllDay) { onChange(s.copy(agendaIncludeAllDay = it)) }
            SliderRow(stringResource(R.string.rotate_every), s.agendaRotateSeconds, 3..30, "${s.agendaRotateSeconds} s") { onChange(s.copy(agendaRotateSeconds = it)) }
        }
    }
}
