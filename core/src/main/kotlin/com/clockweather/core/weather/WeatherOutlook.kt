package com.clockweather.core.weather

/** The one-line answer the widget gives: will it rain, will it be sunny. Pure. */
enum class Outlook { SUNNY, PARTLY_SUNNY, CLOUDY, RAIN_LIKELY, RAIN_POSSIBLE, STORM, SNOW, FOG }

object WeatherOutlook {
    /** Rain is "likely" at or above this chance (setting; default 50). Below [possibleFromPct] it is not mentioned. */
    fun of(day: DayForecast, likelyFromPct: Int = 50, possibleFromPct: Int = 25): Outlook {
        val chance = day.rainChancePct ?: if (day.sky.isWet) 100 else 0
        return when {
            day.sky == Sky.THUNDERSTORM || day.sky == Sky.HAIL -> Outlook.STORM
            day.sky == Sky.SNOW -> Outlook.SNOW
            day.sky.isWet && chance >= likelyFromPct -> Outlook.RAIN_LIKELY
            chance >= likelyFromPct -> Outlook.RAIN_LIKELY
            chance >= possibleFromPct -> Outlook.RAIN_POSSIBLE
            day.sky == Sky.FOG -> Outlook.FOG
            day.sky == Sky.OVERCAST -> Outlook.CLOUDY
            day.sky == Sky.PARTLY_CLOUDY -> Outlook.PARTLY_SUNNY
            else -> Outlook.SUNNY
        }
    }

    fun text(o: Outlook, rainChancePct: Int?, hebrew: Boolean): String {
        val pct = rainChancePct?.let { if (hebrew) " ($it%)" else " ($it%)" } ?: ""
        return when (o) {
            Outlook.SUNNY -> if (hebrew) "צפוי להיות שמשי" else "Sunny day ahead"
            Outlook.PARTLY_SUNNY -> if (hebrew) "שמשי חלקית" else "Partly sunny"
            Outlook.CLOUDY -> if (hebrew) "צפוי להיות מעונן" else "Cloudy day ahead"
            Outlook.RAIN_LIKELY -> (if (hebrew) "צפוי גשם" else "Rain expected") + pct
            Outlook.RAIN_POSSIBLE -> (if (hebrew) "אפשרות לגשם" else "Chance of rain") + pct
            Outlook.STORM -> if (hebrew) "צפויה סערה" else "Storm expected"
            Outlook.SNOW -> if (hebrew) "צפוי שלג" else "Snow expected"
            Outlook.FOG -> if (hebrew) "צפוי ערפל" else "Fog expected"
        }
    }
}

/** Temperature text. The unit is a setting; the number is rounded, never "-0". */
object TempFormat {
    fun value(c: Double, fahrenheit: Boolean): Int {
        val v = if (fahrenheit) c * 9.0 / 5.0 + 32.0 else c
        val r = Math.round(v).toInt()
        return if (r == 0) 0 else r
    }

    fun short(c: Double, fahrenheit: Boolean): String = "${value(c, fahrenheit)}°"
    fun withUnit(c: Double, fahrenheit: Boolean): String = "${value(c, fahrenheit)}°${if (fahrenheit) "F" else "C"}"
}
