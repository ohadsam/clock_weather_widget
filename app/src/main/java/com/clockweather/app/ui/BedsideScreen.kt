package com.clockweather.app.ui

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import android.view.WindowManager
import com.clockweather.app.R
import com.clockweather.app.data.AlarmSource
import com.clockweather.app.widget.Snapshot
import com.clockweather.core.alarm.AlarmIndicator
import com.clockweather.core.widget.WidgetSettings
import kotlinx.coroutines.delay
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/** Bedside clock: black screen, big dim time, date and the next alarm; the screen stays on at the lowest brightness; tap to leave. */
@Composable
fun BedsideScreen(settings: WidgetSettings, onBack: () -> Unit) {
    val ctx = LocalContext.current
    val hebrew = Snapshot.isHebrew(ctx, settings.language)
    val activity = ctx as? ComponentActivity
    DisposableEffect(Unit) {
        val window = activity?.window
        val before = window?.attributes?.screenBrightness ?: WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
        fun setBrightness(v: Float) { window?.let { w -> val lp = w.attributes; lp.screenBrightness = v; w.attributes = lp } }
        window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setBrightness(0.03f)
        onDispose {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            setBrightness(before)
        }
    }
    var now by remember { mutableStateOf(Instant.now()) }
    LaunchedEffect(Unit) { while (true) { delay(1000); now = Instant.now() } }
    val zone = ZoneId.systemDefault()
    val local = now.atZone(zone)
    val loc = if (hebrew) Locale("he") else Locale.ENGLISH
    val alarm = AlarmSource.next(ctx)?.takeIf { AlarmIndicator.visible(it, now, 0, settings.alarmPackage) }
    val dim = Color(0xFF8A8A8A)
    Column(
        Modifier.fillMaxSize().background(Color.Black).clickable(onClickLabel = stringResource(R.string.bedside_exit)) { onBack() },
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center,
    ) {
        Text(DateTimeFormatter.ofPattern(if (settings.use24h) "HH:mm" else "h:mm", Locale.ENGLISH).format(local), color = dim, fontSize = 120.sp, textAlign = TextAlign.Center)
        Text(DateTimeFormatter.ofPattern(if (hebrew) "EEEE, d בMMMM" else "EEEE, d MMMM", loc).format(local), color = dim, fontSize = 22.sp)
        if (alarm != null) Text("⏰ " + AlarmIndicator.label(alarm, now, zone, settings.use24h, hebrew), color = dim, fontSize = 20.sp)
    }
}
