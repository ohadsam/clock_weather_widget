package com.clockweather.app.data

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.clockweather.app.widget.ClockWidgetProvider
import java.util.concurrent.TimeUnit

/** Every 15 minutes: read the step counter and redraw the widgets (only scheduled while a widget shows steps). No network needed. */
class StepWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    override fun doWork(): Result {
        if (StepSource.refresh(applicationContext) != null) ClockWidgetProvider.updateAll(applicationContext)
        return Result.success()
    }

    companion object {
        fun schedule(ctx: Context) {
            WorkManager.getInstance(ctx).enqueueUniquePeriodicWork("steps", ExistingPeriodicWorkPolicy.KEEP, PeriodicWorkRequestBuilder<StepWorker>(15, TimeUnit.MINUTES).build())
        }

        fun cancel(ctx: Context) { WorkManager.getInstance(ctx).cancelUniqueWork("steps") }
    }
}
