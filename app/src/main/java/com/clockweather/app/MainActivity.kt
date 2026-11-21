package com.clockweather.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.clockweather.app.data.SettingsStore
import com.clockweather.app.data.WeatherWorker
import com.clockweather.app.ui.BedsideScreen
import com.clockweather.app.ui.ClockWeatherTheme
import com.clockweather.app.ui.DashDetailScreen
import com.clockweather.app.ui.HomeScreen
import com.clockweather.app.ui.PlacePickerScreen
import com.clockweather.app.ui.SettingsScreen
import com.clockweather.app.ui.WeatherScreen
import com.clockweather.app.widget.ClockWidgetProvider
import com.clockweather.app.widget.Snapshot
import com.clockweather.core.dashboard.DashTile
import com.clockweather.core.widget.WidgetSettings

/** The app: Home (live widget preview) -> Settings / Weather details. One activity, simple state-based navigation. */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val start = intent.getStringExtra(EXTRA_SCREEN) ?: "home"
        setContent { ClockWeatherTheme { AppRoot(start) } }
    }

    companion object { const val EXTRA_SCREEN = "screen" }
}

@Composable
private fun AppRoot(start: String) {
    val ctx = LocalContext.current
    var settings by remember { mutableStateOf(SettingsStore.load(ctx)) }
    val startTile = if (start.startsWith("detail:")) DashTile.byId(start.removePrefix("detail:")) else null
    var screen by remember { mutableStateOf(if (startTile != null) "detail" else if (start in setOf("settings", "weather")) start else "home") }
    var detail by remember { mutableStateOf(startTile) }
    var weatherPlace by remember { mutableStateOf<String?>(null) }

    fun update(new: WidgetSettings) {
        val s = new.sanitized()
        val old = settings
        settings = s
        SettingsStore.save(ctx, s)
        Snapshot.invalidateAgenda()
        ClockWidgetProvider.updateAll(ctx)
        if (s.places != old.places || s.worldPlaces != old.worldPlaces || s.activePlaceId != old.activePlaceId) WeatherWorker.refreshNow(ctx)
    }

    var showGuide by remember { mutableStateOf(!com.clockweather.app.ui.GuidePrefs.seen(ctx)) }
    BackHandler(enabled = screen != "home") { screen = "home" }
    if (showGuide) com.clockweather.app.ui.GuideDialog { com.clockweather.app.ui.GuidePrefs.setSeen(ctx); showGuide = false }
    Box(Modifier.fillMaxSize().statusBarsPadding()) {
        when (screen) {
            "settings" -> SettingsScreen(settings, ::update) { screen = "home" }
            "bedside" -> BedsideScreen(settings) { screen = "home" }
            "map" -> PlacePickerScreen(settings, ::update) { screen = "home" }
            "weather" -> WeatherScreen(settings, weatherPlace) { screen = "home" }
            "detail" -> detail?.let { DashDetailScreen(it, settings) { screen = "home" } } ?: run { screen = "home" }
            else -> HomeScreen(
                settings, onSettings = { screen = "settings" }, onOpenWeather = { weatherPlace = it; screen = "weather" },
                onOpenDetail = { detail = it; screen = "detail" }, onOpenMap = { screen = "map" }, onOpenBedside = { screen = "bedside" }, onChange = ::update,
            )
        }
    }
}
