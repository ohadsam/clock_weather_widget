package com.clockweather.core.sun

import com.clockweather.core.hebrew.HolidayKind
import com.clockweather.core.hebrew.Holidays
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

/** Shabbat / Yom Tov mode: from candle lighting on the eve until nightfall when it ends. Pure: place, date and time are parameters. */
object ShabbatMode {
    private fun isRestDay(d: LocalDate, inIsrael: Boolean) =
        d.dayOfWeek == DayOfWeek.SATURDAY || Holidays.on(d, inIsrael).any { it.kind == HolidayKind.MAJOR && it.dayOff }

    /** Nightfall (havdalah) of the LAST rest day of a run (Shabbat, Yom Tov), else null: the moment the calm ends. */
    fun havdalahOn(date: LocalDate, lat: Double, lon: Double, zone: ZoneId, inIsrael: Boolean): LocalTime? =
        if (isRestDay(date, inIsrael) && !isRestDay(date.plusDays(1), inIsrael)) Zmanim.of(date, lat, lon, zone)?.nightfall else null

    /** Candle-lighting time on an eve (Friday, or the day before Yom Tov), else null. */
    fun candleLightingOn(date: LocalDate, lat: Double, lon: Double, zone: ZoneId, inIsrael: Boolean): LocalTime? =
        if (Zmanim.isCandleLightingDay(date, inIsrael)) Zmanim.of(date, lat, lon, zone)?.candleLighting else null

    fun isActive(date: LocalDate, time: LocalTime, lat: Double, lon: Double, zone: ZoneId, inIsrael: Boolean): Boolean {
        val today = Zmanim.of(date, lat, lon, zone) ?: return false
        if (Zmanim.isCandleLightingDay(date, inIsrael) && time >= today.candleLighting) return true // the eve, from candle lighting
        if (isRestDay(date, inIsrael)) {
            val end = today.nightfall ?: return true
            // a rest day followed by another rest day (two-day Yom Tov, Yom Tov into Shabbat) continues past nightfall
            return time < end || isRestDay(date.plusDays(1), inIsrael)
        }
        return false
    }
}
