package com.clockweather.core.timers

/**
 * Work / rest rounds ("40 s work, 20 s rest, 8 rounds"). An interval timer is an ordinary countdown whose [phase] advances when each
 * segment ends, so it reuses the ring, the exact alarm and the widget line. Pure: encode/decode clamp every number (the text is stored data).
 * With no rest the rounds simply follow each other; with rest there is no rest after the last round.
 */
data class IntervalPlan(val workSec: Int, val restSec: Int, val rounds: Int, val phase: Int = 0) {
    enum class Kind { WORK, REST }

    private val hasRest get() = restSec > 0
    val phaseCount: Int get() = if (hasRest) rounds * 2 - 1 else rounds
    val kind: Kind get() = if (hasRest && phase % 2 == 1) Kind.REST else Kind.WORK
    val round: Int get() = if (hasRest) phase / 2 + 1 else phase + 1
    val durationMs: Long get() = (if (kind == Kind.WORK) workSec else restSec) * 1000L
    val isLast: Boolean get() = phase >= phaseCount - 1

    fun next(): IntervalPlan? = if (isLast) null else copy(phase = phase + 1)
    fun restarted(): IntervalPlan = copy(phase = 0)
    fun total(): Long = (0 until phaseCount).sumOf { copy(phase = it).durationMs }

    /** "Round 2/8 · Work" */
    fun label(hebrew: Boolean): String = (if (hebrew) "סבב $round/$rounds · " else "Round $round/$rounds · ") + word(kind, hebrew)

    fun encode(): String = "$workSec|$restSec|$rounds|$phase"

    companion object {
        fun word(k: Kind, hebrew: Boolean): String = if (k == Kind.WORK) (if (hebrew) "עבודה" else "Work") else (if (hebrew) "מנוחה" else "Rest")

        fun of(workSec: Int, restSec: Int, rounds: Int): IntervalPlan = IntervalPlan(workSec.coerceIn(5, 3600), restSec.coerceIn(0, 3600), rounds.coerceIn(1, 50))

        fun decode(text: String?): IntervalPlan? {
            val p = text?.takeIf { it.length <= 40 }?.split('|') ?: return null
            if (p.size != 4) return null
            val n = p.map { it.toIntOrNull() ?: return null }
            val plan = of(n[0], n[1], n[2])
            return plan.copy(phase = n[3].coerceIn(0, plan.phaseCount - 1))
        }
    }
}
