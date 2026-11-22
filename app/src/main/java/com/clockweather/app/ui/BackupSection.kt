package com.clockweather.app.ui

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.clockweather.app.R
import com.clockweather.core.look.LookCode
import com.clockweather.core.widget.WidgetSettings

/** Settings -> Backup and sharing: send my look as a short code, back up everything as text, restore from pasted text. No files, no permissions. */
@Composable
fun BackupSection(s: WidgetSettings, onChange: (WidgetSettings) -> Unit) {
    val ctx = LocalContext.current
    var pasted by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<Int?>(null) }
    // a file through the system picker (no storage permission): save the backup text, or load one back
    val saver = androidx.activity.compose.rememberLauncherForActivityResult(androidx.activity.result.contract.ActivityResultContracts.CreateDocument("text/plain")) { uri ->
        if (uri != null) result = runCatching { ctx.contentResolver.openOutputStream(uri)?.use { it.write(LookCode.encodeBackup(s).toByteArray(Charsets.UTF_8)) }; R.string.backup_file_saved }.getOrDefault(R.string.code_invalid)
    }
    val loader = androidx.activity.compose.rememberLauncherForActivityResult(androidx.activity.result.contract.ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            val text = runCatching { ctx.contentResolver.openInputStream(uri)?.use { it.readBytes().take(200_000).toByteArray().toString(Charsets.UTF_8) } }.getOrNull()?.trim().orEmpty()
            val applied = if (text.startsWith("CWB1:")) LookCode.decodeBackup(text) else null
            if (applied != null) { onChange(applied); result = R.string.code_applied } else result = R.string.code_invalid
        }
    }
    SectionCard(stringResource(R.string.sec_backup), stringResource(R.string.sec_backup_sum)) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(stringResource(R.string.backup_hint), style = MaterialTheme.typography.bodySmall)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { share(ctx, LookCode.encodeLook(s)) }, modifier = Modifier.heightIn(min = 48.dp)) { Text(stringResource(R.string.share_look)) }
                OutlinedButton(onClick = { share(ctx, LookCode.encodeBackup(s)) }, modifier = Modifier.heightIn(min = 48.dp)) { Text(stringResource(R.string.share_backup)) }
                OutlinedButton(onClick = { saver.launch("ClockWise-backup.txt") }, modifier = Modifier.heightIn(min = 48.dp)) { Text(stringResource(R.string.backup_save_file)) }
                OutlinedButton(onClick = { loader.launch(arrayOf("text/plain", "*/*")) }, modifier = Modifier.heightIn(min = 48.dp)) { Text(stringResource(R.string.backup_load_file)) }
            }
            OutlinedTextField(pasted, { pasted = it.take(100_000) }, label = { Text(stringResource(R.string.paste_code)) }, modifier = Modifier.fillMaxWidth(), maxLines = 3)
            Button(
                enabled = pasted.isNotBlank(), modifier = Modifier.heightIn(min = 48.dp),
                onClick = {
                    val t = pasted.trim()
                    val applied = if (t.startsWith("CWB1:")) LookCode.decodeBackup(t) else LookCode.applyLook(s, t)
                    if (applied != null) { onChange(applied); result = R.string.code_applied; pasted = "" } else result = R.string.code_invalid
                },
            ) { Text(stringResource(R.string.apply_code)) }
            result?.let { Text(stringResource(it), style = MaterialTheme.typography.bodyMedium) }
        }
    }
}

private fun share(ctx: Context, text: String) {
    val send = Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT, text)
    ctx.startActivity(Intent.createChooser(send, null).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
}
