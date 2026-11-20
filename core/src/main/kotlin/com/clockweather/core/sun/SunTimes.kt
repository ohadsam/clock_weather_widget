package com.clockweather.core.sun

import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

/**
 * Offline sunrise/sunset (NOAA general solar position equations, accurate to ~2 minutes at Israeli latitudes).
 * It is the fallback when the weather service is unreachable and feeds the Hebrew-date rollover.
 * Polar day/night returns null.
 */
object SunTimes {
    data class Result(val sunrise: LocalTime, val sunset: LocalTime)

    fun compute(date: LocalDate, latitude: Double, longitude: Double, zone: ZoneId): Result? {
        val rise = event(date, latitude, longitude, zone, rising = true) ?: return null
        val set = event(date, latitude, longitude, zone, rising = false) ?: return null
        return Result(rise, set)
    }

    /** Evening moment when the sun is [degreesBelowHorizon] below the horizon (8.5 = nightfall / tzeit hakochavim). Null if it never gets there. */
    fun eveningAtDepression(date: LocalDate, latitude: Double, longitude: Double, zone: ZoneId, degreesBelowHorizon: Double): LocalTime? =
        event(date, latitude, longitude, zone, rising = false, zenith = 90.0 + degreesBelowHorizon)

    private fun event(date: LocalDate, lat: Double, lon: Double, zone: ZoneId, rising: Boolean, zenith: Double = 90.833): LocalTime? {
        val noon = date.atTime(12, 0).atZone(zone)
        val offsetHours = noon.offset.totalSeconds / 3600.0
        val dayOfYear = date.dayOfYear
        val gamma = 2.0 * Math.PI / 365.0 * (dayOfYear - 1 + ((if (rising) 6 else 18) - 12) / 24.0)
        val eqTime = 229.18 * (0.000075 + 0.001868 * cos(gamma) - 0.032077 * sin(gamma) - 0.014615 * cos(2 * gamma) - 0.040849 * sin(2 * gamma))
        val decl = 0.006918 - 0.399912 * cos(gamma) + 0.070257 * sin(gamma) - 0.006758 * cos(2 * gamma) + 0.000907 * sin(2 * gamma) - 0.002697 * cos(3 * gamma) + 0.00148 * sin(3 * gamma)
        val latRad = Math.toRadians(lat)
        val cosHa = cos(Math.toRadians(zenith)) / (cos(latRad) * cos(decl)) - tan(latRad) * tan(decl)
        if (cosHa < -1.0 || cosHa > 1.0) return null
        val ha = Math.toDegrees(acos(cosHa))
        val minutesUtc = 720.0 - 4.0 * (lon + (if (rising) ha else -ha)) - eqTime
        val local = minutesUtc + offsetHours * 60.0
        val normalized = ((local % 1440.0) + 1440.0) % 1440.0
        return LocalTime.of((normalized / 60).toInt().coerceIn(0, 23), (normalized % 60).toInt().coerceIn(0, 59))
    }
}
