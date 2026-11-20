package com.clockweather.app.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.core.content.ContextCompat
import com.clockweather.core.steps.StepMath
import java.time.LocalDate
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Today's steps from the phone's own step counter (no Google services, nothing leaves the device).
 * [refresh] reads the sensor once (blocking, <= 2 s: call from a worker) and stores the day's state; [today] only reads what was stored.
 */
object StepSource {
    private const val PREFS = "steps"

    fun supported(ctx: Context): Boolean =
        (ctx.getSystemService(Context.SENSOR_SERVICE) as SensorManager).getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null

    fun granted(ctx: Context): Boolean =
        android.os.Build.VERSION.SDK_INT < 29 || ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED

    /** Steps counted today, or null when unsupported, not allowed, or never read. */
    fun today(ctx: Context): Int? {
        if (!granted(ctx)) return null
        val state = StepMath.decode(ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString("state", null)) ?: return null
        return if (state.date == LocalDate.now()) (state.last - state.baseline).coerceAtLeast(0L).toInt() else 0
    }

    fun refresh(ctx: Context): Int? {
        if (!granted(ctx)) return null
        val sm = ctx.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensor = sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) ?: return null
        val latch = CountDownLatch(1)
        var reading = -1L
        val listener = object : SensorEventListener {
            override fun onSensorChanged(e: SensorEvent) { reading = e.values[0].toLong(); latch.countDown() }
            override fun onAccuracyChanged(s: Sensor?, a: Int) = Unit
        }
        return try {
            sm.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL)
            if (!latch.await(2, TimeUnit.SECONDS) || reading < 0) return today(ctx)
            val prefs = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            val r = StepMath.update(StepMath.decode(prefs.getString("state", null)), reading, LocalDate.now())
            prefs.edit().putString("state", StepMath.encode(r.state)).apply()
            r.stepsToday
        } finally { sm.unregisterListener(listener) }
    }
}
