package com.clockweather.core.weather

/** A comfort word for the "feels like" temperature (Celsius). */
object Comfort {
    fun word(feelsC: Double, hebrew: Boolean): String = when {
        feelsC < 5 -> if (hebrew) "קר מאוד" else "Very cold"
        feelsC < 13 -> if (hebrew) "קר" else "Cold"
        feelsC < 19 -> if (hebrew) "קריר" else "Cool"
        feelsC < 27 -> if (hebrew) "נעים" else "Pleasant"
        feelsC < 33 -> if (hebrew) "חם" else "Warm"
        feelsC < 39 -> if (hebrew) "חם מאוד" else "Hot"
        else -> if (hebrew) "חום קיצוני" else "Extreme heat"
    }

    /** UV index words (WHO scale). */
    fun uvWord(uv: Double, hebrew: Boolean): String = when {
        uv < 3 -> if (hebrew) "נמוך" else "Low"
        uv < 6 -> if (hebrew) "בינוני" else "Moderate"
        uv < 8 -> if (hebrew) "גבוה" else "High"
        uv < 11 -> if (hebrew) "גבוה מאוד" else "Very high"
        else -> if (hebrew) "קיצוני" else "Extreme"
    }
}
