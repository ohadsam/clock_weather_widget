package com.clockweather.app.data

import android.content.Context
import com.clockweather.core.weather.RainAlert
import com.clockweather.core.widget.QuietHours
import com.clockweather.core.widget.WidgetSettings
import java.time.Instant
import java.time.ZoneId

/** After each weather refresh: if the owner turned it on, rain is about to start at the active place, it is not quiet time and we did not notify in the last 6 hours, post one notification. */
object RainAlerts {
    private const val PREFS = "rain_alert"

    fun check(ctx: Context, s: WidgetSettings, now: Instant = Instant.now()) {
        if (!s.rainAlert || !Notifier.canPost(ctx)) return
        val place = s.places.firstOrNull { it.id == s.activePlaceId } ?: return
        val f = WeatherRepository.cached(ctx, place.id) ?: return
        val local = now.atZone(ZoneId.systemDefault())
        if (s.shabbatMode && com.clockweather.core.sun.ShabbatMode.isActive(local.toLocalDate(), local.toLocalTime(), place.lat, place.lon, ZoneId.systemDefault(), s.inIsrael)) return
        if (QuietHours.isQuiet(s.quietHoursEnabled, s.quietFromHour, s.quietToHour, now.atZone(ZoneId.systemDefault()).toLocalTime())) return
        val prefs = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        if (!RainAlert.mayNotify(prefs.getLong("last", 0L), now.toEpochMilli())) return
        val alert = RainAlert.next(f.hours, now, f.zone, s.rainLikelyFromPct) ?: return
        val hebrew = com.clockweather.app.widget.Snapshot.isHebrew(ctx, s.language)
        val title = if (hebrew) "גשם בדרך" else "Rain is coming"
        val text = if (hebrew) "גשם צפוי ב${place.name} בעוד כ-${alert.inMinutes} דקות (סיכוי ${alert.chancePct}%)" else "Rain expected in ${place.name} in about ${alert.inMinutes} min (${alert.chancePct}% chance)"
        Notifier.post(ctx, Notifier.CH_RAIN, 4002, title, text, "weather")
        prefs.edit().putLong("last", now.toEpochMilli()).apply()
    }
}
