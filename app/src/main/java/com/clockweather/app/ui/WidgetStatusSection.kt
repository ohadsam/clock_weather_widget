package com.clockweather.app.ui

import android.app.AlarmManager
import android.content.Context
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.clockweather.app.R
import com.clockweather.app.data.TickState
import com.clockweather.app.widget.ClockWidgetProvider
import com.clockweather.core.clock.WidgetHealth

/** Settings -> Widget status: is the minute tick alive, when it last ran, and a one-tap repair (re-arms the alarm and redraws). */
@Composable
fun WidgetStatusSection() {
    val ctx = LocalContext.current
    var refresh by remember { mutableLongStateOf(0L) }
    val now = System.currentTimeMillis() + refresh * 0
    val last = TickState.last(ctx)
    val state = WidgetHealth.assess(ClockWidgetProvider.hasWidgets(ctx), last, now, exactAllowed(ctx))
    val label = when (state) {
        WidgetHealth.State.NO_WIDGET -> R.string.health_no_widget
        WidgetHealth.State.OK -> R.string.health_ok
        WidgetHealth.State.LATE -> R.string.health_late
        WidgetHealth.State.STOPPED -> R.string.health_stopped
        WidgetHealth.State.NO_EXACT_ALARM -> R.string.health_no_exact
    }
    SectionCard(stringResource(R.string.sec_health), stringResource(label)) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(stringResource(label), style = MaterialTheme.typography.bodyLarge)
            if (last > 0) Text(stringResource(R.string.health_last_tick, ((now - last) / 1000).coerceAtLeast(0)), style = MaterialTheme.typography.bodySmall)
            Text(stringResource(R.string.health_hint), style = MaterialTheme.typography.bodySmall)
            OutlinedButton(onClick = { ClockWidgetProvider.repair(ctx); refresh++ }, modifier = Modifier.heightIn(min = 48.dp)) { Text(stringResource(R.string.health_repair)) }
        }
    }
}

private fun exactAllowed(ctx: Context): Boolean =
    Build.VERSION.SDK_INT < Build.VERSION_CODES.S || (ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager).canScheduleExactAlarms()
