package com.clockweather.app.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import com.clockweather.core.weather.Sky

/**
 * A few soft rain drops or snow flakes drifting over the dashboard weather card (code-drawn, no art needed). Only while animation is on
 * (the owner's "calm animations" switch and the system "remove animations" setting turn it off): [animate] false draws nothing.
 */
@Composable
internal fun WeatherParticles(sky: Sky, animate: Boolean, modifier: Modifier = Modifier) {
    if (!animate || !sky.isWet) return
    val snow = sky == Sky.SNOW
    val t by rememberInfiniteTransition(label = "particles").animateFloat(0f, 1f, infiniteRepeatable(tween(if (snow) 6000 else 1800, easing = LinearEasing), RepeatMode.Restart), label = "t")
    Canvas(modifier) {
        val n = if (snow) 16 else 22
        for (i in 0 until n) {
            val x = ((i * 53) % 100) / 100f * size.width
            val phase = (t + i * 0.137f) % 1f
            val y = phase * (size.height + 20f) - 10f
            if (snow) drawCircle(Color.White.copy(alpha = 0.55f), radius = 2.2f + (i % 3), center = Offset(x + 6f * kotlin.math.sin((phase + i) * 6.28f), y))
            else drawLine(Color(0xFF9CC9F2).copy(alpha = 0.55f), Offset(x, y), Offset(x - 4f, y + 12f), strokeWidth = 2f, cap = StrokeCap.Round)
        }
    }
}
