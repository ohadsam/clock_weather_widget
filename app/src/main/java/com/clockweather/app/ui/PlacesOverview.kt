package com.clockweather.app.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
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
import com.clockweather.app.data.WeatherRepository
import com.clockweather.core.weather.TempFormat
import com.clockweather.core.widget.WidgetSettings
import java.time.Instant

/** All saved places at a glance (up to four): now, max/min, chance of rain. Tap one to open its full forecast. */
@Composable
fun PlacesOverview(s: WidgetSettings, tick: Long, onOpenPlace: (String) -> Unit) {
    if (s.places.size < 2) return
    val ctx = LocalContext.current
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(stringResource(R.string.my_places), style = MaterialTheme.typography.titleMedium)
        s.places.forEach { p ->
            val f = remember(p.id, tick) { WeatherRepository.cached(ctx, p.id) }
            val day = f?.today(Instant.now())
            Card(Modifier.fillMaxWidth().clickable(role = Role.Button) { onOpenPlace(p.id) }) {
                Row(Modifier.padding(12.dp).heightIn(min = 56.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (f != null && day != null) SkyIcon(f.current?.sky ?: day.sky, f.current?.isDay ?: true, sizeDp = 48, animate = false)
                    Column(Modifier.weight(1f).padding(start = 12.dp)) {
                        Text(p.name, style = MaterialTheme.typography.titleMedium)
                        if (day != null) Text("${TempFormat.short(day.maxC, s.fahrenheit)} / ${TempFormat.short(day.minC, s.fahrenheit)}" + (day.rainChancePct?.let { "   ☂ $it%" } ?: ""), style = MaterialTheme.typography.bodySmall)
                        else Text(stringResource(R.string.no_weather_yet), style = MaterialTheme.typography.bodySmall)
                    }
                    if (f?.current != null) Text(TempFormat.short(f.current!!.tempC, s.fahrenheit), style = MaterialTheme.typography.headlineMedium)
                }
            }
        }
    }
}
