package com.clockweather.app.data

import android.content.ClipData
import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.clockweather.core.update.ShareText
import java.io.File

/** Share the app: a message with the download link, or the installed APK itself (copied to the cache and handed out through FileProvider). */
object AppSharer {
    fun shareLink(ctx: Context, hebrew: Boolean) {
        val send = Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT, ShareText.message(hebrew))
        ctx.startActivity(Intent.createChooser(send, null).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    /** Straight into WhatsApp (or WhatsApp Business) with the link message; any other app can still be picked in the chooser when WhatsApp is not installed. */
    fun shareWhatsApp(ctx: Context, hebrew: Boolean) {
        val text = ShareText.message(hebrew)
        for (pkg in listOf("com.whatsapp", "com.whatsapp.w4b")) {
            try {
                ctx.startActivity(Intent(Intent.ACTION_SEND).setType("text/plain").setPackage(pkg).putExtra(Intent.EXTRA_TEXT, text).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                return
            } catch (_: android.content.ActivityNotFoundException) { /* try the next one */ }
        }
        shareLink(ctx, hebrew)
    }

    /** A new e-mail with a subject and the link message, in whichever mail app the user has (mailto: only offers mail apps). */
    fun shareEmail(ctx: Context, hebrew: Boolean, subject: String) {
        val mail = Intent(Intent.ACTION_SENDTO, android.net.Uri.parse("mailto:")).putExtra(Intent.EXTRA_SUBJECT, subject).putExtra(Intent.EXTRA_TEXT, ShareText.message(hebrew))
        try { ctx.startActivity(Intent.createChooser(mail, null).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) } catch (_: android.content.ActivityNotFoundException) { shareLink(ctx, hebrew) }
    }

    /** False when the APK could not be prepared. */
    fun shareApk(ctx: Context): Boolean {
        return try {
            val dir = File(ctx.cacheDir, "share").apply { mkdirs() }
            val copy = File(dir, "ClockWise.apk")
            File(ctx.applicationInfo.sourceDir).copyTo(copy, overwrite = true)
            val uri = FileProvider.getUriForFile(ctx, "${ctx.packageName}.fileprovider", copy)
            val send = Intent(Intent.ACTION_SEND).setType("application/vnd.android.package-archive")
                .putExtra(Intent.EXTRA_STREAM, uri).putExtra(Intent.EXTRA_TEXT, ShareText.message(false))
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            send.clipData = ClipData.newRawUri("ClockWise.apk", uri)
            ctx.startActivity(Intent.createChooser(send, null).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            true
        } catch (_: Exception) { false }
    }
}
