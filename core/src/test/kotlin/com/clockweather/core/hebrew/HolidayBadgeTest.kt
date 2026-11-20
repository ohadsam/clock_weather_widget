package com.clockweather.core.hebrew

import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class HolidayBadgeTest {
    @Test fun everyHolidayIdOfSixYearsHasABadgeOrAnExplicitNoBadge() {
        val ids = HashSet<String>()
        for (inIsrael in listOf(true, false)) {
            var d = LocalDate.of(2024, 1, 1)
            while (d.year <= 2029) { Holidays.on(d, inIsrael).forEach { ids += it.id }; d = d.plusDays(1) }
        }
        assertTrue(ids.size > 20, "sweep found the holidays: $ids")
        for (id in ids) {
            val key = HolidayBadge.drawableKey(id)
            if (key == null) assertTrue(id in HolidayBadge.NO_BADGE, "holiday '$id' needs a badge mapping or must be listed in NO_BADGE")
            else assertTrue(key in HolidayBadge.ART_KEYS, "$id maps to unknown art '$key'")
        }
    }

    @Test fun everyArtKeyIsUsedAndNoBadgeIdsStayBare() {
        val used = HashSet<String>()
        var d = LocalDate.of(2024, 1, 1)
        while (d.year <= 2029) { Holidays.on(d).forEach { h -> HolidayBadge.drawableKey(h.id)?.let { used += it } }; d = d.plusDays(1) }
        assertEquals(HolidayBadge.ART_KEYS, used, "every badge is reachable")
        for (id in HolidayBadge.NO_BADGE) assertNull(HolidayBadge.drawableKey(id))
    }
}
