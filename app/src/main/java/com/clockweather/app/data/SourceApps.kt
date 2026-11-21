package com.clockweather.app.data

import android.app.AlarmManager
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.AlarmClock
import android.provider.CalendarContract

/** Opens the apps the dashboard reads from (calendar, alarm app). Every call is safe: false when nothing on the phone can handle it. */
object SourceApps {
    fun calendarIntent(): Intent = Intent(Intent.ACTION_VIEW, CalendarContract.CONTENT_URI.buildUpon().appendPath("time").build())

    fun openCalendar(ctx: Context): Boolean = start(ctx, calendarIntent())

    fun openEvent(ctx: Context, eventId: Long): Boolean =
        start(ctx, Intent(Intent.ACTION_VIEW, ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId)))

    /** The app that owns the next alarm (SmartRing, Clock, ...) when known, else the system's alarm list. */
    fun openAlarmApp(ctx: Context): Boolean {
        val show = (ctx.getSystemService(Context.ALARM_SERVICE) as? AlarmManager)?.nextAlarmClock?.showIntent
        if (show != null && runCatching { show.send(); true }.getOrDefault(false)) return true
        return start(ctx, Intent(AlarmClock.ACTION_SHOW_ALARMS))
    }

    /** What the widget fires for the alarm row: the owning alarm app's own "show" intent when known, else the system alarm list. */
    fun alarmPending(ctx: Context): android.app.PendingIntent? =
        (ctx.getSystemService(Context.ALARM_SERVICE) as? AlarmManager)?.nextAlarmClock?.showIntent
            ?: android.app.PendingIntent.getActivity(ctx, 7001, Intent(AlarmClock.ACTION_SHOW_ALARMS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK), android.app.PendingIntent.FLAG_IMMUTABLE)

    fun openDateSettings(ctx: Context): Boolean = start(ctx, Intent(android.provider.Settings.ACTION_DATE_SETTINGS))

    fun openUrl(ctx: Context, url: String): Boolean = start(ctx, Intent(Intent.ACTION_VIEW, Uri.parse(url)))

    private fun start(ctx: Context, intent: Intent): Boolean =
        runCatching { ctx.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)); true }.getOrDefault(false)
}
