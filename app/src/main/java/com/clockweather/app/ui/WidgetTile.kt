package com.clockweather.app.ui

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.clockweather.app.R
import com.clockweather.app.widget.ClockWidgetProvider
import com.clockweather.app.widget.Snapshot
import com.clockweather.app.widget.WidgetRenderer
import com.clockweather.core.widget.ClockStyle
import com.clockweather.core.widget.WidgetSettings
import kotlinx.coroutines.delay

/** Dashboard card with the live widget preview (same renderer as the home-screen widget), size/wallpaper switches, quick style and "add widget". */
@Composable
fun WidgetTile(settings: WidgetSettings, tick: Long, onChange: (WidgetSettings) -> Unit) {
    val ctx = LocalContext.current
    val density = LocalDensity.current.density
    var size by remember { mutableStateOf(1) }
    var darkWall by remember { mutableStateOf(true) }
    val sizes = listOf(Triple(R.string.size_small, 150, 110), Triple(R.string.size_medium, 280, 160), Triple(R.string.size_large, 330, 340))
    val (_, wDp, hDp) = sizes[size]
    val bmp = remember(settings, tick, size) {
        val st = Snapshot.build(ctx)
        WidgetRenderer.render(ctx, (wDp * density).toInt(), (hDp * density).toInt(), density, st)
    }
    val wall = if (darkWall) Brush.verticalGradient(listOf(Color(0xFF1B2A44), Color(0xFF3B5B7E), Color(0xFFE08A4D)))
    else Brush.verticalGradient(listOf(Color(0xFFBFE3F2), Color(0xFFF4EBD8), Color(0xFFFFD9A8)))
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            Modifier.fillMaxWidth().height((hDp + 32).dp).clip(RoundedCornerShape(24.dp)).background(wall),
            contentAlignment = Alignment.Center,
        ) {
            // changing the clock style cross-fades the preview (the key is the style, so the once-a-minute redraw does not flicker)
            androidx.compose.animation.Crossfade(settings.clockStyle, animationSpec = androidx.compose.animation.core.tween(if (settings.calmAnimations) 0 else 350), label = "style") { style ->
                // the style is also the test tag of the preview (UI tests can tell which style is showing)
                Image(bmp.asImageBitmap(), contentDescription = stringResource(R.string.preview_description), modifier = Modifier.height(hDp.dp).testTag("preview_" + style.name))
            }
        }
        ChipRow(sizes.mapIndexed { i, t -> i to stringResource(t.first) }, { it == size }, { size = it })
        ChipRow(listOf(true to stringResource(R.string.wall_dark), false to stringResource(R.string.wall_light)), { it == darkWall }, { darkWall = it })
        Text(stringResource(R.string.clock_style), style = MaterialTheme.typography.titleMedium)
        ChipRow(
            listOf(ClockStyle.FLIP to R.string.style_flip, ClockStyle.DIGITAL to R.string.style_digital, ClockStyle.ANALOG to R.string.style_analog, ClockStyle.MINIMAL to R.string.style_minimal, ClockStyle.POCKET to R.string.style_pocket)
                .map { it.first to stringResource(it.second) },
            { it == settings.clockStyle }, { onChange(settings.copy(clockStyle = it)) },
        )
        Text(stringResource(R.string.presets), style = MaterialTheme.typography.titleMedium)
        val hebrew = Snapshot.isHebrew(ctx, settings.language)
        ChipRow(com.clockweather.core.widget.Preset.values().map { it to (if (hebrew) it.he else it.en) }, { false }, { onChange(it.apply(settings)) })
        val mgr = AppWidgetManager.getInstance(ctx)
        if (mgr.isRequestPinAppWidgetSupported) {
            Button(onClick = { mgr.requestPinAppWidget(ComponentName(ctx, ClockWidgetProvider::class.java), null, null) }, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)) {
                Text(stringResource(R.string.add_widget))
            }
        } else Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Image(painterResource(R.drawable.spot_add_widget), contentDescription = null, modifier = Modifier.height(120.dp))
            Text(stringResource(R.string.add_widget_hint), style = MaterialTheme.typography.bodyMedium)
        }
        Text(stringResource(R.string.widget_types_hint), style = MaterialTheme.typography.bodySmall)
    }
}
