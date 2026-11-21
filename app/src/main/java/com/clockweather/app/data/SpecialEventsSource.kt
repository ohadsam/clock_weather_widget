package com.clockweather.app.data

import android.content.Context
import android.provider.CalendarContract
import com.clockweather.core.agenda.SpecialEvents
import java.time.Instant
import java.time.ZoneId

/**
 * Birthdays, anniversaries and other special events from the Special Events app (ohadsam/special_events). That app can write them into its own
 * local device calendar (account "special_events", titled "אירועים מיוחדים"); ClockWise reads that calendar with the calendar permission it already
 * has, so no new permission and no link between the apps' databases. Without the calendar there is simply nothing to show.
 * Contract: docs/SPECIAL_EVENTS_INTEGRATION.md.
 */
object SpecialEventsSource {
    const val PACKAGE = "com.ohadsam.specialevents"
    private const val ACCOUNT_NAME = "special_events"

    fun installed(ctx: Context): Boolean = ctx.packageManager.getLaunchIntentForPackage(PACKAGE) != null

    fun launchIntent(ctx: Context) = ctx.packageManager.getLaunchIntentForPackage(PACKAGE)

    /** The id of the Special Events calendar, or null when the app never exported to the calendar (or the permission is missing). */
    fun calendarId(ctx: Context): Long? {
        if (!CalendarSource.granted(ctx)) return null
        return runCatching {
            ctx.contentResolver.query(
                CalendarContract.Calendars.CONTENT_URI, arrayOf(CalendarContract.Calendars._ID),
                "${CalendarContract.Calendars.ACCOUNT_NAME} = ? AND ${CalendarContract.Calendars.ACCOUNT_TYPE} = ?", arrayOf(ACCOUNT_NAME, CalendarContract.ACCOUNT_TYPE_LOCAL), null,
            )?.use { c -> if (c.moveToFirst()) c.getLong(0) else null }
        }.getOrNull()
    }

    const val PERMISSION = "com.ohadsam.specialevents.permission.READ_UPCOMING"
    private const val PROVIDER = "content://com.ohadsam.specialevents.public/upcoming"

    /** True when the user granted ClockWise the other app's read permission (its provider is then preferred over the calendar). */
    fun providerGranted(ctx: Context): Boolean = ctx.checkSelfPermission(PERMISSION) == android.content.pm.PackageManager.PERMISSION_GRANTED

    private fun fromProvider(ctx: Context, today: java.time.LocalDate, withinDays: Int): List<SpecialEvents.Item> {
        if (!providerGranted(ctx)) return emptyList()
        return runCatching {
            ctx.contentResolver.query(android.net.Uri.parse("$PROVIDER?days=$withinDays"), null, null, null, null)?.use { c ->
                val t = c.getColumnIndexOrThrow("title"); val ty = c.getColumnIndexOrThrow("type"); val d = c.getColumnIndexOrThrow("epoch_day")
                buildList { while (c.moveToNext() && size < 50) SpecialEvents.fromRow(c.getString(t) ?: "", c.getString(ty) ?: "", c.getLong(d), today)?.let { add(it) } }
            } ?: emptyList()
        }.getOrDefault(emptyList())
    }

    fun upcoming(ctx: Context, now: Instant, zone: ZoneId, withinDays: Int): List<SpecialEvents.Item> {
        val today0 = now.atZone(zone).toLocalDate()
        fromProvider(ctx, today0, withinDays).takeIf { it.isNotEmpty() }?.let { return it.sortedBy { i -> i.daysLeft } }
        val id = calendarId(ctx) ?: return emptyList()
        val today = now.atZone(zone).toLocalDate()
        val from = today.atStartOfDay(zone).toInstant().toEpochMilli()
        val to = today.plusDays(withinDays.toLong() + 1).atStartOfDay(zone).toInstant().toEpochMilli()
        return SpecialEvents.upcoming(CalendarSource.range(ctx, listOf(id), from, to, zone), today, zone, withinDays)
    }
}
