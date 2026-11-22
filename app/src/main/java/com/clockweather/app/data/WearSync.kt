package com.clockweather.app.data

import android.content.Context
import com.clockweather.app.widget.Snapshot
import com.clockweather.core.wear.WearState
import com.clockweather.core.weather.TempFormat
import com.google.android.gms.wearable.PutDataRequest
import com.google.android.gms.wearable.Wearable
import java.time.Instant

/**
 * Hands the watch (the Wear OS tile of this app) what it shows: the active place's weather and the running countdowns, as one small
 * formatted JSON over the Google Play services Data Layer (Bluetooth / Wi-Fi between the owner's own paired devices; no server).
 * Sent only when something changed (or [force] on the periodic weather refresh, so the watch can tell the data is current).
 */
object WearSync {
    private const val PREFS = "wear_sync"

    fun push(ctx: Context, force: Boolean = false) {
        runCatching {
            val s = SettingsStore.load(ctx)
            val place = s.places.firstOrNull { it.id == s.activePlaceId }
            val now = System.currentTimeMillis()
            val hebrew = Snapshot.isHebrew(ctx, s.language)
            val f = place?.let { WeatherRepository.cached(ctx, it.id) }
            val day = f?.today(Instant.ofEpochMilli(now))
            val cur = f?.current
            val temp = cur?.let { TempFormat.short(it.tempC, s.fahrenheit) }.orEmpty()
            val hiLo = day?.let { "${TempFormat.short(it.maxC, s.fahrenheit)} / ${TempFormat.short(it.minC, s.fahrenheit)}" }.orEmpty()
            val sky = (cur?.sky ?: day?.sky)?.label(hebrew).orEmpty()
            val zone = java.time.ZoneId.systemDefault()
            val events = SpecialEventsSource.upcoming(ctx, Instant.ofEpochMilli(now), zone, 14).take(WearState.MAX_EVENTS).map { e ->
                "${e.emoji} ${e.title}, ${com.clockweather.core.agenda.SpecialEvents.whenText(e.daysLeft, hebrew)}"
            }
            val state = WearState.of(now, place?.name.orEmpty(), temp, hiLo, day?.rainChancePct, sky, hebrew, TimerStore.items(ctx), events)
            val fingerprint = state.copy(at = 0L).encode()
            val prefs = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            if (!force && prefs.getString("last", null) == fingerprint) return
            val request = PutDataRequest.create(WearState.PATH).setData(state.encode().toByteArray(Charsets.UTF_8)).setUrgent()
            Wearable.getDataClient(ctx).putDataItem(request).addOnSuccessListener { prefs.edit().putString("last", fingerprint).apply() }
        }
    }
}
