package com.clockweather.app.ui

import android.location.Geocoder
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.clockweather.app.R
import com.clockweather.app.data.Net
import com.clockweather.app.data.WeatherRepository
import com.clockweather.app.widget.Snapshot
import com.clockweather.core.weather.Forecast
import com.clockweather.core.weather.OpenMeteo
import com.clockweather.core.weather.PlaceIds
import com.clockweather.core.weather.TempFormat
import com.clockweather.core.widget.PlaceSetting
import com.clockweather.core.widget.WidgetSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.format.DateTimeFormatter
import java.util.Locale

private data class Candidate(val lat: Double, val lon: Double, val name: String, val forecast: Forecast? = null)

/**
 * Pick a place by searching a name or by tapping the map, see its weather and local time, then save it as one of "my places"
 * (used by the widget) or as a "place around the world" (dashboard only). The map is loaded only after the user presses "Show map".
 */
@Composable
fun PlacePickerScreen(s: WidgetSettings, onChange: (WidgetSettings) -> Unit, onBack: () -> Unit) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val hebrew = Snapshot.isHebrew(ctx, s.language)
    val language = if (hebrew) "he" else "en"
    var showMap by rememberSaveable { mutableStateOf(false) }
    var query by rememberSaveable { mutableStateOf("") }
    var searching by remember { mutableStateOf(false) }
    var candidate by remember { mutableStateOf<Candidate?>(null) }
    var focus by remember { mutableStateOf<MapPin?>(null) }
    var added by remember { mutableStateOf(false) }
    val results = remember { mutableStateListOf<OpenMeteo.GeoHit>() }

    fun pick(lat: Double, lon: Double, knownName: String?) {
        added = false
        val fallback = knownName ?: PlaceIds.coordLabel(lat, lon)
        candidate = Candidate(lat, lon, fallback)
        scope.launch {
            val name = knownName ?: withContext(Dispatchers.IO) { reverseName(ctx, lat, lon, hebrew) } ?: fallback
            val forecast = withContext(Dispatchers.IO) { Net.get(OpenMeteo.forecastUrl(lat, lon))?.let { OpenMeteo.parseForecast("cand", it, System.currentTimeMillis() / 1000) } }
            if (candidate?.lat == lat && candidate?.lon == lon) candidate = Candidate(lat, lon, name, forecast)
        }
    }

    Column(Modifier.fillMaxSize()) {
        ScreenHeader(stringResource(R.string.map_title), stringResource(R.string.back), onBack)
        Column(Modifier.padding(horizontal = 12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(query, { query = it }, label = { Text(stringResource(R.string.search_place)) }, singleLine = true, modifier = Modifier.weight(1f))
                Button(
                    onClick = {
                        searching = true
                        scope.launch {
                            val hits = withContext(Dispatchers.IO) { WeatherRepository.search(query, language) }
                            results.clear(); results.addAll(hits); searching = false
                        }
                    },
                    enabled = query.trim().length >= 2 && !searching, modifier = Modifier.heightIn(min = 52.dp),
                ) { Text(stringResource(R.string.search)) }
            }
            if (results.isNotEmpty()) Column(Modifier.heightIn(max = 180.dp).verticalScroll(rememberScrollState())) {
                results.forEach { h ->
                    Text(listOfNotNull(h.name, h.admin, h.country).joinToString(", "), Modifier.fillMaxWidth().heightIn(min = 48.dp).clickable {
                        pick(h.lat, h.lon, h.name); focus = MapPin(h.lat, h.lon, h.name); results.clear(); query = ""
                    }.padding(vertical = 12.dp), style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
        Box(Modifier.weight(1f).fillMaxWidth().padding(8.dp)) {
            if (showMap) OsmMap(
                pins = (s.places + s.worldPlaces).map { MapPin(it.lat, it.lon, it.name) },
                candidate = candidate?.let { MapPin(it.lat, it.lon, it.name) }, focus = focus,
                onTap = { la, lo -> pick(la, lo, null) }, modifier = Modifier.fillMaxSize(),
            ) else Column(Modifier.align(Alignment.Center).padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(stringResource(R.string.map_privacy_hint), style = MaterialTheme.typography.bodyMedium)
                Button(onClick = { showMap = true }, modifier = Modifier.heightIn(min = 52.dp)) { Text(stringResource(R.string.show_map)) }
            }
        }
        val c = candidate
        Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            if (c == null) Text(stringResource(if (showMap) R.string.map_tap_hint else R.string.map_search_hint), style = MaterialTheme.typography.bodyMedium)
            else {
                Text(c.name, style = MaterialTheme.typography.titleMedium)
                val f = c.forecast
                if (f != null) {
                    val tf = DateTimeFormatter.ofPattern(if (s.use24h) "HH:mm" else "h:mm a", Locale.ENGLISH)
                    Text(
                        stringResource(R.string.local_time, java.time.Instant.now().atZone(f.zone).format(tf)) + (f.current?.let { "   ${TempFormat.short(it.tempC, s.fahrenheit)}" } ?: ""),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                } else Text(stringResource(R.string.loading), style = MaterialTheme.typography.bodySmall)
                val id = PlaceIds.make(c.lat, c.lon)
                val have = (s.places + s.worldPlaces).any { it.id == id }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { onChange(s.copy(places = s.places + PlaceSetting(id, c.name, c.lat, c.lon), activePlaceId = s.activePlaceId ?: id)); added = true },
                        enabled = !have && s.places.size < WidgetSettings.MAX_PLACES, modifier = Modifier.weight(1f).heightIn(min = 48.dp),
                    ) { Text(stringResource(R.string.add_as_my_place)) }
                    OutlinedButton(
                        onClick = { onChange(s.copy(worldPlaces = s.worldPlaces + PlaceSetting(id, c.name, c.lat, c.lon))); added = true },
                        enabled = !have && s.worldPlaces.size < WidgetSettings.MAX_WORLD_PLACES, modifier = Modifier.weight(1f).heightIn(min = 48.dp),
                    ) { Text(stringResource(R.string.add_to_world)) }
                }
                if (added || have) Text(stringResource(R.string.place_saved), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
            }
        }
    }
}

/** A readable name for a tapped point from the phone's geocoder (null when the phone has none or it fails; the caller shows the coordinates). */
@Suppress("DEPRECATION")
private fun reverseName(ctx: android.content.Context, lat: Double, lon: Double, hebrew: Boolean): String? = runCatching {
    if (!Geocoder.isPresent()) return null
    val a = Geocoder(ctx, if (hebrew) Locale("he") else Locale.ENGLISH).getFromLocation(lat, lon, 1)?.firstOrNull() ?: return null
    listOfNotNull(a.locality ?: a.subAdminArea ?: a.adminArea, a.countryName).distinct().joinToString(", ").ifEmpty { null }
}.getOrNull()
