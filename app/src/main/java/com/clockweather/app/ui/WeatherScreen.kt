package com.clockweather.app.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.clockweather.app.R
import com.clockweather.app.data.WeatherRepository
import com.clockweather.app.data.WeatherWorker
import com.clockweather.app.widget.Snapshot
import com.clockweather.core.clock.Freshness
import com.clockweather.core.weather.DayChart
import com.clockweather.core.weather.Forecast
import com.clockweather.core.weather.HourPoint
import com.clockweather.core.weather.Metric
import com.clockweather.core.weather.TempFormat
import com.clockweather.core.weather.WeatherOutlook
import com.clockweather.core.widget.PlaceSetting
import com.clockweather.core.widget.WidgetSettings
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Weather in the app: every saved place is a page (swipe, or tap a place chip), and each page shows the current weather, a day picker,
 * an animated hourly chart that can be switched between temperature, rain and humidity, and the hour-by-hour list with all three.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WeatherScreen(s: WidgetSettings, startPlaceId: String?, onBack: () -> Unit) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val places = s.places + s.worldPlaces
    Column(Modifier.fillMaxHeight()) {
        ScreenHeader(stringResource(R.string.weather_details), stringResource(R.string.back), onBack) {
            IconButton(onClick = { WeatherWorker.refreshNow(ctx) }) { Icon(Icons.Filled.Refresh, contentDescription = stringResource(R.string.refresh)) }
        }
        if (places.isEmpty()) {
            EmptyWeather()
            return@Column
        }
        val start = places.indexOfFirst { it.id == (startPlaceId ?: s.activePlaceId) }.coerceAtLeast(0)
        val pager = rememberPagerState(initialPage = start) { places.size }
        if (places.size > 1) ChipRow(places.mapIndexed { i, p -> i to p.name }, { it == pager.currentPage }, { scope.launch { pager.animateScrollToPage(it) } })
        HorizontalPager(state = pager, modifier = Modifier.weight(1f).fillMaxWidth(), key = { places[it].id }) { page -> PlaceForecast(places[page], s) }
    }
}

