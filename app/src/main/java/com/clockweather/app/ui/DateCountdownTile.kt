package com.clockweather.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.clockweather.app.R
import com.clockweather.app.data.TimerStore
import com.clockweather.app.widget.ClockWidgetProvider
import com.clockweather.app.widget.RenderState
import com.clockweather.core.dashboard.DashTile
import com.clockweather.core.hebrew.HolidayKind
import com.clockweather.core.hebrew.Holidays
import com.clockweather.core.timers.DateCountdown
import com.clockweather.core.timers.DateCountdowns
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

/** "N days until ...": pick a holiday in one tap or any date, give it a name. The nearest one (within a month) also shows in the widget row. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DateCountdownTile(st: RenderState, index: Int, animate: Boolean) {
    val ctx = LocalContext.current
    val today = st.now.atZone(st.zone).toLocalDate()
    var tick by remember { mutableStateOf(0) }
    val list = remember(tick) { DateCountdowns.upcoming(TimerStore.dates(ctx), today) }
    var adding by remember { mutableStateOf(false) }
    fun save(new: List<DateCountdown>) { TimerStore.saveDates(ctx, new); tick++; ClockWidgetProvider.updateAll(ctx) }
    DashCard(index, animate, DashTitles.of(DashTile.DATE_COUNTDOWN, st.hebrew), null) {
        if (list.isEmpty()) Text(stringResource(R.string.no_date_countdowns), style = MaterialTheme.typography.bodyMedium)
        list.forEach { d ->
            Row(Modifier.fillMaxWidth().heightIn(min = 48.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(DateCountdowns.text(d, today, st.hebrew), Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
                TextButton(onClick = { save(TimerStore.dates(ctx).filterNot { it.id == d.id }) }) { Text(stringResource(R.string.remove)) }
            }
        }
        val upcomingHolidays = remember(today, st.settings.inIsrael) {
            (1..150).flatMap { n -> val day = today.plusDays(n.toLong()); Holidays.on(day, st.settings.inIsrael).filter { it.kind == HolidayKind.MAJOR || it.kind == HolidayKind.MINOR || it.kind == HolidayKind.MODERN }.take(1).map { day to it } }
                .distinctBy { it.second.id }.take(6)
        }
        if (upcomingHolidays.isNotEmpty()) {
            Text(stringResource(R.string.count_to_holiday), style = MaterialTheme.typography.titleSmall)
            ChipRow(upcomingHolidays.map { (day, h) -> day to h.name(st.hebrew) }, { false }) { day ->
                val h = upcomingHolidays.first { it.first == day }.second
                if (TimerStore.dates(ctx).none { it.id == "h_${h.id}_$day" }) save(TimerStore.dates(ctx) + DateCountdown("h_${h.id}_$day", h.name(st.hebrew), day.toEpochDay()))
            }
        }
        OutlinedButton(onClick = { adding = true }, enabled = TimerStore.dates(ctx).size < DateCountdowns.MAX_ITEMS, modifier = Modifier.heightIn(min = 48.dp)) { Text(stringResource(R.string.add_date_countdown)) }
    }
    if (adding) {
        var name by remember { mutableStateOf("") }
        val picker = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { adding = false },
            confirmButton = {
                TextButton(
                    enabled = picker.selectedDateMillis != null && name.isNotBlank(),
                    onClick = {
                        val date = Instant.ofEpochMilli(picker.selectedDateMillis!!).atZone(ZoneOffset.UTC).toLocalDate()
                        save(TimerStore.dates(ctx) + DateCountdown("c${System.currentTimeMillis()}", name.trim(), date.toEpochDay())); adding = false
                    },
                ) { Text(stringResource(R.string.save)) }
            },
            dismissButton = { TextButton(onClick = { adding = false }) { Text(stringResource(R.string.cancel)) } },
        ) {
            OutlinedTextField(name, { name = it.take(30) }, label = { Text(stringResource(R.string.event_name)) }, singleLine = true, modifier = Modifier.fillMaxWidth().padding(16.dp))
            DatePicker(picker)
        }
    }
}
