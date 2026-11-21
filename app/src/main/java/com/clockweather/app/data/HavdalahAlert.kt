package com.clockweather.app.data

import android.content.Context
import com.clockweather.app.R
import com.clockweather.core.sun.ShabbatMode
import com.clockweather.core.widget.WidgetSettings
import java.time.Instant
import java.time.ZoneId

/** With Shabbat mode on: one quiet notification when Shabbat / Yom Tov ends (nightfall), at most once per day, within 45 minutes of the moment (the minute tick calls this). */
object HavdalahAlert {
    private const val PREFS = "havdalah_alert"

    fun check(ctx: Context, s: WidgetSettings, now: Instant = Instant.now()) {
        if (!s.shabbatMode || !Notifier.canPost(ctx)) return
        val place = s.places.firstOrNull { it.id == s.activePlaceId } ?: return
        val zone = ZoneId.systemDefault()
        val local = now.atZone(zone)
        val end = ShabbatMode.havdalahOn(local.toLocalDate(), place.lat, place.lon, zone, s.inIsrael) ?: return
        val minutesSince = java.time.Duration.between(local.toLocalDate().atTime(end), local.toLocalDateTime()).toMinutes()
        if (minutesSince !in 0..45) return
        val prefs = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val day = local.toLocalDate().toString()
        if (prefs.getString("last", "") == day) return
        val hebrew = com.clockweather.app.widget.Snapshot.isHebrew(ctx, s.language)
        Notifier.post(ctx, Notifier.CH_HAVDALAH, 4003, if (hebrew) "שבוע טוב" else "Shavua tov", if (hebrew) "השבת יצאה ב-$end. הווידג'ט חזר להצגה רגילה." else "Shabbat ended at $end. The widget is back to normal.", "home", R.string.channel_havdalah)
        prefs.edit().putString("last", day).apply()
    }
}
