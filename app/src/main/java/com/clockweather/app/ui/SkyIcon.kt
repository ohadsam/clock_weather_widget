package com.clockweather.app.ui

import android.graphics.RectF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.clockweather.app.widget.WeatherArt
import com.clockweather.core.weather.Sky
import kotlinx.coroutines.delay

/** The weather illustration of a sky; with [animate] it plays its 12-frame loop at 8 fps (a Canvas redraw only). */
@Composable
internal fun SkyIcon(sky: Sky, isDay: Boolean, sizeDp: Int = 72, animate: Boolean = false) {
    val ctx = LocalContext.current
    var ms by remember { mutableLongStateOf(0L) }
    if (animate) LaunchedEffect(sky, isDay) { val t0 = System.currentTimeMillis(); while (true) { ms = System.currentTimeMillis() - t0; delay(125) } }
    Canvas(Modifier.size(sizeDp.dp)) {
        drawIntoCanvas { WeatherArt.draw(ctx, it.nativeCanvas, sky, isDay, RectF(0f, 0f, size.width, size.height), if (animate) ms else -1L) }
    }
}
