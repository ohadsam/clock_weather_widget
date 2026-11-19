package com.clockweather.app

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.assertIsDisplayed
import org.junit.Rule
import org.junit.Test

/** Emulator smoke test: the app opens, shows the preview, and Settings opens with its first section visible. */
class HomeScreenTest {
    @get:Rule val rule = createAndroidComposeRule<MainActivity>()

    @Test fun homeShowsAppNameAndOpensSettings() {
        val a = rule.activity
        rule.onNodeWithText(a.getString(R.string.app_name)).assertIsDisplayed()
        rule.onNodeWithText(a.getString(R.string.settings)).performClick()
        rule.onNodeWithText(a.getString(R.string.sec_look)).assertIsDisplayed()
    }
}
