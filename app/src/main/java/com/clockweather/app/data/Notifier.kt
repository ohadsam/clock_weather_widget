package com.clockweather.app.data

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import com.clockweather.app.MainActivity
import com.clockweather.app.R
import com.clockweather.core.timers.SoundKind
import com.clockweather.core.timers.TimerItem

/** The only place that posts notifications (a timer ended, rain about to start). Needs the notification permission on Android 13+; silently does nothing without it. */
object Notifier {
    const val CH_RAIN = "rain"
    private val VIBRATION = longArrayOf(0, 500, 300, 500, 300, 800)

    fun canPost(ctx: Context): Boolean =
        Build.VERSION.SDK_INT < 33 || ctx.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED

    private fun open(ctx: Context, id: Int, screen: String): PendingIntent = PendingIntent.getActivity(
        ctx, id, Intent(ctx, MainActivity::class.java).putExtra(MainActivity.EXTRA_SCREEN, screen).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP),
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
    )

    @SuppressLint("MissingPermission")
    fun post(ctx: Context, channel: String, id: Int, title: String, text: String, screen: String = "home") {
        if (!canPost(ctx)) return
        val nm = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.createNotificationChannel(NotificationChannel(channel, ctx.getString(R.string.channel_rain), NotificationManager.IMPORTANCE_HIGH))
        nm.notify(id, Notification.Builder(ctx, channel).setSmallIcon(android.R.drawable.ic_dialog_info).setContentTitle(title).setContentText(text).setContentIntent(open(ctx, id, screen)).setAutoCancel(true).build())
    }

    /**
     * A countdown ended. The ring and the vibration are per timer: one channel per combination (sound kind x vibrate),
     * created on first use, because Android fixes sound and vibration per channel.
     */
    @SuppressLint("MissingPermission")
    fun postTimerDone(ctx: Context, item: TimerItem) {
        if (!canPost(ctx)) return
        val sound = item.soundKind
        val custom = item.soundUri.takeIf { it.isNotEmpty() }
        val channel = "timer_" + (if (custom != null) "u${custom.hashCode().toUInt()}" else sound.name.lowercase()) + "_${if (item.vibrate) 1 else 0}"
        val nm = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val ch = NotificationChannel(channel, ctx.getString(R.string.channel_timer) + " - " + ctx.getString(soundLabel(sound)) + if (item.vibrate) " + " + ctx.getString(R.string.vibrate) else "", NotificationManager.IMPORTANCE_HIGH)
        if (custom != null) ch.setSound(android.net.Uri.parse(custom), AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build())
        else if (sound == SoundKind.NONE) ch.setSound(null, null) else {
            val type = when (sound) { SoundKind.ALARM -> RingtoneManager.TYPE_ALARM; SoundKind.RINGTONE -> RingtoneManager.TYPE_RINGTONE; else -> RingtoneManager.TYPE_NOTIFICATION }
            val usage = when (sound) { SoundKind.ALARM -> AudioAttributes.USAGE_ALARM; SoundKind.RINGTONE -> AudioAttributes.USAGE_NOTIFICATION_RINGTONE; else -> AudioAttributes.USAGE_NOTIFICATION }
            ch.setSound(RingtoneManager.getDefaultUri(type), AudioAttributes.Builder().setUsage(usage).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build())
        }
        ch.enableVibration(item.vibrate)
        if (item.vibrate) ch.vibrationPattern = VIBRATION
        nm.createNotificationChannel(ch)
        val id = 5000 + (item.id.hashCode() and 0xFFF)
        val title = item.label.ifEmpty { ctx.getString(R.string.countdown_done_title) }
        val n = Notification.Builder(ctx, channel).setSmallIcon(android.R.drawable.ic_lock_idle_alarm).setContentTitle(title)
            .setContentText(ctx.getString(R.string.countdown_done_text)).setContentIntent(open(ctx, id, "home")).setAutoCancel(true).setTimeoutAfter(5 * 60_000L).build()
        if (sound != SoundKind.NONE || custom != null) n.flags = n.flags or Notification.FLAG_INSISTENT // keeps ringing until it is dismissed (or 30 s: the sound is a short loop)
        nm.notify(id, n)
    }

    fun soundLabel(s: SoundKind): Int = when (s) {
        SoundKind.NONE -> R.string.sound_none
        SoundKind.ALARM -> R.string.sound_alarm
        SoundKind.NOTIFICATION -> R.string.sound_notification
        SoundKind.RINGTONE -> R.string.sound_ringtone
    }
}
