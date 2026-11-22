package com.clockweather.wear

import android.content.Context
import android.net.Uri
import com.clockweather.core.wear.WearState
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.Wearable
import java.util.concurrent.TimeUnit

/** The latest state the phone sent, kept on the watch (raw JSON, parsed and clamped by :core on read). */
object StateStore {
    private const val PREFS = "state"

    fun save(ctx: Context, json: String) { ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString("json", json).apply() }

    /** Blocking (background thread): asks the Data Layer for the current item first, falls back to the stored copy. */
    fun load(ctx: Context): WearState? {
        runCatching {
            val buffer = Tasks.await(Wearable.getDataClient(ctx).getDataItems(Uri.parse("wear://*" + WearState.PATH)), 2, TimeUnit.SECONDS)
            try {
                buffer.firstOrNull()?.data?.toString(Charsets.UTF_8)?.takeIf { WearState.decode(it) != null }?.let { save(ctx, it) }
            } finally { buffer.release() }
        }
        return WearState.decode(ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString("json", null))
    }
}
