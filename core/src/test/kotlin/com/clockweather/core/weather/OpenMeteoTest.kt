package com.clockweather.core.weather

import java.time.Instant
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class OpenMeteoTest {
    private val sample = """
    {"latitude":32.09,"longitude":34.88,"timezone":"Asia/Jerusalem","utc_offset_seconds":7200,
     "current":{"time":"2026-11-19T18:00","temperature_2m":18.4,"relative_humidity_2m":64,"apparent_temperature":17.0,"is_day":0,"weather_code":2,"wind_speed_10m":9.5},
     "hourly":{"time":["2026-11-19T00:00","2026-11-19T01:00","2026-11-20T00:00"],"temperature_2m":[15.1,14.8,13.0],"relative_humidity_2m":[70,72,80],
       "precipitation_probability":[0,10,60],"precipitation":[0.0,0.0,1.2],"weather_code":[1,2,61],"is_day":[0,0,0]},
     "daily":{"time":["2026-11-19","2026-11-20"],"weather_code":[2,61],"temperature_2m_max":[23.4,19.0],"temperature_2m_min":[14.2,12.6],
       "precipitation_probability_max":[10,70],"precipitation_sum":[0.0,4.5],"sunrise":["2026-11-19T06:14","2026-11-20T06:15"],"sunset":["2026-11-19T16:38","2026-11-20T16:37"]}}
    """.trimIndent()

    @Test fun parsesCurrentDailyAndHourly() {
        val f = assertNotNull(OpenMeteo.parseForecast("pt", sample, 1000))
        assertEquals("Asia/Jerusalem", f.zone.id)
        assertEquals(18.4, f.current!!.tempC, 0.001)
        assertEquals(Sky.PARTLY_CLOUDY, f.current!!.sky)
        assertEquals(false, f.current!!.isDay)
        assertEquals(2, f.days.size)
        assertEquals(23.4, f.days[0].maxC, 0.001)
        assertEquals(14.2, f.days[0].minC, 0.001)
        assertEquals(java.time.LocalTime.of(16, 38), f.days[0].sunset!!.toLocalTime())
        assertEquals(3, f.hours.size)
        assertEquals(2, f.hoursOf(LocalDate.of(2026, 11, 19)).size)
        assertEquals(Sky.RAIN, f.days[1].sky)
    }

    @Test fun todayPicksTheLocalDate() {
        val f = OpenMeteo.parseForecast("pt", sample, 0)!!
        // 2026-11-19T23:30Z is already 20 Nov 01:30 in Jerusalem (UTC+2)
        assertEquals(LocalDate.of(2026, 11, 20), f.today(Instant.parse("2026-11-19T23:30:00Z"))!!.date)
        assertEquals(LocalDate.of(2026, 11, 19), f.today(Instant.parse("2026-11-19T10:00:00Z"))!!.date)
    }

    @Test fun badInputNeverThrows() {
        for (bad in listOf("", "{", "[]", "null", "{\"daily\":{}}", "{\"daily\":{\"time\":[\"x\"]}}", "\u0000garbage")) assertNull(OpenMeteo.parseForecast("p", bad, 0), bad)
        assertNull(OpenMeteo.parseForecast("p", "x".repeat(600_000), 0))
    }

    @Test fun missingCurrentIsTolerated() {
        val noCurrent = sample.replace(Regex("\"current\":\\{[^}]*\\},"), "")
        val f = OpenMeteo.parseForecast("p", noCurrent, 0)!!
        assertNull(f.current)
        assertEquals(2, f.days.size)
    }

    @Test fun urlsAreWellFormedAndBounded() {
        val u = OpenMeteo.forecastUrl(32.0889, 34.8864)
        assertTrue(u.startsWith("https://api.open-meteo.com/v1/forecast?latitude=32.089&longitude=34.886"))
        assertTrue("timezone=auto" in u && "daily=" in u && "hourly=" in u)
        assertTrue("forecast_days=7" in OpenMeteo.forecastUrl(0.0, 0.0, 99))
        val g = OpenMeteo.geocodingUrl("פתח תקווה", "he")
        assertTrue(g.startsWith("https://geocoding-api.open-meteo.com/v1/search?name=") && "language=he" in g)
    }

    @Test fun geocodingCleansUntrustedNames() {
        val body = """{"results":[{"name":"Pet‮ah Tikva","admin1":"Central","country":"Israel","latitude":32.09,"longitude":34.88,"timezone":"Asia/Jerusalem"},
            {"name":"Bad","latitude":999,"longitude":0},{"latitude":1,"longitude":1}]}"""
        val hits = OpenMeteo.parseGeocoding(body)
        assertEquals(1, hits.size)
        assertEquals("Petah Tikva", hits[0].name)
    }

    @Test fun wmoMappingAndOutlook() {
        assertEquals(Sky.CLEAR, Sky.fromWmo(0))
        assertEquals(Sky.THUNDERSTORM, Sky.fromWmo(95))
        assertEquals(Sky.UNKNOWN, Sky.fromWmo(1234))
        assertEquals(Sky.UNKNOWN, Sky.fromWmo(null))
        val d = DayForecast(LocalDate.of(2026, 11, 19), 20.0, 12.0, 70, 3.0, Sky.RAIN, null, null)
        assertEquals(Outlook.RAIN_LIKELY, WeatherOutlook.of(d))
        assertEquals("צפוי גשם (70%)", WeatherOutlook.text(Outlook.RAIN_LIKELY, 70, true))
        assertEquals(Outlook.SUNNY, WeatherOutlook.of(d.copy(rainChancePct = 0, sky = Sky.CLEAR)))
        assertEquals(Outlook.RAIN_POSSIBLE, WeatherOutlook.of(d.copy(rainChancePct = 30, sky = Sky.PARTLY_CLOUDY)))
        assertEquals(Outlook.STORM, WeatherOutlook.of(d.copy(sky = Sky.THUNDERSTORM)))
    }

    @Test fun temperatureFormatting() {
        assertEquals("25°", TempFormat.short(24.6, false))
        assertEquals("0°", TempFormat.short(-0.2, false))
        assertEquals("77°F", TempFormat.withUnit(25.0, true))
        assertEquals("-3°", TempFormat.short(-3.4, false))
    }
}
