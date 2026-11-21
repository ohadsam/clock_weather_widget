package com.clockweather.app

import android.graphics.Bitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.test.platform.io.PlatformTestStorageRegistry
import org.junit.Rule
import org.junit.Test

/**
 * Walks the main screens and saves a PNG of each through the test runner's output storage; AGP pulls them and `ui-tests.yml` uploads them
 * as the `ui-screenshots` artifact. Look at them: they are the evidence for layout, contrast and RTL that no unit test can give.
 */
class ScreenshotJourneyTest {
    @get:Rule val rule = createAndroidComposeRule<MainActivity>()

    private fun shot(name: String) {
        rule.waitForIdle()
        val bmp = rule.onRoot().captureToImage().asAndroidBitmap()
        // The test runner's own output folder: AGP copies it to app/build/outputs/connected_android_test_additional_output/ before it uninstalls the app
        // (files written into the app's own folders would be deleted by that uninstall).
        PlatformTestStorageRegistry.getInstance().openOutputFile("$name.png").use { bmp.compress(Bitmap.CompressFormat.PNG, 100, it) }
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
