package com.clockweather.app.data

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.clockweather.app.widget.ClockWidgetProvider
import com.clockweather.app.widget.TickScheduler
import java.util.concurrent.TimeUnit

/** Refreshes forecasts every 30 minutes (and on demand) and redraws the widgets. */
class WeatherWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    override fun doWork(): Result {
        val settings = SettingsStore.load(applicationContext)
        if (settings.places.isNotEmpty()) WeatherRepository.refreshAll(applicationContext, settings)
        ClockWidgetProvider.updateAll(applicationContext)
        RainAlerts.check(applicationContext, settings)
        // Heartbeat: every refresh also repairs the minute-tick chain and the calendar watcher if either was lost.
        if (ClockWidgetProvider.hasWidgets(applicationContext)) {
            TickScheduler.scheduleNext(applicationContext)
            CalendarChangeWorker.schedule(applicationContext)
        }
        return Result.success()
    }

    companion object {
        private val NETWORK = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()

        fun schedule(ctx: Context) {
            val req = PeriodicWorkRequestBuilder<WeatherWorker>(30, TimeUnit.MINUTES).setConstraints(NETWORK).build()
            WorkManager.getInstance(ctx).enqueueUniquePeriodicWork("weather", ExistingPeriodicWorkPolicy.KEEP, req)
        }

        fun refreshNow(ctx: Context) {
            val req = OneTimeWorkRequestBuilder<WeatherWorker>().setConstraints(NETWORK).build()
            WorkManager.getInstance(ctx).enqueueUniqueWork("weather-now", ExistingWorkPolicy.REPLACE, req)
        }
    }
}
