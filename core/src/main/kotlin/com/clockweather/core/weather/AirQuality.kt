package com.clockweather.core.weather

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.intOrNull

/** Air quality, UV and pollen from Open-Meteo's air-quality API (shape confirmed by `api-probe.yml`). Pure: parsing and wording only. */
object AirQuality {
    const val HOST = "air-quality-api.open-meteo.com"

    /** [aqi] European AQI (0..), [uv] current UV index, [pollen] highest of birch/grass/olive grains per m3 in the current hour. */
    data class Reading(val aqi: Int?, val uv: Double?, val pollen: Int?)

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    fun url(lat: Double, lon: Double): String =
        "https://$HOST/v1/air-quality?latitude=${"%.3f".format(java.util.Locale.ROOT, lat)}&longitude=${"%.3f".format(java.util.Locale.ROOT, lon)}" +
            "&current=european_aqi,uv_index&hourly=birch_pollen,grass_pollen,olive_pollen&timezone=auto&forecast_days=1"

    fun parse(body: String): Reading? {
        val root = runCatching { json.parseToJsonElement(body) as? JsonObject }.getOrNull() ?: return null
        val cur = root["current"] as? JsonObject ?: return null
        val aqi = (cur["european_aqi"] as? JsonPrimitive)?.doubleOrNull?.toInt()?.takeIf { it in 0..500 }
        val uv = (cur["uv_index"] as? JsonPrimitive)?.doubleOrNull?.takeIf { it in 0.0..20.0 }
        val hour = ((cur["time"] as? JsonPrimitive)?.content ?: "")
        val hourly = root["hourly"] as? JsonObject
        val times = (hourly?.get("time") as? JsonArray)?.map { (it as? JsonPrimitive)?.content }
        val idx = times?.indexOf(hour)?.takeIf { it >= 0 }
        val pollen = if (hourly == null || idx == null) null else listOf("birch_pollen", "grass_pollen", "olive_pollen")
            .mapNotNull { k -> ((hourly[k] as? JsonArray)?.getOrNull(idx) as? JsonPrimitive)?.doubleOrNull }.maxOrNull()?.toInt()?.takeIf { it in 0..100_000 }
        return if (aqi == null && uv == null && pollen == null) null else Reading(aqi, uv, pollen)
    }

    /** European AQI bands. */
    fun word(aqi: Int, hebrew: Boolean): String = when {
        aqi <= 20 -> if (hebrew) "טובה מאוד" else "Very good"
        aqi <= 40 -> if (hebrew) "טובה" else "Good"
        aqi <= 60 -> if (hebrew) "בינונית" else "Moderate"
        aqi <= 80 -> if (hebrew) "גרועה" else "Poor"
        aqi <= 100 -> if (hebrew) "גרועה מאוד" else "Very poor"
        else -> if (hebrew) "מסוכנת" else "Extremely poor"
    }

    fun uvWord(uv: Double, hebrew: Boolean): String = when {
        uv < 3 -> if (hebrew) "נמוכה" else "Low"
        uv < 6 -> if (hebrew) "בינונית" else "Moderate"
        uv < 8 -> if (hebrew) "גבוהה" else "High"
        uv < 11 -> if (hebrew) "גבוהה מאוד" else "Very high"
        else -> if (hebrew) "קיצונית" else "Extreme"
    }

    /** One short line, e.g. "Air: Moderate (59) · UV 4 · Pollen 12". Null when there is nothing to show. */
    fun line(r: Reading, hebrew: Boolean): String? {
        val parts = buildList {
            r.aqi?.let { add((if (hebrew) "אוויר: " else "Air: ") + word(it, hebrew) + " ($it)") }
            r.uv?.takeIf { it >= 0.5 }?.let { add("UV ${"%.0f".format(java.util.Locale.ROOT, it)} (${uvWord(it, hebrew)})") }
            r.pollen?.takeIf { it > 0 }?.let { add((if (hebrew) "אבקנים " else "Pollen ") + it) }
        }
        return parts.takeIf { it.isNotEmpty() }?.joinToString(" · ")
    }
}
