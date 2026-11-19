package com.clockweather.core.text

/** Cleaning for text that comes from outside (calendar titles, place names): no control or bidi-control characters, clamped length. */
object Untrusted {
    private fun bad(c: Char): Boolean =
        c.isISOControl() || c in '‎'..'‏' || c in '‪'..'‮' || c in '⁦'..'⁩' || c == '﻿'

    fun clean(raw: String, max: Int): String {
        val sb = StringBuilder()
        var lastSpace = true
        for (c in raw) {
            if (bad(c)) { if (c == '\n' || c == '\t' || c == '\r') { if (!lastSpace) sb.append(' '); lastSpace = true }; continue }
            if (c.isWhitespace()) { if (!lastSpace) sb.append(' '); lastSpace = true } else { sb.append(c); lastSpace = false }
            if (sb.length >= max) break
        }
        return sb.toString().trim().take(max)
    }
}
