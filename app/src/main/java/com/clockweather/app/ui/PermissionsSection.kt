package com.clockweather.app.ui

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.clockweather.app.R

/**
 * One place that lists every permission the app can use, why, and its state, with "allow" or "open settings".
 * Everything here is optional: the widget shows clock and date with none of them. Re-reads the state when the user comes back from system settings.
 */
@Composable
fun PermissionsSection() {
    val ctx = LocalContext.current
    var refresh by remember { mutableIntStateOf(0) }
    val owner = LocalLifecycleOwner.current
    DisposableEffect(owner) {
        val obs = LifecycleEventObserver { _, e -> if (e == Lifecycle.Event.ON_RESUME) refresh++ }
        owner.lifecycle.addObserver(obs)
        onDispose { owner.lifecycle.removeObserver(obs) }
    }
    val location = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { refresh++ }
    val calendar = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { ok ->
        if (ok) com.clockweather.app.data.CalendarChangeWorker.schedule(ctx)
        refresh++
    }
    val steps = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { ok ->
        if (ok) com.clockweather.app.data.StepWorker.schedule(ctx)
        refresh++
    }
    val notif = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { refresh++ }
    val granted = refresh.let { listOf(has(ctx, Manifest.permission.ACCESS_COARSE_LOCATION), has(ctx, Manifest.permission.READ_CALENDAR), exactAlarms(ctx), canInstall(ctx), com.clockweather.app.data.StepSource.granted(ctx), com.clockweather.app.data.Notifier.canPost(ctx)) }
    SectionCard(stringResource(R.string.sec_permissions), stringResource(R.string.sec_permissions_sum, granted.count { it }, granted.size)) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(stringResource(R.string.permissions_hint), style = MaterialTheme.typography.bodySmall)
            PermRow(stringResource(R.string.perm_location), stringResource(R.string.perm_location_why), granted[0], { location.launch(Manifest.permission.ACCESS_COARSE_LOCATION) }) { appSettings(ctx) }
            PermRow(stringResource(R.string.perm_calendar), stringResource(R.string.perm_calendar_why), granted[1], { calendar.launch(Manifest.permission.READ_CALENDAR) }) { appSettings(ctx) }
            PermRow(stringResource(R.string.perm_exact), stringResource(R.string.perm_exact_why), granted[2], null) { exactSettings(ctx) }
            PermRow(stringResource(R.string.perm_steps), stringResource(R.string.perm_steps_why), granted[4], { steps.launch(Manifest.permission.ACTIVITY_RECOGNITION) }) { appSettings(ctx) }
            PermRow(stringResource(R.string.perm_notifications), stringResource(R.string.perm_notifications_why), granted[5], { notif.launch(Manifest.permission.POST_NOTIFICATIONS) }) { appSettings(ctx) }
            PermRow(stringResource(R.string.perm_install), stringResource(R.string.perm_install_why), granted[3], null) { installSettings(ctx) }
        }
    }
}

@Composable
private fun PermRow(title: String, why: String, granted: Boolean, allow: (() -> Unit)?, openSettings: () -> Unit) {
    Column(Modifier.fillMaxWidth()) {
        Text((if (granted) "✓ " else "○ ") + title, style = MaterialTheme.typography.titleSmall)
        Text(why, style = MaterialTheme.typography.bodySmall)
        if (!granted) Row(Modifier.padding(top = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            if (allow != null) OutlinedButton(onClick = allow, modifier = Modifier.heightIn(min = 48.dp)) { Text(stringResource(R.string.perm_allow)) }
            OutlinedButton(onClick = openSettings, modifier = Modifier.heightIn(min = 48.dp)) { Text(stringResource(R.string.perm_open_settings)) }
        }
    }
}

private fun has(ctx: Context, p: String) = ContextCompat.checkSelfPermission(ctx, p) == PackageManager.PERMISSION_GRANTED

private fun exactAlarms(ctx: Context): Boolean =
    Build.VERSION.SDK_INT < Build.VERSION_CODES.S || (ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager).canScheduleExactAlarms()

private fun canInstall(ctx: Context): Boolean = ctx.packageManager.canRequestPackageInstalls()

private fun open(ctx: Context, intent: Intent) {
    runCatching { ctx.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
        .onFailure { appSettings(ctx) }
}

private fun appSettings(ctx: Context) =
    runCatching { ctx.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${ctx.packageName}")).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }

private fun exactSettings(ctx: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) open(ctx, Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM, Uri.parse("package:${ctx.packageName}"))) else appSettings(ctx)
}

private fun installSettings(ctx: Context) = open(ctx, Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:${ctx.packageName}")))
