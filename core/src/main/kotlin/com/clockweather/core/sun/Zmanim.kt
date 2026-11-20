package com.clockweather.core.sun

import com.clockweather.core.hebrew.HolidayKind
import com.clockweather.core.hebrew.Holidays
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

/**
 * Jewish times of day computed offline: nightfall (tzeit hakochavim, sun 8.5 degrees below the horizon) and the Shabbat/holiday
 * candle lighting (a set number of minutes before sunset) and havdalah (= nightfall). Pure: date and place are parameters.
 */
object Zmanim {
    const val NIGHTFALL_DEGREES = 8.5
    const val DEFAULT_CANDLE_MINUTES = 18

    data class Day(val sunset: LocalTime, val nightfall: LocalTime?, val candleLighting: LocalTime)

    fun of(date: LocalDate, lat: Double, lon: Double, zone: ZoneId, candleMinutes: Int = DEFAULT_CANDLE_MINUTES): Day? {
        val sun = SunTimes.compute(date, lat, lon, zone) ?: return null
        val night = SunTimes.eveningAtDepression(date, lat, lon, zone, NIGHTFALL_DEGREES)
        return Day(sun.sunset, night, sun.sunset.minusMinutes(candleMinutes.toLong()))
    }

    /** Friday or the eve of a Yom Tov: candle lighting applies that evening. */
    fun isCandleLightingDay(date: LocalDate, inIsrael: Boolean = true): Boolean =
        date.dayOfWeek == DayOfWeek.FRIDAY || Holidays.on(date.plusDays(1), inIsrael).any { it.kind == HolidayKind.MAJOR && it.dayOff }
}
