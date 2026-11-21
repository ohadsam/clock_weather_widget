package com.clockweather.core.weather

import com.clockweather.core.widget.PlaceSetting
import com.clockweather.core.widget.WidgetSettings
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PlaceIdsTest {
    @Test fun sameSpotSameId() {
        assertEquals(PlaceIds.make(32.0853, 34.7818), PlaceIds.make(32.0851, 34.7821))
        assertEquals("32.09° N, 34.88° E", PlaceIds.coordLabel(32.0853, 34.8801))
        assertEquals("33.87° S, 151.21° E", PlaceIds.coordLabel(-33.8688, 151.2093))
    }

    @Test fun worldPlacesAreCleanedAndCapped() {
        val many = (0 until 15).map { PlaceSetting("w$it", "City $it", it * 5.0, it * 10.0) }
        val s = WidgetSettings(
            places = listOf(PlaceSetting("home", "Home", 32.0, 34.0)),
            worldPlaces = many + PlaceSetting("bad", "Nowhere", 123.0, 0.0) + PlaceSetting("home", "Dup of home", 32.0, 34.0) + PlaceSetting("x", "  ", 1.0, 1.0),
        ).sanitized()
        assertEquals(WidgetSettings.MAX_WORLD_PLACES, s.worldPlaces.size)
        assertTrue(s.worldPlaces.none { it.id == "bad" || it.id == "home" })
        assertTrue(s.worldPlaces.all { it.name.isNotEmpty() })
    }
}
