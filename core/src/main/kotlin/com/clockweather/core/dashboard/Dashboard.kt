package com.clockweather.core.dashboard

/** The cards the in-app dashboard can show. [id] is what is stored in settings, so never rename one. */
enum class DashTile(val id: String, val he: String, val en: String, val defaultVisible: Boolean = true) {
    WIDGET("widget", "הווידג'ט", "Widget"),
    DATE("date", "תאריך וחגים", "Date and holidays"),
    WEATHER("weather", "מזג אוויר", "Weather"),
    PLACES("places", "המקומות שלי", "My places"),
    ALARM("alarm", "שעון מעורר", "Alarm"),
    AGENDA("agenda", "אירועי היום", "Today's events"),
    SUN("sun", "זריחה, שקיעה וצאת הכוכבים", "Sun and nightfall"),
    STEPS("steps", "צעדים", "Steps", defaultVisible = false),
    WORLD("world", "שעונים בעולם", "World clocks", defaultVisible = false);

    companion object { fun byId(id: String): DashTile? = values().firstOrNull { it.id == id } }
}

/**
 * Order and visibility of the dashboard cards (owner-customisable). Pure list maths on stored id lists:
 * unknown ids are dropped, duplicates removed, cards added in a later version are appended, so old settings always load.
 */
object Dashboard {
    data class Entry(val tile: DashTile, val visible: Boolean)

    fun resolve(order: List<String>, hidden: List<String>): List<Entry> {
        val known = order.mapNotNull { DashTile.byId(it) }.distinct()
        val all = known + DashTile.values().filter { it !in known }
        val explicitHidden = hidden.mapNotNull { DashTile.byId(it) }.toSet()
        return all.map { t -> Entry(t, if (order.isEmpty() && hidden.isEmpty()) t.defaultVisible else (t !in explicitHidden && (t in known || t.defaultVisible))) }
    }

    fun orderOf(entries: List<Entry>): List<String> = entries.map { it.tile.id }
    fun hiddenOf(entries: List<Entry>): List<String> = entries.filter { !it.visible }.map { it.tile.id }

    fun move(entries: List<Entry>, tile: DashTile, delta: Int): List<Entry> {
        val i = entries.indexOfFirst { it.tile == tile }
        val j = i + delta
        if (i < 0 || j !in entries.indices) return entries
        return entries.toMutableList().also { val e = it.removeAt(i); it.add(j, e) }
    }

    fun setVisible(entries: List<Entry>, tile: DashTile, visible: Boolean): List<Entry> =
        entries.map { if (it.tile == tile) it.copy(visible = visible) else it }

    fun visibleTiles(order: List<String>, hidden: List<String>): List<DashTile> = resolve(order, hidden).filter { it.visible }.map { it.tile }
}
