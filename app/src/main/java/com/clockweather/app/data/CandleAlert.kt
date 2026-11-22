package com.clockweather.app.data

import android.content.Context
import com.clockweather.app.R
import com.clockweather.core.sun.ShabbatMode
import com.clockweather.core.widget.WidgetSettings
import java.time.Duration
import java.time.Instant
import java.time.ZoneId

/** With Shabbat mode on: one notification 15 minutes before candle lighting (on a Friday or the eve of a holiday), once per day. The minute tick calls this. */
object CandleAlert {
    private const val PREFS = "candle_alert"
    private const val LEAD_MIN = 15L

    fun check(ctx: Context, s: WidgetSettings, now: Instant = Instant.now()) {
        if (!s.shabbatMode || !Notifier.canPost(ctx)) return
        val place = s.places.firstOrNull { it.id == s.activePlaceId } ?: return
        val zone = ZoneId.systemDefault()
        val local = now.atZone(zone)
        val at = ShabbatMode.candleLightingOn(local.toLocalDate(), place.lat, place.lon, zone, s.inIsrael) ?: return
        val minutesTo = Duration.between(local.toLocalDateTime(), local.toLocalDate().atTime(at)).toMinutes()
        if (minutesTo !in -5L..LEAD_MIN) return
        val prefs = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val day = local.toLocalDate().toString()
        if (prefs.getString("last", "") == day) return
        val hebrew = com.clockweather.app.widget.Snapshot.isHebrew(ctx, s.language)
        Notifier.post(ctx, Notifier.CH_CANDLE, 4004, if (hebrew) "הדלקת נרות" else "Candle lighting",
            if (hebrew) "הדלקת נרות ב-$at (${place.name})" else "Candle lighting at $at (${place.name})", "home", R.string.channel_candle)
        prefs.edit().putString("last", day).apply()
    }
}
