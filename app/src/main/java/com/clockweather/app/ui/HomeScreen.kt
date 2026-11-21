package com.clockweather.app.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.clockweather.app.R
import com.clockweather.app.data.CalendarSource
import com.clockweather.app.data.StepSource
import com.clockweather.app.widget.Snapshot
import com.clockweather.core.auto.AutoDashboard
import com.clockweather.core.auto.Facts
import com.clockweather.core.dashboard.DashTile
import com.clockweather.core.dashboard.Dashboard
import com.clockweather.core.widget.WidgetSettings
import kotlinx.coroutines.delay

/**
 * Home = the dashboard: the cards the owner chose (Settings -> Dashboard), in the owner's order. Cards slide in, tap opens a drill-down,
 * and most have a button to the app the data comes from. Refreshes every 15 s while open.
 */
@Composable
fun HomeScreen(
    settings: WidgetSettings, onSettings: () -> Unit, onOpenWeather: (String?) -> Unit, onOpenDetail: (DashTile) -> Unit, onOpenMap: () -> Unit, onOpenBedside: () -> Unit,
    onChange: (WidgetSettings) -> Unit,
) {
    val ctx = LocalContext.current
    var tick by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) { while (true) { delay(15_000); tick = System.currentTimeMillis() } }
    val st = remember(settings, tick) { Snapshot.build(ctx, forDashboard = true) }
    val animate = !settings.calmAnimations
    val chosen = Dashboard.visibleTiles(settings.dashboardOrder, settings.dashboardHidden)
    val tiles = if (settings.autoContent) {
        AutoDashboard.arrange(chosen, settings, Facts(st.now, st.zone, st.agenda, st.nextAlarm, st.forecast, st.steps?.takeIf { settings.showSteps && StepSource.granted(ctx) }, settings.places.size, settings.worldClocks.size, settings.worldPlaces.size))
    } else chosen
    Column(Modifier.verticalScroll(rememberScrollState()).padding(bottom = 24.dp)) {
        ScreenHeader(stringResource(R.string.app_name), stringResource(R.string.back), null, brand = true) {
            val localNow = st.now.atZone(st.zone)
            val seasonDay = com.clockweather.core.widget.Season.effectiveDay(localNow.toLocalDate(), localNow.toLocalTime(), st.forecast?.today(st.now)?.sunset?.toLocalTime())
            val season = if (settings.seasonalTheme) com.clockweather.core.widget.Season.of(seasonDay, settings.inIsrael) else null
            val key = if (season != null) com.clockweather.core.widget.Season.ornamentKey(seasonDay, settings.inIsrael) else null
            // Hanukkah: the picture with that day's candles when it exists, else the general one
            val orn = key?.let { k -> ctx.resources.getIdentifier("orn_$k", "drawable", ctx.packageName).takeIf { it != 0 } ?: ctx.resources.getIdentifier("orn_" + season!!.ornament, "drawable", ctx.packageName) } ?: 0
            if (orn != 0) Image(painterResource(orn), contentDescription = if (st.hebrew) season!!.he else season!!.en, modifier = Modifier.height(40.dp))
            IconButton(onClick = onSettings) { Icon(Icons.Filled.Settings, contentDescription = stringResource(R.string.settings)) }
        }
        Column(Modifier.padding(horizontal = 12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            val wide = androidx.compose.ui.platform.LocalConfiguration.current.screenWidthDp >= 720
            val render: @Composable (DashTile, Int) -> Unit = { tile, i ->
                when (tile) {
                    DashTile.WIDGET -> WidgetTile(settings, tick, onChange)
                    DashTile.DATE -> DateTile(st, i, animate) { onOpenDetail(DashTile.DATE) }
                    DashTile.WEATHER -> WeatherTile(st, i, animate) { onOpenWeather(null) }
                    DashTile.PLACES -> PlacesOverview(settings, tick) { onOpenWeather(it) }
                    DashTile.ALARM -> AlarmTile(st, i, animate) { onOpenDetail(DashTile.ALARM) }
                    DashTile.AGENDA -> AgendaTile(st, i, animate, CalendarSource.granted(ctx)) { onOpenDetail(DashTile.AGENDA) }
                    DashTile.SUN -> SunTile(st, i, animate) { onOpenDetail(DashTile.SUN) }
                    DashTile.STEPS -> StepsTile(st, i, animate, settings.showSteps && StepSource.granted(ctx))
                    DashTile.STOPWATCH -> StopwatchTile(st, i, animate)
                    DashTile.COUNTDOWN -> CountdownTile(st, i, animate)
                    DashTile.DATE_COUNTDOWN -> DateCountdownTile(st, i, animate)
                    DashTile.SPECIAL_EVENTS -> SpecialEventsTile(st, i, animate)
                    DashTile.WORLD -> WorldTile(st, i, animate)
                    DashTile.WORLD_PLACES -> WorldPlacesTile(st, i, animate, onOpenWeather = { onOpenWeather(it) }, onAdd = onOpenMap)
                    DashTile.MAP -> MapTile(st, i, animate, onOpenMap)
                    DashTile.BEDSIDE -> BedsideTile(st, i, animate, onOpenBedside)
                }
            }
            // tablets / landscape: two columns
            if (wide) Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) { tiles.forEachIndexed { i, tl -> if (i % 2 == 0) render(tl, i) } }
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) { tiles.forEachIndexed { i, tl -> if (i % 2 == 1) render(tl, i) } }
            } else tiles.forEachIndexed { i, tl -> render(tl, i) }
            if (settings.places.isEmpty()) {
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Image(painterResource(R.drawable.spot_location), contentDescription = null, modifier = Modifier.height(140.dp))
                    Text(stringResource(R.string.no_place_hint), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
                    Button(onClick = onSettings, modifier = Modifier.heightIn(min = 48.dp)) { Text(stringResource(R.string.add_place_cta)) }
                }
            }
        }
    }
}
