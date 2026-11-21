package com.clockweather.core.widget

import java.time.LocalTime

/** Night mode: between [from] and [to] (hours, may wrap past midnight) animations and alerts stay off. */
object QuietHours {
    fun isQuiet(enabled: Boolean, fromHour: Int, toHour: Int, time: LocalTime): Boolean {
        if (!enabled || fromHour == toHour) return false
        val h = time.hour
        return if (fromHour < toHour) h in fromHour until toHour else h >= fromHour || h < toHour
    }
}
