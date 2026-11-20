package com.clockweather.core.dashboard

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DashboardTest {
    @Test fun defaultsShowTheMainCardsAndHideTheOptionalOnes() {
        val v = Dashboard.visibleTiles(emptyList(), emptyList())
        assertTrue(DashTile.WEATHER in v && DashTile.AGENDA in v && DashTile.ALARM in v)
        assertTrue(DashTile.STEPS !in v && DashTile.WORLD !in v)
    }

    @Test fun moveAndHideSurviveARoundTrip() {
        var e = Dashboard.resolve(emptyList(), emptyList())
        e = Dashboard.move(e, DashTile.AGENDA, -2)
        e = Dashboard.setVisible(e, DashTile.WEATHER, false)
        e = Dashboard.setVisible(e, DashTile.STEPS, true)
        val back = Dashboard.resolve(Dashboard.orderOf(e), Dashboard.hiddenOf(e))
        assertEquals(e, back)
        assertTrue(back.first { it.tile == DashTile.STEPS }.visible)
        assertTrue(!back.first { it.tile == DashTile.WEATHER }.visible)
    }

    @Test fun garbageAndNewTilesAreRepaired() {
        val e = Dashboard.resolve(listOf("weather", "nope", "weather", "alarm"), listOf("zzz"))
        assertEquals(DashTile.values().size, e.size)
        assertEquals(listOf(DashTile.WEATHER, DashTile.ALARM), e.take(2).map { it.tile })
        assertTrue(e.first { it.tile == DashTile.STEPS }.visible.not()) // a tile unknown to the stored order keeps its default
    }

    @Test fun moveAtTheEdgesDoesNothing() {
        val e = Dashboard.resolve(emptyList(), emptyList())
        assertEquals(e, Dashboard.move(e, e.first().tile, -1))
        assertEquals(e, Dashboard.move(e, e.last().tile, 1))
    }
}
