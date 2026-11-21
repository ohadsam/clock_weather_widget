package com.clockweather.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

/** Emulator smoke test: the app opens (first-run guide skipped), shows the dashboard, and Settings opens with its first section visible. */
class HomeScreenTest {
    @get:Rule val rule = createAndroidComposeRule<MainActivity>()

    @Test fun homeShowsAppNameAndOpensSettings() {
        val a = rule.activity
        runCatching { rule.onNodeWithText(a.getString(R.string.guide_skip)).performClick() } // the guide shows on a fresh install
        rule.onNodeWithText(a.getString(R.string.app_name)).assertIsDisplayed()
        rule.onNodeWithContentDescription(a.getString(R.string.settings)).performClick()
        rule.onNodeWithText(a.getString(R.string.sec_look)).assertIsDisplayed()
    }
}
