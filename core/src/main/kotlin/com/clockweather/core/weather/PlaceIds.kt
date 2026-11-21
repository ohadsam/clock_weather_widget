package com.clockweather.core.weather

import kotlin.math.abs

/** Stable ids and fallback names for places picked by search or by tapping the map. */
object PlaceIds {
    /** Same point (to ~100 m) always gives the same id, so picking it twice never duplicates it. */
    fun make(lat: Double, lon: Double): String = "p" + Math.round(lat * 1000).toInt() + "_" + Math.round(lon * 1000).toInt()

    /** `32.09° N, 34.88° E` - used when no place name is known for a tapped point. */
    fun coordLabel(lat: Double, lon: Double): String =
        "%.2f° %s, %.2f° %s".format(java.util.Locale.ENGLISH, abs(lat), if (lat >= 0) "N" else "S", abs(lon), if (lon >= 0) "E" else "W")
}
