package com.clockweather.core.clock

/**
 * Is the home-screen clock being kept up to date? [lastTickMs] is when the minute alarm last redrew the widget.
 * Pure: the app passes what it measured; the Settings "Widget status" card shows the verdict and offers a repair.
 */
object WidgetHealth {
    enum class State { NO_WIDGET, OK, LATE, STOPPED, NO_EXACT_ALARM }

    /** A tick is due every minute; a few seconds of Doze/launcher delay is normal, more than 3 minutes means the chain broke. */
    fun assess(hasWidgets: Boolean, lastTickMs: Long, nowMs: Long, exactAlarmsAllowed: Boolean): State = when {
        !hasWidgets -> State.NO_WIDGET
        lastTickMs <= 0L || nowMs - lastTickMs > 3 * TickMath.MINUTE_MS -> State.STOPPED
        !exactAlarmsAllowed -> State.NO_EXACT_ALARM
        nowMs - lastTickMs > 90_000L -> State.LATE
        else -> State.OK
    }
}
