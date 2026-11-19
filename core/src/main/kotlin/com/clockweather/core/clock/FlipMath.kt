package com.clockweather.core.clock

import kotlin.math.PI
import kotlin.math.cos

/**
 * Math of the flip-card animation, shared by the in-app preview and the widget renderer.
 * A card shows the old digit, folds its top half down, and the new digit's top half falls into place.
 * progress p in 0..1: p < 0.5 the old top half is folding (scaleY 1 -> 0); p >= 0.5 the new bottom half unfolds (0 -> 1).
 */
object FlipMath {
    const val DURATION_MS = 450
    const val FRAMES = 8

    /** Which of the four card positions change between two digit sets (only those animate). */
    fun changed(from: IntArray, to: IntArray): BooleanArray = BooleanArray(to.size) { i -> from.getOrNull(i) != to[i] }

    /** Ease in/out so the fold starts and ends softly. */
    fun ease(t: Float): Float = ((1.0 - cos(PI * t.coerceIn(0f, 1f))) / 2.0).toFloat()

    /** Vertical scale of the folding top half of the OLD digit (1 -> 0 during the first half). */
    fun oldTopScale(progress: Float): Float {
        val p = ease(progress)
        return if (p >= 0.5f) 0f else 1f - p * 2f
    }

    /** Vertical scale of the unfolding bottom half of the NEW digit (0 -> 1 during the second half). */
    fun newBottomScale(progress: Float): Float {
        val p = ease(progress)
        return if (p <= 0.5f) 0f else (p - 0.5f) * 2f
    }

    /** Progress values of the pre-rendered burst the widget pushes (evenly spaced, last = 1). */
    fun burst(frames: Int = FRAMES): List<Float> = (1..frames).map { it.toFloat() / frames }
}
