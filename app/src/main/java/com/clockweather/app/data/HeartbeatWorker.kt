package com.clockweather.app.data

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.clockweather.app.widget.ClockWidgetProvider
import java.util.concurrent.TimeUnit

/**
 * Safety net for the minute tick: every 15 minutes (no network needed) it re-arms the alarm chain and, if the last tick is more than
 * 2 minutes old, redraws the widgets. WorkManager survives reboots and app updates, so a lost alarm never leaves the clock frozen.
 */
class HeartbeatWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    override fun doWork(): Result {
        if (ClockWidgetProvider.hasWidgets(applicationContext)) ClockWidgetProvider.repair(applicationContext, onlyIfStale = true)
        return Result.success()
    }

    companion object {
        fun schedule(ctx: Context) {
            WorkManager.getInstance(ctx).enqueueUniquePeriodicWork("heartbeat", ExistingPeriodicWorkPolicy.KEEP, PeriodicWorkRequestBuilder<HeartbeatWorker>(15, TimeUnit.MINUTES).build())
        }
    }
}
