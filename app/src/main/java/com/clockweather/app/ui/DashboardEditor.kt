package com.clockweather.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.clockweather.app.R
import com.clockweather.core.dashboard.Dashboard
import com.clockweather.core.widget.WidgetSettings

/** Settings -> Dashboard: show/hide each card and move it up or down (buttons, so it also works with TalkBack and one hand). */
@Composable
fun DashboardEditor(s: WidgetSettings, hebrew: Boolean, onChange: (WidgetSettings) -> Unit) {
    val entries = Dashboard.resolve(s.dashboardOrder, s.dashboardHidden)
    fun apply(list: List<Dashboard.Entry>) = onChange(s.copy(dashboardOrder = Dashboard.orderOf(list), dashboardHidden = Dashboard.hiddenOf(list)))
    SectionCard(stringResource(R.string.sec_dashboard), stringResource(R.string.sec_dashboard_sum, entries.count { it.visible })) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(stringResource(R.string.dashboard_hint), style = MaterialTheme.typography.bodySmall)
            entries.forEachIndexed { i, e ->
                Row(Modifier.fillMaxWidth().heightIn(min = 48.dp), verticalAlignment = Alignment.CenterVertically) {
                    Switch(checked = e.visible, onCheckedChange = { apply(Dashboard.setVisible(entries, e.tile, it)) })
                    Text(DashTitles.of(e.tile, hebrew), Modifier.weight(1f).padding(horizontal = 12.dp), style = MaterialTheme.typography.bodyLarge)
                    IconButton(onClick = { apply(Dashboard.move(entries, e.tile, -1)) }, enabled = i > 0) { Icon(Icons.Filled.KeyboardArrowUp, contentDescription = stringResource(R.string.move_up), modifier = Modifier.size(28.dp)) }
                    IconButton(onClick = { apply(Dashboard.move(entries, e.tile, 1)) }, enabled = i < entries.lastIndex) { Icon(Icons.Filled.KeyboardArrowDown, contentDescription = stringResource(R.string.move_down), modifier = Modifier.size(28.dp)) }
                }
            }
            OutlinedButton(onClick = { onChange(s.copy(dashboardOrder = emptyList(), dashboardHidden = emptyList())) }, modifier = Modifier.heightIn(min = 48.dp)) { Text(stringResource(R.string.dashboard_reset)) }
        }
    }
}
