package com.clockweather.app

import android.graphics.Bitmap
import androidx.test.core.app.ApplicationProvider
import com.clockweather.app.widget.RenderState
import com.clockweather.app.widget.WidgetRenderer
import com.clockweather.core.agenda.AgendaEvent
import com.clockweather.core.alarm.NextAlarm
import com.clockweather.core.clock.ClockZoneSetting
import com.clockweather.core.weather.OpenMeteo
import com.clockweather.core.widget.ClockStyle
import com.clockweather.core.widget.PlaceSetting
import com.clockweather.core.widget.WidgetSettings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.time.Instant
import java.time.ZoneId

/** Renders every clock style at several sizes in both languages: nothing throws, the size is right, something is drawn. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34])
class WidgetRendererTest {
    private val ctx get() = ApplicationProvider.getApplicationContext<android.content.Context>()
    private val now = Instant.parse("2026-11-19T08:05:00Z")

    private val forecastJson = """
      {"timezone":"Asia/Jerusalem","current":{"temperature_2m":22.4,"is_day":1,"weather_code":2,"relative_humidity_2m":60,"apparent_temperature":21,"wind_speed_10m":8},
       "hourly":{"time":["2026-11-19T09:00"],"temperature_2m":[22.0],"precipitation_probability":[10],"weather_code":[2],"is_day":[1]},
       "daily":{"time":["2026-11-19"],"weather_code":[61],"temperature_2m_max":[25.2],"temperature_2m_min":[15.1],"precipitation_probability_max":[70],
                "sunrise":["2026-11-19T06:14"],"sunset":["2026-11-19T16:38"]}}
    """.trimIndent()

    private fun state(style: ClockStyle, hebrew: Boolean, flipFrom: IntArray? = null, p: Float = 1f): RenderState {
        val s = WidgetSettings(
            clockStyle = style, showSteps = true,
            places = listOf(PlaceSetting("pt", "Petah Tikva", 32.09, 34.88)), activePlaceId = "pt",
            worldClocks = listOf(ClockZoneSetting("America/New_York", "NYC")),
        ).sanitized()
        return RenderState(
            s, now, ZoneId.of("Asia/Jerusalem"), hebrew, OpenMeteo.parseForecast("pt", forecastJson, 0),
            NextAlarm(now.toEpochMilli() + 9 * 3_600_000L, "com.smartring.app"),
            listOf(AgendaEvent(1, "Team sync", now.toEpochMilli() + 30 * 60_000L, now.toEpochMilli() + 90 * 60_000L, false)),
            steps = 4210, flipFrom = flipFrom, flipProgress = p,
        )
    }

    private fun opaquePixels(b: Bitmap): Int {
        var n = 0
        for (y in 0 until b.height step 4) for (x in 0 until b.width step 4) if ((b.getPixel(x, y) ushr 24) > 0) n++
        return n
    }

    @Test fun everyStyleSizeAndLanguageRenders() {
        val sizes = listOf(110 to 70, 150 to 110, 280 to 160, 330 to 340, 60 to 60)
        for (style in ClockStyle.values()) for (hebrew in listOf(true, false)) for ((w, h) in sizes) {
            val bmp = WidgetRenderer.render(ctx, w * 2, h * 2, 2f, state(style, hebrew))
            assertEquals(w * 2, bmp.width); assertEquals(h * 2, bmp.height)
            assertTrue("$style he=$hebrew ${w}x$h is blank", opaquePixels(bmp) > 0)
        }
    }

    @Test fun flipFramesRenderAndDiffer() {
        val from = intArrayOf(1, 1, 0, 4)
        val a = WidgetRenderer.render(ctx, 560, 320, 2f, state(ClockStyle.FLIP, false, from, 0.2f))
        val b = WidgetRenderer.render(ctx, 560, 320, 2f, state(ClockStyle.FLIP, false, from, 0.8f))
        assertTrue(opaquePixels(a) > 0 && opaquePixels(b) > 0)
        assertTrue("flip frames should differ", !a.sameAs(b))
    }

    @Test fun hugeRequestIsCappedNotCrashing() {
        val bmp = WidgetRenderer.render(ctx, 6000, 6000, 3f, state(ClockStyle.FLIP, true))
        assertTrue(bmp.width.toLong() * bmp.height <= 1_700_000L)
    }

    @Test fun transparentByDefault() {
        val bmp = WidgetRenderer.render(ctx, 600, 400, 2f, state(ClockStyle.DIGITAL, false).let { it.copy(settings = it.settings.copy(showWeather = false)) })
        assertEquals("top-left corner must stay transparent", 0, bmp.getPixel(1, 1) ushr 24)
    }
}
