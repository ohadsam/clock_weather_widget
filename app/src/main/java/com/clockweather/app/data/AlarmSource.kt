package com.clockweather.app.data

import android.app.AlarmManager
import android.content.Context
import com.clockweather.core.alarm.NextAlarm

/** The system's next alarm (every alarm app that uses AlarmManager.setAlarmClock, such as SmartRing, shows up here). */
object AlarmSource {
    fun next(ctx: Context): NextAlarm? {
        val am = ctx.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return null
        val info = am.nextAlarmClock ?: return null
        return NextAlarm(info.triggerTime, info.showIntent?.creatorPackage)
    }

    /** Alarm apps worth offering by name in Settings (package, label resource handled by the UI). */
    val KNOWN_PACKAGES = listOf("com.smartring.app", "com.google.android.deskclock", "com.sec.android.app.clockpackage")
}
