package com.clockweather.app.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.clockweather.core.weather.ChartSeries
import com.clockweather.core.weather.DayChart
import com.clockweather.core.weather.Metric

/**
 * The animated hourly chart of one metric. Temperature and humidity draw a smooth curve that is revealed from start to end with a
 * filled area beneath; rain draws bars that grow up. The animation restarts whenever [animKey] changes (another day, metric or place).
 * It only draws what :core DayChart prepared.
 */
@Composable
fun HourChart(series: ChartSeries, nowIndex: Int?, color: Color, animKey: Any, labelFor: (Int) -> String = { "%02d".format(it) }) {
    val progress = remember(animKey) { Animatable(0f) }
    LaunchedEffect(animKey) { progress.snapTo(0f); progress.animateTo(1f, tween(1000, easing = FastOutSlowInEasing)) }
    val ink = MaterialTheme.colorScheme.onSurface
    val grid = ink.copy(alpha = 0.12f)
    val paint = remember { android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply { textAlign = android.graphics.Paint.Align.CENTER } }
    Canvas(Modifier.fillMaxWidth().height(210.dp)) {
        val n = series.values.size
        if (n == 0) return@Canvas
        val padL = 14.dp.toPx(); val padR = 14.dp.toPx(); val top = 28.dp.toPx(); val bottom = size.height - 26.dp.toPx()
        val plotW = size.width - padL - padR
        fun x(i: Int) = padL + if (n == 1) plotW / 2f else plotW * i / (n - 1f)
        fun y(f: Float) = bottom - (bottom - top) * f
        val p = progress.value
        // grid
        for (k in 0..2) drawLine(grid, Offset(padL, y(k / 2f)), Offset(size.width - padR, y(k / 2f)), strokeWidth = 1.dp.toPx())
        when (series.metric) {
            Metric.RAIN -> {
                val barW = (plotW / n) * 0.62f
                for (i in 0 until n) {
                    val f = series.fraction(i) ?: continue
                    val h = (bottom - top) * f * p
                    drawRoundRect(color, Offset(x(i) - barW / 2f, bottom - h), Size(barW, h.coerceAtLeast(1f)), CornerRadius(barW / 3f))
                }
            }
            else -> {
                val pts = (0 until n).mapNotNull { i -> series.fraction(i)?.let { Offset(x(i), y(it)) } }
                if (pts.size >= 2) {
                    val line = Path().apply {
                        moveTo(pts[0].x, pts[0].y)
                        for (i in 1 until pts.size) { val mx = (pts[i - 1].x + pts[i].x) / 2f; cubicTo(mx, pts[i - 1].y, mx, pts[i].y, pts[i].x, pts[i].y) }
                    }
                    val area = Path().apply { addPath(line); lineTo(pts.last().x, bottom); lineTo(pts.first().x, bottom); close() }
                    clipRect(left = 0f, top = 0f, right = padL + plotW * p + 2f, bottom = size.height) {
                        drawPath(area, Brush.verticalGradient(listOf(color.copy(alpha = 0.35f), color.copy(alpha = 0.02f)), startY = top, endY = bottom))
                        drawPath(line, color, style = Stroke(width = 3.dp.toPx()))
                    }
                    // dots + value labels at the lowest and highest point, appearing near the end of the reveal
                    val show = ((p - 0.7f) / 0.3f).coerceIn(0f, 1f)
                    if (show > 0f) {
                        val minI = (0 until n).filter { series.values[it] != null }.minByOrNull { series.values[it]!! }
                        val maxI = (0 until n).filter { series.values[it] != null }.maxByOrNull { series.values[it]!! }
                        paint.color = ink.copy(alpha = show).toArgb(); paint.textSize = 12.dp.toPx()
                        listOfNotNull(minI, maxI).distinct().forEach { i ->
                            val f = series.fraction(i) ?: return@forEach
                            drawCircle(color.copy(alpha = show), 4.dp.toPx(), Offset(x(i), y(f)))
                            drawIntoCanvas { it.nativeCanvas.drawText(DayChart.label(series.metric, series.values[i]), x(i), y(f) - 9.dp.toPx(), paint) }
                        }
                    }
                }
            }
        }
        // "now" marker
        if (nowIndex != null) drawLine(ink.copy(alpha = 0.45f * p), Offset(x(nowIndex), top - 6.dp.toPx()), Offset(x(nowIndex), bottom), strokeWidth = 1.5.dp.toPx())
        // hour labels every 3 hours
        paint.color = ink.copy(alpha = 0.7f).toArgb(); paint.textSize = 11.dp.toPx()
        for (i in 0 until n step 3) drawIntoCanvas { it.nativeCanvas.drawText(labelFor(series.hours[i]), x(i), size.height - 6.dp.toPx(), paint) }
    }
}
