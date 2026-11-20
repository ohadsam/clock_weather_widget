package com.clockweather.core.weather

/**
 * Which frame of a weather loop to show at a given time. Loops are 12 frames at 8 fps (1.5 s) made from a few unique
 * images (WeatherAnimTable, generated from the approved ChatGPT frames). Pure: time is a parameter.
 */
object WeatherAnim {
    const val FRAMES = 12
    const val FPS = 8
    const val LOOP_MS = FRAMES * 1000L / FPS

    /** Key as in the table: `<sky>_<day|night>`, night only exists for the three clear-ish skies. */
    fun key(sky: Sky, isDay: Boolean): String {
        val night = !isDay && sky in setOf(Sky.CLEAR, Sky.MOSTLY_CLEAR, Sky.PARTLY_CLOUDY)
        return "${sky.assetKey}_${if (night) "night" else "day"}"
    }

    fun has(key: String): Boolean = WeatherAnimTable.SEQUENCES.containsKey(key)

    /** Number of unique frame images of [key] (resource suffix 1..n), 0 when there is no loop. */
    fun uniqueFrames(key: String): Int = WeatherAnimTable.SEQUENCES[key]?.max() ?: 0

    /** 1-based unique-frame index for [key] at [timeMs] since the loop started, or null when [key] has no loop. */
    fun frameAt(key: String, timeMs: Long): Int? {
        val seq = WeatherAnimTable.SEQUENCES[key] ?: return null
        val slot = Math.floorMod(timeMs * FPS / 1000L, FRAMES.toLong()).toInt()
        return seq[slot]
    }
}
