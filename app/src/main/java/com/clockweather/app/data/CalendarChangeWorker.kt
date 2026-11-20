package com.clockweather.app.data

import android.content.Context
import android.provider.CalendarContract
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.clockweather.app.widget.ClockWidgetProvider
import com.clockweather.app.widget.Snapshot
import java.util.concurrent.TimeUnit

/**
 * Wakes when ANY calendar data changes (an event added, moved or deleted, a sync finished), even with the app closed,
 * drops the cached agenda and redraws the widgets, then arms itself again (a content-URI trigger fires once).
 */
class CalendarChangeWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    override fun doWork(): Result {
        Snapshot.invalidateAgenda()
        ClockWidgetProvider.updateAll(applicationContext)
        schedule(applicationContext, replace = true)
        return Result.success()
    }

    companion object {
        /** No-op without calendar permission (nothing to watch). */
        fun schedule(ctx: Context, replace: Boolean = false) {
            if (!CalendarSource.granted(ctx)) return
            val constraints = Constraints.Builder()
                .addContentUriTrigger(CalendarContract.CONTENT_URI, true)
                .setTriggerContentUpdateDelay(2, TimeUnit.SECONDS)
                .setTriggerContentMaxDelay(30, TimeUnit.SECONDS)
                .build()
            val req = OneTimeWorkRequestBuilder<CalendarChangeWorker>().setConstraints(constraints).build()
            WorkManager.getInstance(ctx).enqueueUniqueWork("calendar-watch", if (replace) ExistingWorkPolicy.APPEND_OR_REPLACE else ExistingWorkPolicy.KEEP, req)
        }
    }
}
