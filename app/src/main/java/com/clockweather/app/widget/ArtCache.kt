package com.clockweather.app.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory

/** Decodes a drawable-nodpi image once by resource name; null when the resource does not exist (callers draw a fallback). */
object ArtCache {
    private val cache = HashMap<String, Bitmap?>()

    @Synchronized
    fun get(ctx: Context, name: String): Bitmap? = cache.getOrPut(name) {
        val id = ctx.resources.getIdentifier(name, "drawable", ctx.packageName)
        if (id == 0) null else runCatching { BitmapFactory.decodeResource(ctx.resources, id) }.getOrNull()
    }
}
