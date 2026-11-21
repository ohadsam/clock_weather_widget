package com.clockweather.app.ui

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import com.clockweather.core.timers.SoundKind
import com.clockweather.core.timers.Stopwatch
import com.clockweather.core.timers.TimerFormat
import com.clockweather.core.timers.TimerItem
import com.clockweather.core.timers.TimerList
import kotlinx.coroutines.delay

/** Ticks about 4 times a second while a timer card is visible; every displayed value is derived from the clock, never counted. */
@Composable
private fun rememberNow(): Long {
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) { while (true) { delay(250); now = System.currentTimeMillis() } }
    return now
}

@Composable
internal fun StopwatchTile(st: RenderState, index: Int, animate: Boolean) = TimersCard(st, index, animate, countdown = false)

@Composable
internal fun CountdownTile(st: RenderState, index: Int, animate: Boolean) = TimersCard(st, index, animate, countdown = true)

/** All stopwatches (or all countdowns): add as many as needed, each with its own label, buttons and (countdowns) end-of-time sound and vibration. */
@Composable
private fun TimersCard(st: RenderState, index: Int, animate: Boolean, countdown: Boolean) {
    val ctx = LocalContext.current
    val now = rememberNow()
    val items = remember(now / 250) { TimerStore.items(ctx).filter { it.countdown == countdown } }
    val all = remember(now / 250) { TimerStore.items(ctx) }
    fun refreshWidget() = ClockWidgetProvider.updateAll(ctx)
    var askedNotifications by remember { mutableStateOf(false) }
    val tile = if (countdown) DashTile.COUNTDOWN else DashTile.STOPWATCH
    val permission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }
    DashCard(index, animate, DashTitles.of(tile, st.hebrew), null) {
        if (items.isEmpty()) Text(stringResource(if (countdown) R.string.no_countdowns else R.string.no_stopwatches), style = MaterialTheme.typography.bodyMedium)
        Text(stringResource(if (st.settings.showTimers) R.string.timer_in_widget_hint else R.string.timer_widget_off_hint), style = MaterialTheme.typography.bodySmall)
        items.forEach { item ->
            TimerItemCard(
                item, now, st.hebrew, animate,
                onChange = { new -> TimerStore.update(ctx, item.id) { new }; refreshWidget() },
                onStarted = { started ->
                    TimerStore.update(ctx, item.id) { started }; CountdownAlarm.schedule(ctx, started); refreshWidget()
                    if (!Notifier.canPost(ctx) && !askedNotifications) { askedNotifications = true; permission.launch(Manifest.permission.POST_NOTIFICATIONS) }
                },
                onCancelAlarm = { CountdownAlarm.cancel(ctx, item.id) },
                onDelete = { CountdownAlarm.cancel(ctx, item.id); TimerStore.save(ctx, TimerList.remove(all, item.id)); refreshWidget() },
                onTest = { Notifier.postTimerDone(ctx, item) },
            )
        }
        OutlinedButton(
            onClick = {
                val id = TimerList.nextId(all, System.currentTimeMillis())
                val base = TimerItem(id, countdown)
                TimerStore.save(ctx, TimerList.add(all, if (countdown) base.with(Countdown().withDuration(5 * 60_000L)) else base)); refreshWidget()
            },
            enabled = all.size < TimerList.MAX_ITEMS, modifier = Modifier.heightIn(min = 48.dp),
        ) { Text(stringResource(if (countdown) R.string.add_countdown else R.string.add_stopwatch)) }
    }
}

