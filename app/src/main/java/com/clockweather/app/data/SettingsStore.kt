package com.clockweather.app.data

import android.content.Context
import com.clockweather.core.widget.WidgetSettings

/** Persists WidgetSettings as one sanitised JSON string. Knows nothing about the UI or the widget. */
object SettingsStore {
    private const val PREFS = "settings"
    private const val KEY = "json"

    fun load(ctx: Context): WidgetSettings =
        WidgetSettings.decode(ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY, null))

    fun save(ctx: Context, s: WidgetSettings) {
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY, WidgetSettings.encode(s)).apply()
    }
}
