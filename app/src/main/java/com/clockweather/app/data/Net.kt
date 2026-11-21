package com.clockweather.app.data

import java.net.HttpURLConnection
import java.net.URL

/** The only place that opens a network connection: HTTPS to an allow-listed host, short timeouts, size-capped body. */
object Net {
    private val ALLOWED_HOSTS = setOf("api.open-meteo.com", "geocoding-api.open-meteo.com", "air-quality-api.open-meteo.com")
    private const val MAX_BYTES = 512 * 1024

    /** Blocking; call from a background thread. Returns null on any failure. */
    fun get(url: String): String? {
        return try {
            val u = URL(url)
            if (u.protocol != "https" || u.host !in ALLOWED_HOSTS) return null
            val c = u.openConnection() as HttpURLConnection
            c.connectTimeout = 10_000
            c.readTimeout = 10_000
            c.instanceFollowRedirects = false
            c.setRequestProperty("Accept", "application/json")
            if (c.responseCode != 200) return null
            c.inputStream.use { input ->
                val out = java.io.ByteArrayOutputStream()
                val buf = ByteArray(8192)
                while (true) {
                    val n = input.read(buf)
                    if (n < 0) break
                    out.write(buf, 0, n)
                    if (out.size() > MAX_BYTES) return null
                }
                out.toString("UTF-8")
            }
        } catch (_: Exception) {
            null
        }
    }
}
