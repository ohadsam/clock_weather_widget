package com.clockweather.app.data

import android.content.Context
import com.clockweather.core.weather.Forecast
import com.clockweather.core.weather.OpenMeteo
import com.clockweather.core.widget.PlaceSetting
import com.clockweather.core.widget.WidgetSettings

/** Fetches and caches forecasts per place (raw JSON in SharedPreferences, parsed by :core on read). */
object WeatherRepository {
    private const val PREFS = "forecast"

    fun cached(ctx: Context, placeId: String): Forecast? {
        val p = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val body = p.getString("body_$placeId", null) ?: return null
        return OpenMeteo.parseForecast(placeId, body, p.getLong("at_$placeId", 0L))
    }

    /** Blocking. Refreshes every saved place (max 4); a failed fetch keeps the previous cache. */
    fun refreshAll(ctx: Context, settings: WidgetSettings, nowSec: Long = System.currentTimeMillis() / 1000) {
        for (place in settings.places) refresh(ctx, resolve(ctx, place), nowSec)
    }

    private fun resolve(ctx: Context, place: PlaceSetting): PlaceSetting {
        if (!place.isCurrent) return place
        val loc = LocationSource.last(ctx) ?: return place
        return place.copy(lat = loc.first, lon = loc.second)
    }

    private fun refresh(ctx: Context, place: PlaceSetting, nowSec: Long) {
        val body = Net.get(OpenMeteo.forecastUrl(place.lat, place.lon)) ?: return
        if (OpenMeteo.parseForecast(place.id, body, nowSec) == null) return
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putString("body_${place.id}", body).putLong("at_${place.id}", nowSec).apply()
    }

    fun search(query: String, language: String): List<OpenMeteo.GeoHit> {
        if (query.trim().length < 2) return emptyList()
        val body = Net.get(OpenMeteo.geocodingUrl(query, language)) ?: return emptyList()
        return OpenMeteo.parseGeocoding(body)
    }
}