@Composable
private fun TimerItemCard(
    item: TimerItem, now: Long, hebrew: Boolean, animate: Boolean,
    onChange: (TimerItem) -> Unit, onStarted: (TimerItem) -> Unit, onCancelAlarm: () -> Unit, onDelete: () -> Unit, onTest: () -> Unit,
) {
    var label by remember(item.id) { mutableStateOf(item.label) }
    Column(Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        OutlinedTextField(
            label, { label = it.take(TimerList.MAX_LABEL); onChange(item.copy(label = label)) },
            label = { Text(stringResource(R.string.timer_label)) }, singleLine = true, modifier = Modifier.fillMaxWidth(),
        )
        if (item.countdown) CountdownBody(item, now, animate, hebrew, onChange, onStarted, onCancelAlarm, onTest)
        else StopwatchBody(item, now, onChange)
        TimerButtons(
            running = if (item.countdown) item.countdownState.running else item.stopwatch.running,
            canStart = !item.countdown || item.countdownState.totalMs > 0L,
            onStartPause = {
                if (item.countdown) {
                    val c = item.countdownState
                    if (c.running) { onCancelAlarm(); onChange(item.with(c.pause(now))) } else onStarted(item.with(c.start(now)))
                } else {
                    val s = item.stopwatch
                    onChange(item.with(if (s.running) s.pause(now) else s.start(now)))
                }
            },
            onRestart = {
                if (item.countdown) { onStarted(item.with(item.countdownState.reset().start(now))) } else onChange(item.with(Stopwatch().start(now)))
            },
            onDelete = onDelete,
        )
    }
}

@Composable
private fun StopwatchBody(item: TimerItem, now: Long, onChange: (TimerItem) -> Unit) {
    Text(TimerFormat.clock(item.stopwatch.elapsed(now)), style = MaterialTheme.typography.displaySmall)
    if (item.stopwatch.active && !item.stopwatch.running) Text(stringResource(R.string.paused), style = MaterialTheme.typography.bodySmall)
}

@Composable
private fun CountdownBody(item: TimerItem, now: Long, animate: Boolean, hebrew: Boolean, onChange: (TimerItem) -> Unit, onStarted: (TimerItem) -> Unit, onCancelAlarm: () -> Unit, onTest: () -> Unit) {
    val cd = item.countdownState
    val fraction by animateFloatAsState(cd.fraction(now), tween(if (animate) 300 else 0), label = "ring")
    val color = if (cd.finished) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
    val track = MaterialTheme.colorScheme.surfaceVariant
    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Canvas(Modifier.size(130.dp)) {
            val stroke = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
            val inset = 5.dp.toPx()
            val arc = Size(size.width - 2 * inset, size.height - 2 * inset)
            drawArc(track, 0f, 360f, false, Offset(inset, inset), arc, style = stroke)
            drawArc(color, -90f, 360f * fraction, false, Offset(inset, inset), arc, style = stroke)
        }
        Text(if (cd.finished) stringResource(R.string.countdown_finished) else TimerFormat.clock(cd.remaining(now)), style = MaterialTheme.typography.headlineMedium)
    }
    if (!cd.running) {
        ChipRow(listOf(1, 5, 10, 15, 30, 60).map { it to "$it ${if (hebrew) "דק׳" else "min"}" }, { cd.totalMs == it * 60_000L }, { onChange(item.with(Countdown().withDuration(it * 60_000L))) })
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            for (delta in listOf(-5, -1, 1, 5)) OutlinedButton(
                onClick = { onChange(item.with(Countdown().withDuration((cd.totalMs + delta * 60_000L).coerceAtLeast(60_000L)))) }, modifier = Modifier.heightIn(min = 48.dp),
            ) { Text(if (delta > 0) "+$delta" else "$delta") }
        }
    }
    Text(stringResource(R.string.alert_when_done), style = MaterialTheme.typography.titleSmall)
    ChipRow(SoundKind.values().map { it to stringResource(Notifier.soundLabel(it)) }, { it == item.soundKind }, { onChange(item.copy(sound = it.name)) })
    SwitchRow(stringResource(R.string.vibrate), item.vibrate) { onChange(item.copy(vibrate = it)) }
    OutlinedButton(onClick = onTest, modifier = Modifier.heightIn(min = 48.dp)) { Text(stringResource(R.string.test_alert)) }
}

@Composable
private fun TimerButtons(running: Boolean, canStart: Boolean, onStartPause: () -> Unit, onRestart: () -> Unit, onDelete: () -> Unit) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(onClick = onStartPause, enabled = canStart, modifier = Modifier.heightIn(min = 48.dp)) { Text(stringResource(if (running) R.string.pause else R.string.start)) }
        OutlinedButton(onClick = onRestart, enabled = canStart, modifier = Modifier.heightIn(min = 48.dp)) { Text(stringResource(R.string.restart)) }
        OutlinedButton(onClick = onDelete, modifier = Modifier.heightIn(min = 48.dp)) { Text(stringResource(R.string.cancel_timer)) }
    }
}
