package com.clockweather.app.ui

import android.graphics.RectF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.clockweather.app.R
import com.clockweather.app.data.WeatherRepository
import com.clockweather.app.data.WeatherWorker
import com.clockweather.app.widget.Snapshot
import com.clockweather.app.widget.WeatherArt
import com.clockweather.core.weather.Sky
import com.clockweather.core.weather.TempFormat
import com.clockweather.core.weather.WeatherOutlook
import com.clockweather.core.widget.WidgetSettings
import java.time.format.DateTimeFormatter
import java.util.Locale

/** Drill-down from the widget's weather panel: current conditions, max/min, rain outlook and the hour-by-hour list for 3 days. */
@Composable
fun WeatherScreen(s: WidgetSettings, onBack: () -> Unit) {
    val ctx = LocalContext.current
    val hebrew = Snapshot.isHebrew(ctx, s.language)
    val forecast = remember(s.activePlaceId) { s.activePlaceId?.let { WeatherRepository.cached(ctx, it) } }
    val place = s.places.firstOrNull { it.id == s.activePlaceId }?.name ?: ""
    var dayIndex by remember { mutableIntStateOf(0) }
    Column {
        ScreenHeader(stringResource(R.string.weather_details), stringResource(R.string.back), onBack) {
            IconButton(onClick = { WeatherWorker.refreshNow(ctx) }) { Icon(Icons.Filled.Refresh, contentDescription = stringResource(R.string.refresh)) }
        }
        if (forecast == null || forecast.days.isEmpty()) {
            Text(stringResource(R.string.no_weather_yet), Modifier.padding(16.dp), style = MaterialTheme.typography.bodyLarge)
            return@Column
        }
        val day = forecast.days[dayIndex.coerceIn(0, forecast.days.lastIndex)]
        ChipRow(forecast.days.mapIndexed { i, d -> i to d.date.format(DateTimeFormatter.ofPattern("EEE d/M", if (hebrew) Locale("he") else Locale.ENGLISH)) }, { it == dayIndex }, { dayIndex = it })
        Card(Modifier.fillMaxWidth().padding(12.dp)) {
            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                SkyIcon(forecast.current?.sky?.takeIf { dayIndex == 0 } ?: day.sky, forecast.current?.isDay ?: true, animate = s.animateWeather && !s.calmAnimations)
                Column(Modifier.padding(start = 16.dp)) {
                    Text(place, style = MaterialTheme.typography.titleMedium)
                    val now = forecast.current
                    if (dayIndex == 0 && now != null) Text(TempFormat.short(now.tempC, s.fahrenheit), style = MaterialTheme.typography.displaySmall)
                    Text("${stringResource(R.string.max_min)}: ${TempFormat.short(day.maxC, s.fahrenheit)} / ${TempFormat.short(day.minC, s.fahrenheit)}", style = MaterialTheme.typography.bodyLarge)
                    Text(WeatherOutlook.text(WeatherOutlook.of(day, s.rainLikelyFromPct), day.rainChancePct, hebrew), style = MaterialTheme.typography.bodyMedium)
                    val rise = day.sunrise?.toLocalTime(); val set = day.sunset?.toLocalTime()
                    if (rise != null && set != null) Text("☀↑ $rise   ☀↓ $set", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
        val hours = forecast.hoursOf(day.date)
        LazyColumn(Modifier.fillMaxWidth().weight(1f)) {
            items(hours) { h ->
                Row(Modifier.fillMaxWidth().heightIn(min = 52.dp).padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("%02d:00".format(h.time.hour), Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
                    SkyIcon(h.sky, h.isDay, 32)
                    Text(TempFormat.short(h.tempC, s.fahrenheit), Modifier.weight(1f).padding(start = 12.dp), style = MaterialTheme.typography.titleMedium)
                    Text("☂ ${h.rainChancePct ?: 0}%", Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                    Text("💧 ${h.humidityPct ?: "-"}%", Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
private fun SkyIcon(sky: Sky, isDay: Boolean, sizeDp: Int = 72, animate: Boolean = false) {
    val ctx = LocalContext.current
    var ms by remember { mutableLongStateOf(0L) }
    if (animate) LaunchedEffect(sky, isDay) { val t0 = System.currentTimeMillis(); while (true) { ms = System.currentTimeMillis() - t0; kotlinx.coroutines.delay(125) } }
    Canvas(Modifier.size(sizeDp.dp)) {
        drawIntoCanvas { WeatherArt.draw(ctx, it.nativeCanvas, sky, isDay, RectF(0f, 0f, size.width, size.height), if (animate) ms else -1L) }
    }
}
