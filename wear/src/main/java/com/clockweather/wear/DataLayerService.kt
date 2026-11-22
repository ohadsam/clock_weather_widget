package com.clockweather.wear

import androidx.wear.tiles.TileService
import com.clockweather.core.wear.WearState
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.WearableListenerService

/** Receives the phone's state (weather + countdowns), keeps the latest copy and asks the status tile to redraw. */
class DataLayerService : WearableListenerService() {
    override fun onDataChanged(events: DataEventBuffer) {
        for (e in events) {
            if (e.type != DataEvent.TYPE_CHANGED || e.dataItem.uri.path != WearState.PATH) continue
            val text = e.dataItem.data?.toString(Charsets.UTF_8) ?: continue
            if (WearState.decode(text) == null) continue
            StateStore.save(this, text)
            TileService.getUpdater(this).requestUpdate(StatusTileService::class.java)
        }
    }
}
