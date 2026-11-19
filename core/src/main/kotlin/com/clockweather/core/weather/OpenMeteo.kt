package com.clockweather.core.weather

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonObject
import java.time.DateTimeException
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Locale

/**
 * Open-Meteo request builder and tolerant parser. Pure: the network call lives in :app.
 * The field names below come from the public API docs and MUST be confirmed by the probe workflow
 * (.github/workflows/api-probe.yml) before release: the sandbox cannot reach the API.
 */
object OpenMeteo {
    const val HOST = "api.open-meteo.com"
    const val GEOCODING_HOST = "geocoding-api.open-meteo.com"
    private const val MAX_BYTES = 512 * 1024

    fun forecastUrl(lat: Double, lon: Double, days: Int = 3): String =
        "https://$HOST/v1/forecast?latitude=${coord(lat)}&longitude=${coord(lon)}" +
            "&current=temperature_2m,relative_humidity_2m,apparent_temperature,is_day,weather_code,wind_speed_10m" +
            "&hourly=temperature_2m,relative_humidity_2m,precipitation_probability,precipitation,weather_code,is_day" +
            "&daily=weather_code,temperature_2m_max,temperature_2m_min,precipitation_probability_max,precipitation_sum,sunrise,sunset" +
            "&timezone=auto&forecast_days=${days.coerceIn(1, 7)}"

    fun geocodingUrl(query: String, language: String, count: Int = 6): String =
        "https://$GEOCODING_HOST/v1/search?name=${java.net.URLEncoder.encode(query.trim().take(80), "UTF-8")}" +
            "&count=${count.coerceIn(1, 10)}&language=${language.take(2)}&format=json"

    private fun coord(v: Double) = String.format(Locale.US, "%.3f", v.coerceIn(-180.0, 180.0))

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    /** Returns null when the body is not a usable forecast (never throws on bad input). */
    fun parseForecast(placeId: String, body: String, fetchedAtEpochSec: Long): Forecast? {
        if (body.length > MAX_BYTES) return null
        val root = runCatching { json.parseToJsonElement(body).jsonObject }.getOrNull() ?: return null
        val zone = (root.str("timezone"))?.let { runCatching { ZoneId.of(it) }.getOrNull() } ?: ZoneId.systemDefault()
        val daily = root["daily"] as? JsonObject
        val hourly = root["hourly"] as? JsonObject
        val days = parseDays(daily)
        if (days.isEmpty()) return null
        val current = (root["current"] as? JsonObject)?.let { c ->
            val t = c.num("temperature_2m") ?: return@let null
            CurrentWeather(
                tempC = t,
                feelsLikeC = c.num("apparent_temperature"),
                humidityPct = c.num("relative_humidity_2m")?.toInt(),
                windKmh = c.num("wind_speed_10m"),
                sky = Sky.fromWmo(c.num("weather_code")?.toInt()),
                isDay = (c.num("is_day") ?: 1.0) >= 1.0,
            )
        }
        return Forecast(placeId, zone, fetchedAtEpochSec, current, days, parseHours(hourly))
    }

    private fun parseDays(d: JsonObject?): List<DayForecast> {
        d ?: return emptyList()
        val times = d.arr("time") ?: return emptyList()
        return times.indices.mapNotNull { i ->
            val date = times.strAt(i)?.let { runCatching { java.time.LocalDate.parse(it) }.getOrNull() } ?: return@mapNotNull null
            val max = d.arr("temperature_2m_max")?.numAt(i) ?: return@mapNotNull null
            val min = d.arr("temperature_2m_min")?.numAt(i) ?: return@mapNotNull null
            DayForecast(
                date = date, maxC = max, minC = min,
                rainChancePct = d.arr("precipitation_probability_max")?.numAt(i)?.toInt()?.coerceIn(0, 100),
                rainSumMm = d.arr("precipitation_sum")?.numAt(i),
                sky = Sky.fromWmo(d.arr("weather_code")?.numAt(i)?.toInt()),
                sunrise = d.arr("sunrise")?.strAt(i)?.let(::localTime),
                sunset = d.arr("sunset")?.strAt(i)?.let(::localTime),
            )
        }
    }

    private fun parseHours(h: JsonObject?): List<HourPoint> {
        h ?: return emptyList()
        val times = h.arr("time") ?: return emptyList()
        return times.indices.mapNotNull { i ->
            val time = times.strAt(i)?.let(::localTime) ?: return@mapNotNull null
            val temp = h.arr("temperature_2m")?.numAt(i) ?: return@mapNotNull null
            HourPoint(
                time = time, tempC = temp,
                rainChancePct = h.arr("precipitation_probability")?.numAt(i)?.toInt()?.coerceIn(0, 100),
                rainMm = h.arr("precipitation")?.numAt(i),
                humidityPct = h.arr("relative_humidity_2m")?.numAt(i)?.toInt()?.coerceIn(0, 100),
                sky = Sky.fromWmo(h.arr("weather_code")?.numAt(i)?.toInt()),
                isDay = (h.arr("is_day")?.numAt(i) ?: 1.0) >= 1.0,
            )
        }
    }

    private fun localTime(s: String): LocalDateTime? = try { LocalDateTime.parse(s) } catch (_: DateTimeException) { null }

    private fun JsonObject.str(k: String): String? = (this[k] as? JsonPrimitive)?.contentOrNull
    private fun JsonObject.num(k: String): Double? = (this[k] as? JsonPrimitive)?.doubleOrNull
    private fun JsonObject.arr(k: String): JsonArray? = this[k] as? JsonArray
    private fun JsonArray.numAt(i: Int): Double? = (getOrNull(i) as? JsonPrimitive)?.doubleOrNull
    private fun JsonArray.strAt(i: Int): String? = (getOrNull(i) as? JsonPrimitive)?.contentOrNull

    /** A geocoding hit; names are untrusted text and are cleaned before they reach the UI. */
    data class GeoHit(val name: String, val admin: String?, val country: String?, val lat: Double, val lon: Double, val zone: String?)

    fun parseGeocoding(body: String): List<GeoHit> {
        if (body.length > MAX_BYTES) return emptyList()
        val root = runCatching { json.parseToJsonElement(body).jsonObject }.getOrNull() ?: return emptyList()
        val results = root["results"] as? JsonArray ?: return emptyList()
        return results.mapNotNull { e ->
            val o = e as? JsonObject ?: return@mapNotNull null
            val lat = o.num("latitude") ?: return@mapNotNull null
            val lon = o.num("longitude") ?: return@mapNotNull null
            if (lat !in -90.0..90.0 || lon !in -180.0..180.0) return@mapNotNull null
            val name = com.clockweather.core.text.Untrusted.clean(o.str("name") ?: return@mapNotNull null, 60)
            if (name.isEmpty()) return@mapNotNull null
            GeoHit(name, o.str("admin1")?.let { com.clockweather.core.text.Untrusted.clean(it, 60) }, o.str("country")?.let { com.clockweather.core.text.Untrusted.clean(it, 60) }, lat, lon, o.str("timezone"))
        }
    }
}
