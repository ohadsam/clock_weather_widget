package com.clockweather.wear

import androidx.wear.protolayout.ColorBuilders.argb
import androidx.wear.protolayout.DimensionBuilders.sp
import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.ResourceBuilders
import androidx.wear.protolayout.TimelineBuilders
import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.TileBuilders
import androidx.wear.tiles.TileService
import com.clockweather.core.hebrew.DateCardText
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import java.time.LocalDate
import java.time.ZoneId
import java.util.Locale

/** A date card for the watch: weekday and date, the Hebrew date, then the holiday or the weekly portion. Redraws itself after midnight. */
class DateTileService : TileService() {
    private fun line(text: String, size: Float, color: Int, bold: Boolean = false) = LayoutElementBuilders.Text.Builder()
        .setText(text)
        .setMaxLines(2)
        .setFontStyle(
            LayoutElementBuilders.FontStyle.Builder().setSize(sp(size)).setColor(argb(color))
                .setWeight(if (bold) LayoutElementBuilders.FONT_WEIGHT_BOLD else LayoutElementBuilders.FONT_WEIGHT_NORMAL).build(),
        ).build()

    override fun onTileRequest(requestParams: RequestBuilders.TileRequest): ListenableFuture<TileBuilders.Tile> {
        val zone = ZoneId.systemDefault()
        val hebrew = Locale.getDefault().language == "he" || Locale.getDefault().language == "iw"
        val card = DateCardText.of(LocalDate.now(zone), zone.id == "Asia/Jerusalem", hebrew)
        val column = LayoutElementBuilders.Column.Builder()
            .setHorizontalAlignment(LayoutElementBuilders.HORIZONTAL_ALIGN_CENTER)
            .addContent(line(card.gregorian, 16f, 0xFFCCCCCC.toInt()))
            .addContent(line(card.hebrewDate, 22f, 0xFFFFFFFF.toInt(), bold = true))
        card.extra?.let { column.addContent(line(it, 16f, 0xFFF2B84B.toInt())) }
        val layout = LayoutElementBuilders.Box.Builder()
            .setHorizontalAlignment(LayoutElementBuilders.HORIZONTAL_ALIGN_CENTER)
            .setVerticalAlignment(LayoutElementBuilders.VERTICAL_ALIGN_CENTER)
            .addContent(column.build()).build()
        return Futures.immediateFuture(
            TileBuilders.Tile.Builder()
                .setResourcesVersion(RES_VERSION)
                .setFreshnessIntervalMillis(DateCardText.untilMidnightMs(System.currentTimeMillis(), zone) + 1_000L)
                .setTileTimeline(TimelineBuilders.Timeline.fromLayoutElement(layout)).build(),
        )
    }

    override fun onTileResourcesRequest(requestParams: RequestBuilders.ResourcesRequest): ListenableFuture<ResourceBuilders.Resources> =
        Futures.immediateFuture(ResourceBuilders.Resources.Builder().setVersion(RES_VERSION).build())

    private companion object { const val RES_VERSION = "1" }
}
