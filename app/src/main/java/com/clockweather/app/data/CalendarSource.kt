package com.clockweather.app.data

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.provider.CalendarContract
import com.clockweather.core.agenda.AgendaEvent
import java.time.Instant
import java.time.ZoneId

/** Read-only device calendar (Google Calendar syncs into it, as does any calendar account on the phone). Needs READ_CALENDAR; returns empty without it. */
object CalendarSource {
    data class Cal(val id: Long, val name: String, val account: String)

    fun granted(ctx: Context): Boolean =
        ctx.checkSelfPermission(Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED

    fun calendars(ctx: Context): List<Cal> {
        if (!granted(ctx)) return emptyList()
        val out = ArrayList<Cal>()
        runCatching {
            ctx.contentResolver.query(
                CalendarContract.Calendars.CONTENT_URI,
                arrayOf(CalendarContract.Calendars._ID, CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, CalendarContract.Calendars.ACCOUNT_NAME),
                null, null, CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
            )?.use { c ->
                while (c.moveToNext() && out.size < 40) out += Cal(c.getLong(0), c.getString(1) ?: "", c.getString(2) ?: "")
            }
        }
        return out
    }

    /** Instances from now to the end of the local day; [calendarIds] empty = every visible calendar. */
    fun today(ctx: Context, calendarIds: List<Long>, now: Instant, zone: ZoneId): List<AgendaEvent> {
        val start = now.toEpochMilli()
        val end = now.atZone(zone).toLocalDate().plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()
        return range(ctx, calendarIds, start, end, zone, 60)
    }

    /** Instances between two moments (all-day events are mapped to the local day); [calendarIds] empty = every visible calendar. */
    fun range(ctx: Context, calendarIds: List<Long>, startMs: Long, endMs: Long, zone: ZoneId, limit: Int = 300): List<AgendaEvent> {
        if (!granted(ctx)) return emptyList()
        val start = startMs
        val end = endMs
        val uri = CalendarContract.Instances.CONTENT_URI.buildUpon().also {
            ContentUris.appendId(it, start - 12 * 3_600_000L) // include all-day events that began earlier today
            ContentUris.appendId(it, end)
        }.build()
        val out = ArrayList<AgendaEvent>()
        runCatching {
            ctx.contentResolver.query(
                uri,
                arrayOf(
                    CalendarContract.Instances.EVENT_ID, CalendarContract.Instances.TITLE, CalendarContract.Instances.BEGIN,
                    CalendarContract.Instances.END, CalendarContract.Instances.ALL_DAY, CalendarContract.Instances.EVENT_LOCATION,
                    CalendarContract.Instances.CALENDAR_ID,
                ),
                null, null, CalendarContract.Instances.BEGIN,
            )?.use { c ->
                while (c.moveToNext() && out.size < limit) {
                    val calId = c.getLong(6)
                    if (calendarIds.isNotEmpty() && calId !in calendarIds) continue
                    val allDay = c.getInt(4) == 1
                    var b = c.getLong(2)
                    var e = c.getLong(3)
                    if (allDay) { // all-day instances are stored in UTC midnight: map to the local day
                        val d = Instant.ofEpochMilli(b).atZone(ZoneId.of("UTC")).toLocalDate()
                        b = d.atStartOfDay(zone).toInstant().toEpochMilli()
                        e = d.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()
                    }
                    out += AgendaEvent(c.getLong(0), c.getString(1) ?: "", b, e, allDay, c.getString(5))
                }
            }
        }
        return out
    }
}
