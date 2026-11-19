package com.clockweather.core.weather

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

/** What the sky looks like, independent of the data provider. Each value maps to one illustration (DESIGN_LANGUAGE asset contract). */
enum class Sky(val assetKey: String) {
    CLEAR("clear"), MOSTLY_CLEAR("mostly_clear"), PARTLY_CLOUDY("partly_cloudy"), OVERCAST("overcast"),
    FOG("fog"), DRIZZLE("drizzle"), RAIN("rain"), HEAVY_RAIN("heavy_rain"), FREEZING_RAIN("freezing_rain"),
    SNOW("snow"), THUNDERSTORM("thunderstorm"), HAIL("hail"), UNKNOWN("unknown");

    val isWet: Boolean get() = this in setOf(DRIZZLE, RAIN, HEAVY_RAIN, FREEZING_RAIN, SNOW, THUNDERSTORM, HAIL)

    fun label(hebrew: Boolean): String = when (this) {
        CLEAR -> if (hebrew) "בהיר" else "Clear"
        MOSTLY_CLEAR -> if (hebrew) "בהיר ברובו" else "Mostly clear"
        PARTLY_CLOUDY -> if (hebrew) "מעונן חלקית" else "Partly cloudy"
        OVERCAST -> if (hebrew) "מעונן" else "Overcast"
        FOG -> if (hebrew) "ערפל" else "Fog"
        DRIZZLE -> if (hebrew) "טפטוף" else "Drizzle"
        RAIN -> if (hebrew) "גשם" else "Rain"
        HEAVY_RAIN -> if (hebrew) "גשם חזק" else "Heavy rain"
        FREEZING_RAIN -> if (hebrew) "גשם קפוא" else "Freezing rain"
        SNOW -> if (hebrew) "שלג" else "Snow"
        THUNDERSTORM -> if (hebrew) "סופת רעמים" else "Thunderstorm"
        HAIL -> if (hebrew) "ברד" else "Hail"
        UNKNOWN -> if (hebrew) "לא ידוע" else "Unknown"
    }

    companion object {
        /** WMO weather interpretation codes as used by Open-Meteo. Unknown codes never crash: they become [UNKNOWN]. */
        fun fromWmo(code: Int?): Sky = when (code) {
            0 -> CLEAR
            1 -> MOSTLY_CLEAR
            2 -> PARTLY_CLOUDY
            3 -> OVERCAST
            45, 48 -> FOG
            51, 53, 55 -> DRIZZLE
            56, 57, 66, 67 -> FREEZING_RAIN
            61, 63, 80, 81 -> RAIN
            65, 82 -> HEAVY_RAIN
            71, 73, 75, 77, 85, 86 -> SNOW
            95 -> THUNDERSTORM
            96, 99 -> HAIL
            else -> UNKNOWN
        }
    }
}

data class Place(
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val zoneId: String = "auto",
    /** True for the "follow my location" entry, whose coordinates are replaced at refresh time. */
    val isCurrentLocation: Boolean = false,
)

data class CurrentWeather(
    val tempC: Double,
    val feelsLikeC: Double?,
    val humidityPct: Int?,
    val windKmh: Double?,
    val sky: Sky,
    val isDay: Boolean,
)

data class HourPoint(
    val time: LocalDateTime,
    val tempC: Double,
    val rainChancePct: Int?,
    val rainMm: Double?,
    val humidityPct: Int?,
    val sky: Sky,
    val isDay: Boolean,
)

data class DayForecast(
    val date: LocalDate,
    val maxC: Double,
    val minC: Double,
    val rainChancePct: Int?,
    val rainSumMm: Double?,
    val sky: Sky,
    val sunrise: LocalDateTime?,
    val sunset: LocalDateTime?,
)

/** One fetched forecast for a place, with the zone its local times belong to and when it was fetched. */
data class Forecast(
    val placeId: String,
    val zone: ZoneId,
    val fetchedAtEpochSec: Long,
    val current: CurrentWeather?,
    val days: List<DayForecast>,
    val hours: List<HourPoint>,
) {
    fun today(now: java.time.Instant): DayForecast? {
        val date = now.atZone(zone).toLocalDate()
        return days.firstOrNull { it.date == date } ?: days.firstOrNull()
    }

    /** Hours of the local day of [date], in order (for the day-detail graph). */
    fun hoursOf(date: LocalDate): List<HourPoint> = hours.filter { it.time.toLocalDate() == date }
}
