package com.clockweather.app.data

import android.content.Context
import com.clockweather.core.weather.AirQuality
import com.clockweather.core.widget.WidgetSettings

/** Air quality of the active place, fetched only while "Air quality" is on (extra host, see docs/DATA_AND_PRIVACY.md); cached as raw JSON. */
object AirQualityRepository {
    private const val PREFS = "airq"

    fun refresh(ctx: Context, s: WidgetSettings) {
        val p = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        if (!s.showAirQuality) { if (p.contains("body")) p.edit().clear().apply(); return }
        val place = s.places.firstOrNull { it.id == s.activePlaceId } ?: return
        val loc = if (place.isCurrent) LocationSource.last(ctx) ?: return else place.lat to place.lon
        val body = Net.get(AirQuality.url(loc.first, loc.second)) ?: return
        if (AirQuality.parse(body) == null) return
        p.edit().putString("body", body).apply()
    }

    fun cached(ctx: Context): AirQuality.Reading? =
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString("body", null)?.let { AirQuality.parse(it) }
}
