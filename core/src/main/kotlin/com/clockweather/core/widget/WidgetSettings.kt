package com.clockweather.core.widget

import com.clockweather.core.clock.ClockZone
import com.clockweather.core.text.Untrusted
import com.clockweather.core.weather.Place
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/** FLIP and DIGITAL are digital clocks (digits); ANALOG is a plain hands dial; POCKET is the golden pocket watch (owner, 2026-11-19). */
enum class ClockStyle { FLIP, DIGITAL, ANALOG, MINIMAL, POCKET }

/** Everything the owner can change about the widget. Every field has a default; unknown or bad stored values are repaired by [sanitized]. */
@Serializable
data class PlaceSetting(val id: String, val name: String, val lat: Double, val lon: Double, val isCurrent: Boolean = false) {
    fun toPlace() = Place(id, name, lat, lon, "auto", isCurrent)
}

@Serializable
data class WidgetSettings(
    val version: Int = VERSION,
    val clockStyle: ClockStyle = ClockStyle.FLIP,
    val paletteId: String = "classic_white",
    /** Plate behind everything: 0 = fully transparent (default, the owner's request). */
    val backgroundAlphaPct: Int = 0,
    /** Plate of the weather/info panel under the clock. */
    val panelAlphaPct: Int = 75,
    val cornerRadiusDp: Int = 18,
    /** Extra scale on top of the automatic fit to the widget size. */
    val scalePct: Int = 100,
    val use24h: Boolean = true,
    val showAmPm: Boolean = true,
    val showClock: Boolean = true,
    val showWeather: Boolean = true,
    val showHiLo: Boolean = true,
    val showOutlook: Boolean = true,
    val showGregorianDate: Boolean = true,
    val showHebrewDate: Boolean = true,
    val showHolidays: Boolean = true,
    val showAlarm: Boolean = true,
    val showAgenda: Boolean = true,
    val showWorldClocks: Boolean = true,
    val showSteps: Boolean = false,
    val showSunTimes: Boolean = true,
    /** Nightfall (tzeit) and, on a Shabbat / Yom Tov eve, candle lighting, next to the sun times. Needs [showSunTimes]. */
    val showNightfall: Boolean = true,
    val fahrenheit: Boolean = false,
    val rainLikelyFromPct: Int = 50,
    val rollHebrewAtSunset: Boolean = true,
    val inIsrael: Boolean = true,
    /** "system", "he" or "en". */
    val language: String = "system",
    val calmAnimations: Boolean = false,
    /** Play the weather illustration loop (1.5 s) on every minute tick while the screen is on. */
    val animateWeather: Boolean = true,
    val places: List<PlaceSetting> = emptyList(),
    val activePlaceId: String? = null,
    val worldClocks: List<ClockZoneSetting> = emptyList(),
    /** Package whose next alarm is shown; empty = any alarm app (the system's next alarm). */
    val alarmPackage: String = "",
    val alarmShowWithinHours: Int = 24,
    val agendaCalendarIds: List<Long> = emptyList(),
    val agendaRotateSeconds: Int = 8,
    val agendaIncludeAllDay: Boolean = true,
) {
    fun sanitized(): WidgetSettings {
        val cleanPlaces = places.asSequence()
            .filter { it.lat in -90.0..90.0 && it.lon in -180.0..180.0 }
            .map { it.copy(id = Untrusted.clean(it.id, 40), name = Untrusted.clean(it.name, 60)) }
            .filter { it.id.isNotEmpty() && it.name.isNotEmpty() }
            .distinctBy { it.id }.take(MAX_PLACES).toList()
        val zones = worldClocks.asSequence()
            .filter { com.clockweather.core.clock.WorldClock.validZone(it.zoneId) != null }
            .map { it.copy(label = Untrusted.clean(it.label, 24)) }
            .distinctBy { it.zoneId }.take(MAX_WORLD_CLOCKS).toList()
        return copy(
            version = VERSION,
            paletteId = if (Palettes.byId(paletteId) != null) paletteId else Palettes.DEFAULT.id,
            backgroundAlphaPct = backgroundAlphaPct.coerceIn(0, 100),
            panelAlphaPct = panelAlphaPct.coerceIn(0, 100),
            cornerRadiusDp = cornerRadiusDp.coerceIn(0, 36),
            scalePct = scalePct.coerceIn(50, 150),
            rainLikelyFromPct = rainLikelyFromPct.coerceIn(10, 90),
            language = if (language in setOf("system", "he", "en")) language else "system",
            places = cleanPlaces,
            activePlaceId = activePlaceId?.takeIf { id -> cleanPlaces.any { it.id == id } } ?: cleanPlaces.firstOrNull()?.id,
            worldClocks = zones,
            alarmPackage = alarmPackage.take(200).filter { it.isLetterOrDigit() || it == '.' || it == '_' },
            alarmShowWithinHours = alarmShowWithinHours.coerceIn(0, 168),
            agendaCalendarIds = agendaCalendarIds.take(40),
            agendaRotateSeconds = agendaRotateSeconds.coerceIn(3, 60),
        )
    }

    fun clockZones(): List<ClockZone> = worldClocks.map { ClockZone(it.zoneId, it.label) }

    companion object {
        const val VERSION = 1
        const val MAX_PLACES = 4
        const val MAX_WORLD_CLOCKS = 3
        const val MAX_JSON_BYTES = 64 * 1024

        private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true; coerceInputValues = true }

        fun encode(s: WidgetSettings): String = json.encodeToString(serializer(), s.sanitized())

        /** Never throws: an empty, huge or corrupt string gives the defaults. */
        fun decode(raw: String?): WidgetSettings {
            if (raw.isNullOrBlank() || raw.length > MAX_JSON_BYTES) return WidgetSettings().sanitized()
            return runCatching { json.decodeFromString(serializer(), raw).sanitized() }.getOrElse { WidgetSettings().sanitized() }
        }
    }
}

@Serializable
data class ClockZoneSetting(val zoneId: String, val label: String)
