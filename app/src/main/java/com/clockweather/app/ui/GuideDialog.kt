package com.clockweather.app.ui

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.clockweather.app.R
import kotlinx.coroutines.launch

/** Remembers that the first-run guide was shown. */
object GuidePrefs {
    private fun p(ctx: Context) = ctx.getSharedPreferences("guide", Context.MODE_PRIVATE)
    fun seen(ctx: Context): Boolean = p(ctx).getBoolean("seen", false)
    fun setSeen(ctx: Context, v: Boolean = true) { p(ctx).edit().putBoolean("seen", v).apply() }
}

/** Four short pages on first launch (and again from Settings): the widget, the dashboard, settings, privacy. */
@Composable
fun GuideDialog(onDone: () -> Unit) {
    val titles = listOf(R.string.guide_title_1, R.string.guide_title_2, R.string.guide_title_3, R.string.guide_title_4)
    val bodies = listOf(R.string.guide_body_1, R.string.guide_body_2, R.string.guide_body_3, R.string.guide_body_4)
    val pager = rememberPagerState { titles.size }
    val scope = rememberCoroutineScope()
    Dialog(onDismissRequest = onDone) {
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                HorizontalPager(pager, Modifier.fillMaxWidth()) { page ->
                    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (page == 0) Image(painterResource(R.drawable.spot_add_widget), contentDescription = null, modifier = Modifier.height(110.dp))
                        if (page == 1) Image(painterResource(R.drawable.spot_location), contentDescription = null, modifier = Modifier.height(110.dp))
                        Text(stringResource(titles[page]), style = MaterialTheme.typography.titleLarge)
                        Text(stringResource(bodies[page]), style = MaterialTheme.typography.bodyLarge)
                    }
                }
                Text("${pager.currentPage + 1} / ${titles.size}", style = MaterialTheme.typography.bodySmall, modifier = Modifier.align(Alignment.CenterHorizontally))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    TextButton(onClick = onDone, modifier = Modifier.heightIn(min = 48.dp)) { Text(stringResource(R.string.guide_skip)) }
                    if (pager.currentPage < titles.lastIndex) TextButton(onClick = { scope.launch { pager.animateScrollToPage(pager.currentPage + 1) } }, modifier = Modifier.heightIn(min = 48.dp)) { Text(stringResource(R.string.guide_next)) }
                    else TextButton(onClick = onDone, modifier = Modifier.heightIn(min = 48.dp)) { Text(stringResource(R.string.guide_done)) }
                }
            }
        }
    }
}
