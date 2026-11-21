package com.clockweather.app

import android.graphics.Bitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import java.io.File
import java.io.FileOutputStream

/**
 * Walks the main screens and saves a PNG of each to the app's external files folder (`screenshots/`), which `ui-tests.yml` pulls and uploads
 * as an artifact. Look at them: they are the evidence for layout, contrast and RTL that no unit test can give.
 */
class ScreenshotJourneyTest {
    @get:Rule val rule = createAndroidComposeRule<MainActivity>()

    private fun shot(name: String) {
        rule.waitForIdle()
        val bmp = rule.onRoot().captureToImage().asAndroidBitmap()
        val dir = File(InstrumentationRegistry.getInstrumentation().targetContext.getExternalFilesDir(null), "screenshots").apply { mkdirs() }
        FileOutputStream(File(dir, "$name.png")).use { bmp.compress(Bitmap.CompressFormat.PNG, 100, it) }
    }

    @Test fun journey() {
        val a = rule.activity
        shot("00_first_run_guide")
        runCatching { rule.onNodeWithText(a.getString(R.string.guide_skip)).performClick() }
        shot("01_dashboard")
        rule.onNodeWithContentDescription(a.getString(R.string.settings)).performClick()
        shot("02_settings")
        rule.activityRule.scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }
        shot("03_back_on_dashboard")
    }
}
