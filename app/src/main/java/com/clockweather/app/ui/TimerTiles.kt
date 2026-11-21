package com.clockweather.app.ui

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.clockweather.app.R
import com.clockweather.app.data.CountdownAlarm
import com.clockweather.app.data.Notifier
import com.clockweather.app.data.TimerStore
import com.clockweather.app.widget.ClockWidgetProvider
import com.clockweather.app.widget.RenderState
import com.clockweather.core.dashboard.DashTile
import com.clockweather.core.timers.Countdown
import com.clockweather.core.timers.Stopwatch
import com.clockweather.core.timers.TimerFormat
import kotlinx.coroutines.delay

/** Re-reads the stored timers about 4 times a second while the card is visible, so what you see is always derived from the clock. */
@Composable
private fun rememberNow(): Long {
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) { while (true) { delay(250); now = System.currentTimeMillis() } }
    return now
}

@Composable
internal fun StopwatchTile(st: RenderState, index: Int, animate: Boolean) {
    val ctx = LocalContext.current
    val now = rememberNow()
    val sw = TimerStore.stopwatch(ctx)
    fun set(s: Stopwatch) { TimerStore.save(ctx, s); ClockWidgetProvider.updateAll(ctx) }
    DashCard(index, animate, DashTitles.of(DashTile.STOPWATCH, st.hebrew), null) {
        Text(TimerFormat.clock(sw.elapsed(now)), style = MaterialTheme.typography.displayMedium)
        Text(stringResource(if (st.settings.showTimers) R.string.timer_in_widget_hint else R.string.timer_widget_off_hint), style = MaterialTheme.typography.bodySmall)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (sw.running) Button(onClick = { set(sw.pause(now)) }, modifier = Modifier.heightIn(min = 48.dp)) { Text(stringResource(R.string.pause)) }
            else Button(onClick = { set(sw.start(now)) }, modifier = Modifier.heightIn(min = 48.dp)) { Text(stringResource(R.string.start)) }
            OutlinedButton(onClick = { set(sw.reset()) }, enabled = sw.active, modifier = Modifier.heightIn(min = 48.dp)) { Text(stringResource(R.string.reset)) }
        }
    }
}

@Composable
internal fun CountdownTile(st: RenderState, index: Int, animate: Boolean) {
    val ctx = LocalContext.current
    val now = rememberNow()
    val cd = TimerStore.countdown(ctx)
    var pendingStart by remember { mutableStateOf(false) }
    fun set(c: Countdown) { TimerStore.save(ctx, c); ClockWidgetProvider.updateAll(ctx) }
    fun start() {
        val c = cd.start(System.currentTimeMillis())
        set(c)
        if (c.running) CountdownAlarm.schedule(ctx, c.endsAtMs)
    }
    val permission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { start() } // start whatever the answer: the widget row still works
    val fraction by animateFloatAsState(cd.fraction(now), tween(if (animate) 300 else 0), label = "ring")
    val color = if (cd.finished) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
    val track = MaterialTheme.colorScheme.surfaceVariant
    DashCard(index, animate, DashTitles.of(DashTile.COUNTDOWN, st.hebrew), null) {
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Canvas(Modifier.size(150.dp)) {
                val stroke = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                val inset = 6.dp.toPx()
                val arcSize = Size(size.width - 2 * inset, size.height - 2 * inset)
                drawArc(track, 0f, 360f, false, Offset(inset, inset), arcSize, style = stroke)
                drawArc(color, -90f, 360f * fraction, false, Offset(inset, inset), arcSize, style = stroke)
            }
            Text(if (cd.finished) stringResource(R.string.countdown_finished) else TimerFormat.clock(cd.remaining(now)), style = MaterialTheme.typography.headlineMedium)
        }
        if (!cd.running) {
            val presets = listOf(1, 5, 10, 15, 30, 60)
            ChipRow(presets.map { it to "$it ${if (st.hebrew) "דק׳" else "min"}" }, { cd.totalMs == it * 60_000L }, { set(cd.clear().withDuration(it * 60_000L)) })
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 4.dp)) {
            if (cd.running) Button(onClick = { set(cd.pause(now)); CountdownAlarm.cancel(ctx) }, modifier = Modifier.heightIn(min = 48.dp)) { Text(stringResource(R.string.pause)) }
            else Button(
                onClick = { if (!Notifier.canPost(ctx) && !pendingStart) { pendingStart = true; permission.launch(Manifest.permission.POST_NOTIFICATIONS) } else start() },
                enabled = cd.totalMs > 0L, modifier = Modifier.heightIn(min = 48.dp),
            ) { Text(stringResource(R.string.start)) }
            OutlinedButton(onClick = { set(cd.reset()); CountdownAlarm.cancel(ctx) }, enabled = cd.active, modifier = Modifier.heightIn(min = 48.dp)) { Text(stringResource(R.string.reset)) }
            OutlinedButton(onClick = { set(cd.clear()); CountdownAlarm.cancel(ctx) }, enabled = cd.active, modifier = Modifier.heightIn(min = 48.dp)) { Text(stringResource(R.string.clear)) }
        }
    }
}
