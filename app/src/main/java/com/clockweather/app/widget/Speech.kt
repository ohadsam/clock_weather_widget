package com.clockweather.app.widget

import com.clockweather.core.agenda.Agenda
import com.clockweather.core.alarm.AlarmIndicator
import com.clockweather.core.weather.TempFormat
import com.clockweather.core.widget.WidgetSpeech
import java.time.format.DateTimeFormatter
import java.util.Locale

/** The sentence TalkBack reads for the whole widget (the widget is one picture, so it needs a spoken description). */
object Speech {
    fun describe(st: RenderState): String {
        val s = st.settings
        val local = st.now.atZone(st.zone)
        val loc = if (st.hebrew) Locale("he") else Locale.ENGLISH
        val time = DateTimeFormatter.ofPattern(if (s.use24h) "HH:mm" else "h:mm a", Locale.ENGLISH).format(local)
        val day = DateTimeFormatter.ofPattern("EEEE", loc).format(local)
        val cur = st.forecast?.current
        val weather = cur?.let { TempFormat.short(it.tempC, s.fahrenheit) + ", " + it.sky.label(st.hebrew) }
        val extras = buildList {
            st.nextAlarm?.takeIf { AlarmIndicator.visible(it, st.now, s.alarmShowWithinHours, s.alarmPackage) }?.let { add(AlarmIndicator.label(it, st.now, st.zone, s.use24h, st.hebrew)) }
            Agenda.forToday(st.agenda, st.now, st.zone).firstOrNull()?.let { add(Agenda.bannerText(it, st.now, st.zone, s.use24h, st.hebrew)) }
            addAll(st.timerLines)
        }
        return WidgetSpeech.sentence(st.hebrew, time, day, weather, extras)
    }
}
