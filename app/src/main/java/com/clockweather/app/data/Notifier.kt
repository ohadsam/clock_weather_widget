package com.clockweather.app.data

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Notification
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import com.clockweather.app.MainActivity
import com.clockweather.app.R

/** The only place that posts notifications (countdown finished, rain about to start). Needs the notification permission on Android 13+; silently does nothing without it. */
object Notifier {
    const val CH_TIMER = "timer"
    const val CH_RAIN = "rain"

    fun canPost(ctx: Context): Boolean =
        Build.VERSION.SDK_INT < 33 || ctx.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED

    @SuppressLint("MissingPermission")
    fun post(ctx: Context, channel: String, id: Int, title: String, text: String, screen: String = "home") {
        if (!canPost(ctx)) return
        val nm = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val name = ctx.getString(if (channel == CH_TIMER) R.string.channel_timer else R.string.channel_rain)
        nm.createNotificationChannel(NotificationChannel(channel, name, NotificationManager.IMPORTANCE_HIGH))
        val open = PendingIntent.getActivity(
            ctx, id, Intent(ctx, MainActivity::class.java).putExtra(MainActivity.EXTRA_SCREEN, screen).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
        val n = Notification.Builder(ctx, channel)
            .setSmallIcon(if (channel == CH_TIMER) android.R.drawable.ic_lock_idle_alarm else android.R.drawable.ic_dialog_info)
            .setContentTitle(title).setContentText(text).setContentIntent(open).setAutoCancel(true).build()
        nm.notify(id, n)
    }
}
