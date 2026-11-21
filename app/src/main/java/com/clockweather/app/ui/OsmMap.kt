package com.clockweather.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import java.io.File

/** A pin on the map. */
data class MapPin(val lat: Double, val lon: Double, val title: String)

/**
 * OpenStreetMap view (osmdroid). Tiles are downloaded only while this composable is on screen, which the picker allows only after the user
 * pressed "Show map". A tap reports the point; saved places are pins; [candidate] is the pin being considered; [focus] pans the map.
 */
@Composable
fun OsmMap(pins: List<MapPin>, candidate: MapPin?, focus: MapPin?, onTap: (Double, Double) -> Unit, modifier: Modifier = Modifier) {
    val ctx = LocalContext.current
    val tap by rememberUpdatedState(onTap)
    val map = remember {
        Configuration.getInstance().apply {
            userAgentValue = ctx.packageName
            osmdroidBasePath = File(ctx.cacheDir, "osmdroid")
            osmdroidTileCache = File(ctx.cacheDir, "osmdroid/tiles")
        }
        MapView(ctx).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            minZoomLevel = 2.0
            controller.setZoom(3.0)
            controller.setCenter(GeoPoint(28.0, 25.0))
        }
    }
    val owner = LocalLifecycleOwner.current
    DisposableEffect(owner) {
        val obs = LifecycleEventObserver { _, e ->
            if (e == Lifecycle.Event.ON_RESUME) map.onResume() else if (e == Lifecycle.Event.ON_PAUSE) map.onPause()
        }
        owner.lifecycle.addObserver(obs)
        onDispose { owner.lifecycle.removeObserver(obs); map.onDetach() }
    }
    AndroidView(factory = { map }, modifier = modifier) { mv ->
        mv.overlays.clear()
        mv.overlays.add(MapEventsOverlay(object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint): Boolean { tap(p.latitude, p.longitude); return true }
            override fun longPressHelper(p: GeoPoint): Boolean = false
        }))
        for (pin in pins) mv.overlays.add(Marker(mv).apply { position = GeoPoint(pin.lat, pin.lon); title = pin.title; setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM) })
        candidate?.let { c -> mv.overlays.add(Marker(mv).apply { position = GeoPoint(c.lat, c.lon); title = c.title; setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM); showInfoWindow() }) }
        mv.invalidate()
    }
    LaunchedEffect(focus) {
        focus?.let { map.controller.animateTo(GeoPoint(it.lat, it.lon)); if (map.zoomLevelDouble < 6.0) map.controller.setZoom(6.0) }
    }
}
