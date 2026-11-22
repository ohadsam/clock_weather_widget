package com.clockweather.wear

import androidx.wear.protolayout.ColorBuilders.argb
import androidx.wear.protolayout.DimensionBuilders.sp
import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.ResourceBuilders
import androidx.wear.protolayout.TimelineBuilders
import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.TileBuilders
import androidx.wear.tiles.TileService
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import java.util.Locale
import java.util.concurrent.Executors

/** Weather of the active place and the running timers, sent by the phone. Redraws every minute (the system may stretch that) and when new data arrives. */
class StatusTileService : TileService() {
    private val executor = Executors.newSingleThreadExecutor()

    private fun line(text: String, size: Float, color: Int, bold: Boolean = false) = LayoutElementBuilders.Text.Builder()
        .setText(text).setMaxLines(1)
        .setFontStyle(LayoutElementBuilders.FontStyle.Builder().setSize(sp(size)).setColor(argb(color)).setWeight(if (bold) LayoutElementBuilders.FONT_WEIGHT_BOLD else LayoutElementBuilders.FONT_WEIGHT_NORMAL).build())
        .build()

    override fun onTileRequest(requestParams: RequestBuilders.TileRequest): ListenableFuture<TileBuilders.Tile> = Futures.submit<TileBuilders.Tile>({
        val now = System.currentTimeMillis()
        val state = StateStore.load(this)
        val hebrew = state?.hebrew ?: (Locale.getDefault().language.let { it == "he" || it == "iw" })
        val column = LayoutElementBuilders.Column.Builder().setHorizontalAlignment(LayoutElementBuilders.HORIZONTAL_ALIGN_CENTER)
        if (state == null) {
            column.addContent(line(getString(R.string.status_open_phone), 14f, 0xFFCCCCCC.toInt()))
        } else {
            val weather = state.weatherLines()
            weather.forEachIndexed { i, text -> column.addContent(line(text, if (i == 1) 22f else 14f, if (i == 1) 0xFFFFFFFF.toInt() else 0xFFCCCCCC.toInt(), bold = i == 1)) }
            state.timerLines(now).forEach { column.addContent(line(it, 14f, 0xFFF2B84B.toInt())) }
            state.eventLines().forEach { column.addContent(line(it, 13f, 0xFFB7D7F5.toInt())) }
            if (weather.isEmpty() && state.timers.isEmpty() && state.events.isEmpty()) column.addContent(line(getString(R.string.status_nothing), 14f, 0xFFCCCCCC.toInt()))
            if (state.isStale(now)) column.addContent(line(getString(R.string.status_stale), 12f, 0xFF999999.toInt()))
        }
        val layout = LayoutElementBuilders.Box.Builder()
            .setHorizontalAlignment(LayoutElementBuilders.HORIZONTAL_ALIGN_CENTER).setVerticalAlignment(LayoutElementBuilders.VERTICAL_ALIGN_CENTER)
            .addContent(column.build()).build()
        TileBuilders.Tile.Builder().setResourcesVersion(RES_VERSION).setFreshnessIntervalMillis(60_000L)
            .setTileTimeline(TimelineBuilders.Timeline.fromLayoutElement(layout)).build()
    }, executor)

    override fun onTileResourcesRequest(requestParams: RequestBuilders.ResourcesRequest): ListenableFuture<ResourceBuilders.Resources> =
        Futures.immediateFuture(ResourceBuilders.Resources.Builder().setVersion(RES_VERSION).build())

    private companion object { const val RES_VERSION = "1" }
}