@Composable
private fun EmptyWeather() {
    Column(Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Image(painterResource(R.drawable.spot_offline), contentDescription = null, modifier = Modifier.height(150.dp))
        Text(stringResource(R.string.no_weather_yet), style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun PlaceForecast(place: PlaceSetting, s: WidgetSettings) {
    val ctx = LocalContext.current
    val hebrew = Snapshot.isHebrew(ctx, s.language)
    val forecast = remember(place.id) { WeatherRepository.cached(ctx, place.id) }
    if (forecast == null || forecast.days.isEmpty()) { EmptyWeather(); return }
    var dayIndex by rememberSaveable(place.id) { mutableIntStateOf(0) }
    var metricName by rememberSaveable(place.id) { mutableStateOf(Metric.TEMP.name) }
    val metric = Metric.valueOf(metricName)
    val day = forecast.days[dayIndex.coerceIn(0, forecast.days.lastIndex)]
    val hours = forecast.hoursOf(day.date)
    val now = remember { LocalDateTime.now(forecast.zone) }
    val series = remember(place.id, dayIndex, metric, s.fahrenheit) { DayChart.series(hours, metric, s.fahrenheit) }
    val dayFmt = DateTimeFormatter.ofPattern("EEE d/M", if (hebrew) Locale("he") else Locale.ENGLISH)
    val metricColor = when (metric) { Metric.TEMP -> Color(0xFFFF9800); Metric.RAIN -> Color(0xFF4FA3E0); Metric.HUMIDITY -> Color(0xFF26A69A); Metric.UV -> Color(0xFF8E5BD6); Metric.WIND -> Color(0xFF607D8B) }

    LazyColumn(Modifier.fillMaxWidth(), contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 24.dp)) {
        item { SummaryCard(place, forecast, dayIndex, s, hebrew) }
        item { ChipRow(forecast.days.mapIndexed { i, d -> i to d.date.format(dayFmt) }, { it == dayIndex }, { dayIndex = it }) }
        item {
            ChipRow(
                listOf(Metric.TEMP to R.string.metric_temp, Metric.RAIN to R.string.metric_rain, Metric.HUMIDITY to R.string.metric_humidity, Metric.UV to R.string.metric_uv, Metric.WIND to R.string.metric_wind).map { it.first.name to stringResource(it.second) },
                { it == metricName }, { metricName = it },
            )
        }
        item {
            Card(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp)) {
                if (hours.isEmpty()) Text(stringResource(R.string.no_hours), Modifier.padding(16.dp))
                else HourChart(series, DayChart.nowIndex(hours, now), metricColor, animKey = Triple(place.id, dayIndex, metric))
            }
        }
        if (forecast.fetchedAtEpochSec > 0) item {
            Text(
                stringResource(R.string.updated_ago, Freshness.label(Freshness.ageMinutes(forecast.fetchedAtEpochSec, System.currentTimeMillis() / 1000), hebrew)),
                Modifier.padding(horizontal = 16.dp), style = MaterialTheme.typography.bodySmall,
            )
        }
        items(hours) { h -> HourRow(h, s.fahrenheit) }
    }
}

@Composable
private fun SummaryCard(place: PlaceSetting, f: Forecast, dayIndex: Int, s: WidgetSettings, hebrew: Boolean) {
    val day = f.days[dayIndex.coerceIn(0, f.days.lastIndex)]
    val cur = f.current?.takeIf { dayIndex == 0 }
    Card(Modifier.fillMaxWidth().padding(12.dp)) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            SkyIcon(cur?.sky ?: day.sky, cur?.isDay ?: true, sizeDp = 88, animate = s.animateWeather && !s.calmAnimations)
            Column(Modifier.padding(start = 16.dp)) {
                Text(place.name, style = MaterialTheme.typography.titleMedium)
                if (cur != null) Text(TempFormat.short(cur.tempC, s.fahrenheit), style = MaterialTheme.typography.displaySmall)
                Text("${stringResource(R.string.max_min)}: ${TempFormat.short(day.maxC, s.fahrenheit)} / ${TempFormat.short(day.minC, s.fahrenheit)}", style = MaterialTheme.typography.bodyLarge)
                Text(WeatherOutlook.text(WeatherOutlook.of(day, s.rainLikelyFromPct), day.rainChancePct, hebrew), style = MaterialTheme.typography.bodyMedium)
                val rise = day.sunrise?.toLocalTime(); val set = day.sunset?.toLocalTime()
                if (rise != null && set != null) Text("☀↑ $rise   ☀↓ $set", style = MaterialTheme.typography.bodySmall)
                cur?.humidityPct?.let { Text("💧 $it%", style = MaterialTheme.typography.bodySmall) }
                cur?.feelsLikeC?.let { Text("${stringResource(R.string.feels_like)} ${TempFormat.short(it, s.fahrenheit)} · ${com.clockweather.core.weather.Comfort.word(it, hebrew)}", style = MaterialTheme.typography.bodyMedium) }
                f.hoursOf(day.date).mapNotNull { it.uvIndex }.maxOrNull()?.let { Text("UV ${Math.round(it)} · ${com.clockweather.core.weather.Comfort.uvWord(it, hebrew)}", style = MaterialTheme.typography.bodySmall) }
                val ctx = androidx.compose.ui.platform.LocalContext.current
                androidx.compose.material3.TextButton(onClick = { com.clockweather.app.data.SourceApps.openUrl(ctx, "https://www.windy.com/?rain,${place.lat},${place.lon},8") }, modifier = Modifier.heightIn(min = 48.dp)) { Text(stringResource(R.string.rain_radar)) }
            }
        }
    }
}

@Composable
private fun HourRow(h: HourPoint, fahrenheit: Boolean) {
    Row(Modifier.fillMaxWidth().heightIn(min = 52.dp).padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
        Text("%02d:00".format(h.time.hour), Modifier.width(56.dp), style = MaterialTheme.typography.bodyLarge)
        SkyIcon(h.sky, h.isDay, sizeDp = 32)
        Text(TempFormat.short(h.tempC, fahrenheit), Modifier.width(52.dp).padding(start = 8.dp), style = MaterialTheme.typography.titleMedium)
        MiniBar(Modifier.weight(1f).padding(horizontal = 6.dp), "☂", h.rainChancePct, Color(0xFF4FA3E0))
        MiniBar(Modifier.weight(1f), "💧", h.humidityPct, Color(0xFF26A69A))
    }
}

@Composable
private fun MiniBar(modifier: Modifier, icon: String, pct: Int?, color: Color) {
    Column(modifier) {
        Text("$icon ${pct ?: "–"}%", style = MaterialTheme.typography.bodySmall)
        Box(Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)).background(color.copy(alpha = 0.18f))) {
            Box(Modifier.fillMaxWidth(((pct ?: 0) / 100f).coerceIn(0f, 1f)).fillMaxHeight().clip(RoundedCornerShape(3.dp)).background(color))
        }
    }
}
