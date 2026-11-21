package com.clockweather.app.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.clockweather.app.R
import com.clockweather.app.widget.ClockWidgetProvider
import com.clockweather.core.widget.ClockStyle
import com.clockweather.core.widget.Palettes
import com.clockweather.core.widget.WidgetOverride
import com.clockweather.core.widget.WidgetSettings

/**
 * Settings -> Widgets on my home screen: every widget that is placed, each with its OWN look and "what to show" on top of the global
 * settings. Anything left alone keeps following the global settings; "Use global settings" removes the widget's own choices.
 */
@Composable
fun WidgetsSection(s: WidgetSettings, hebrew: Boolean, onChange: (WidgetSettings) -> Unit) {
    val ctx = LocalContext.current
    val placed = remember(s) { ClockWidgetProvider.placed(ctx) }
    SectionCard(stringResource(R.string.sec_widgets), stringResource(R.string.sec_widgets_sum, placed.size)) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(stringResource(R.string.widgets_hint), style = MaterialTheme.typography.bodySmall)
            if (placed.isEmpty()) Text(stringResource(R.string.no_widgets_placed), style = MaterialTheme.typography.bodyMedium)
            placed.forEachIndexed { i, (id, kind) ->
                val key = id.toString()
                val own = s.perWidget[key] ?: WidgetOverride()
                var open by rememberSaveable(id) { mutableStateOf(false) }
                Row(Modifier.fillMaxWidth().heightIn(min = 48.dp).clickable { open = !open }, verticalAlignment = Alignment.CenterVertically) {
                    Text("${i + 1}. ${if (hebrew) kind.he else kind.en}" + if (own.isEmpty) "" else "  •  " + stringResource(R.string.customised), style = MaterialTheme.typography.titleSmall)
                }
                if (open) WidgetEditor(s, id, own, hebrew, onChange)
            }
        }
    }
}

@Composable
private fun WidgetEditor(s: WidgetSettings, id: Int, own: WidgetOverride, hebrew: Boolean, onChange: (WidgetSettings) -> Unit) {
    val key = id.toString()
    val eff = own.applyTo(s)
    fun set(o: WidgetOverride) = onChange(s.copy(perWidget = if (o.isEmpty) s.perWidget - key else s.perWidget + (key to o)))
    Column(Modifier.padding(start = 8.dp, bottom = 12.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(stringResource(R.string.clock_style), style = MaterialTheme.typography.titleSmall)
        ChipRow(ClockStyle.values().map { it to stringResource(styleName(it)) }, { it == eff.clockStyle }, { set(own.copy(clockStyle = it)) })
        Text(stringResource(R.string.palette), style = MaterialTheme.typography.titleSmall)
        ChipRow(Palettes.ALL.map { it.id to (if (hebrew) it.he else it.en) }, { it == eff.paletteId }, { set(own.copy(paletteId = it)) })
        SliderRow(stringResource(R.string.bg_transparency), 100 - eff.backgroundAlphaPct, 0..100, "${100 - eff.backgroundAlphaPct}%") { set(own.copy(backgroundAlphaPct = 100 - it)) }
        SwitchRow(stringResource(R.string.show_clock), eff.showClock) { set(own.copy(showClock = it)) }
        SwitchRow(stringResource(R.string.show_weather), eff.showWeather) { set(own.copy(showWeather = it)) }
        SwitchRow(stringResource(R.string.show_gregorian), eff.showGregorianDate) { set(own.copy(showGregorianDate = it)) }
        SwitchRow(stringResource(R.string.show_hebrew), eff.showHebrewDate) { set(own.copy(showHebrewDate = it)) }
        SwitchRow(stringResource(R.string.show_holidays), eff.showHolidays) { set(own.copy(showHolidays = it)) }
        SwitchRow(stringResource(R.string.show_alarm), eff.showAlarm) { set(own.copy(showAlarm = it)) }
        SwitchRow(stringResource(R.string.show_agenda), eff.showAgenda) { set(own.copy(showAgenda = it)) }
        SwitchRow(stringResource(R.string.show_world), eff.showWorldClocks) { set(own.copy(showWorldClocks = it)) }
        SwitchRow(stringResource(R.string.show_sun), eff.showSunTimes) { set(own.copy(showSunTimes = it)) }
        SwitchRow(stringResource(R.string.show_timers), eff.showTimers) { set(own.copy(showTimers = it)) }
        SwitchRow(stringResource(R.string.show_settings_button), eff.showSettingsButton) { set(own.copy(showSettingsButton = it)) }
        OutlinedButton(onClick = { set(WidgetOverride()) }, enabled = !own.isEmpty, modifier = Modifier.heightIn(min = 48.dp)) { Text(stringResource(R.string.use_global)) }
    }
}
