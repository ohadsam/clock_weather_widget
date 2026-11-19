package com.clockweather.core.widget

import kotlin.math.pow

/** Colour of one look, as ARGB ints (the widget bitmap and the Compose preview read the same table). */
data class Palette(
    val id: String,
    val he: String,
    val en: String,
    val cardTop: Int,
    val cardBottom: Int,
    val digit: Int,
    val panel: Int,
    val panelText: Int,
    val accent: Int,
    val hinge: Int,
)

object Palettes {
    val ALL = listOf(
        Palette("classic_white", "לבן קלאסי", "Classic white", 0xFFFFFFFF.toInt(), 0xFFE9E9EC.toInt(), 0xFF111111.toInt(), 0xFF1B1B1D.toInt(), 0xFFFFFFFF.toInt(), 0xFFFFB300.toInt(), 0xFFB5B5BA.toInt()),
        Palette("graphite", "גרפיט", "Graphite", 0xFF3A3D42.toInt(), 0xFF2B2E33.toInt(), 0xFFF5F5F5.toInt(), 0xFF16181B.toInt(), 0xFFF5F5F5.toInt(), 0xFF4FC3F7.toInt(), 0xFF121316.toInt()),
        Palette("sand", "חול", "Sand", 0xFFF6EBD7.toInt(), 0xFFE8D8BB.toInt(), 0xFF3B2F1E.toInt(), 0xFF3B2F1E.toInt(), 0xFFFFF4E0.toInt(), 0xFFE07A1F.toInt(), 0xFFB9A57F.toInt()),
        Palette("ocean", "אוקיינוס", "Ocean", 0xFFDFF3FA.toInt(), 0xFFBFE3F2.toInt(), 0xFF0B2A3C.toInt(), 0xFF0B2A3C.toInt(), 0xFFEAF8FF.toInt(), 0xFF00B0FF.toInt(), 0xFF8DB8CB.toInt()),
        Palette("sunset", "שקיעה", "Sunset", 0xFFFFE3D1.toInt(), 0xFFFFC9A8.toInt(), 0xFF4A1D12.toInt(), 0xFF4A1D12.toInt(), 0xFFFFEFE6.toInt(), 0xFFFF6D3A.toInt(), 0xFFD59C80.toInt()),
        Palette("forest", "יער", "Forest", 0xFFE3EFE0.toInt(), 0xFFC9DEC4.toInt(), 0xFF14301C.toInt(), 0xFF14301C.toInt(), 0xFFEAF6E7.toInt(), 0xFF66BB6A.toInt(), 0xFF93AE8E.toInt()),
        Palette("midnight", "חצות", "Midnight", 0xFF1A2238.toInt(), 0xFF111829.toInt(), 0xFFF2F5FF.toInt(), 0xFF0B0F1C.toInt(), 0xFFF2F5FF.toInt(), 0xFF8C9EFF.toInt(), 0xFF080B14.toInt()),
    )
    val DEFAULT = ALL.first()
    fun byId(id: String): Palette? = ALL.firstOrNull { it.id == id }

    private fun lin(c: Int): Double { val v = c / 255.0; return if (v <= 0.03928) v / 12.92 else ((v + 0.055) / 1.055).pow(2.4) }
    fun luminance(argb: Int): Double = 0.2126 * lin((argb shr 16) and 0xFF) + 0.7152 * lin((argb shr 8) and 0xFF) + 0.0722 * lin(argb and 0xFF)

    /** WCAG contrast ratio of two opaque colours. */
    fun contrast(a: Int, b: Int): Double {
        val la = luminance(a); val lb = luminance(b)
        return (maxOf(la, lb) + 0.05) / (minOf(la, lb) + 0.05)
    }
}
