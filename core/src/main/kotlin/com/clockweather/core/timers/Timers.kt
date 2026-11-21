package com.clockweather.core.timers

/**
 * Stopwatch (counts up) and countdown (counts down). Pure state machines: every function takes "now" as epoch milliseconds,
 * so what is shown is always derived from timestamps (never from a running counter) and survives the app being closed.
 */
data class Stopwatch(val running: Boolean = false, val startedAtMs: Long = 0L, val accumulatedMs: Long = 0L) {
    val active: Boolean get() = running || accumulatedMs > 0L
    fun elapsed(nowMs: Long): Long = (accumulatedMs + if (running) (nowMs - startedAtMs).coerceAtLeast(0L) else 0L)
    fun start(nowMs: Long): Stopwatch = if (running) this else copy(running = true, startedAtMs = nowMs)
    fun pause(nowMs: Long): Stopwatch = if (!running) this else Stopwatch(false, 0L, elapsed(nowMs))
    fun reset(): Stopwatch = Stopwatch()

    fun encode(): String = "${if (running) 1 else 0}|$startedAtMs|$accumulatedMs"

    companion object {
        fun decode(text: String?): Stopwatch {
            val p = text?.split('|') ?: return Stopwatch()
            if (p.size != 3) return Stopwatch()
            return runCatching { Stopwatch(p[0] == "1", p[1].toLong(), p[2].toLong().coerceAtLeast(0L)) }.getOrDefault(Stopwatch())
        }
    }
}

data class Countdown(
    val totalMs: Long = 0L,
    val running: Boolean = false,
    val endsAtMs: Long = 0L,
    val pausedRemainingMs: Long = 0L,
    val finished: Boolean = false,
) {
    /** Something to show: set up, running, paused, or finished and not yet dismissed. */
    val active: Boolean get() = totalMs > 0L

    fun remaining(nowMs: Long): Long = when {
        finished -> 0L
        running -> (endsAtMs - nowMs).coerceAtLeast(0L)
        else -> pausedRemainingMs
    }

    fun isDue(nowMs: Long): Boolean = running && nowMs >= endsAtMs

    /** Sets the duration (only while not running); the countdown is then paused at the full time. */
    fun withDuration(ms: Long): Countdown = if (running) this else Countdown(totalMs = ms.coerceIn(0L, MAX_MS), pausedRemainingMs = ms.coerceIn(0L, MAX_MS))

    fun start(nowMs: Long): Countdown = when {
        running || totalMs <= 0L -> this
        finished || pausedRemainingMs <= 0L -> copy(finished = false, running = true, pausedRemainingMs = 0L, endsAtMs = nowMs + totalMs)
        else -> copy(running = true, endsAtMs = nowMs + pausedRemainingMs, pausedRemainingMs = 0L)
    }

    fun pause(nowMs: Long): Countdown = if (!running) this else copy(running = false, pausedRemainingMs = remaining(nowMs), endsAtMs = 0L)

    /** Called when the end time has passed: becomes finished (stays visible until reset). */
    fun finish(): Countdown = copy(running = false, finished = true, pausedRemainingMs = 0L, endsAtMs = 0L)

    fun reset(): Countdown = Countdown(totalMs = totalMs, pausedRemainingMs = totalMs)
    fun clear(): Countdown = Countdown()

    /** 0..1 of the time still left, for the progress ring. */
    fun fraction(nowMs: Long): Float = if (totalMs <= 0L) 0f else (remaining(nowMs).toFloat() / totalMs).coerceIn(0f, 1f)

    fun encode(): String = "$totalMs|${if (running) 1 else 0}|$endsAtMs|$pausedRemainingMs|${if (finished) 1 else 0}"

    companion object {
        const val MAX_MS = 24L * 3_600_000L
        fun decode(text: String?): Countdown {
            val p = text?.split('|') ?: return Countdown()
            if (p.size != 5) return Countdown()
            return runCatching { Countdown(p[0].toLong().coerceIn(0L, MAX_MS), p[1] == "1", p[2].toLong(), p[3].toLong().coerceAtLeast(0L), p[4] == "1") }.getOrDefault(Countdown())
        }
    }
}

/** Text for the timers. [clock] is for the app (seconds); [widgetStopwatch]/[widgetCountdown] are for the widget, which redraws once a minute, so they never show seconds. */
object TimerFormat {
    fun clock(ms: Long): String {
        val total = (ms.coerceAtLeast(0L) / 1000L)
        val h = total / 3600; val m = (total % 3600) / 60; val s = total % 60
        return if (h > 0) "%d:%02d:%02d".format(h, m, s) else "%d:%02d".format(m, s)
    }

    private fun minutesLabel(minutes: Long, hebrew: Boolean): String = when {
        minutes >= 60 -> "%d:%02d %s".format(minutes / 60, minutes % 60, if (hebrew) "שע׳" else "h")
        else -> "$minutes ${if (hebrew) "דק׳" else "min"}"
    }

    fun widgetStopwatch(elapsedMs: Long, hebrew: Boolean): String {
        val minutes = elapsedMs.coerceAtLeast(0L) / 60_000L
        return "⏱ " + if (minutes == 0L) (if (hebrew) "פחות מדקה" else "< 1 min") else minutesLabel(minutes, hebrew)
    }

    fun widgetCountdown(c: Countdown, nowMs: Long, hebrew: Boolean): String {
        if (c.finished || c.isDue(nowMs)) return "⏳ " + if (hebrew) "הסתיים" else "Done"
        val left = c.remaining(nowMs)
        val minutes = (left + 59_999L) / 60_000L // round up: "1 min" until it is really over
        return "⏳ " + if (!c.running && left <= 0L) (if (hebrew) "הסתיים" else "Done") else minutesLabel(minutes.coerceAtLeast(1L), hebrew) + if (!c.running) (if (hebrew) " (מושהה)" else " (paused)") else ""
    }
}
