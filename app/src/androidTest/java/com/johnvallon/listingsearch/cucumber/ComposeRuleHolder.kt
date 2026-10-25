package com.johnvallon.listingsearch.cucumber

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.johnvallon.listingsearch.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.cucumber.junit.WithJunitRule
import org.junit.Rule
import org.junit.rules.RuleChain

/**
 * Starts the app for each scenario. Hilt must be set up before MainActivity opens, so the two
 * rules are chained explicitly (Hilt outside, the Compose/activity rule inside) instead of
 * relying on rule `order`, which Cucumber does not apply reliably.
 */
@WithJunitRule(useAsTestClassInDescription = true)
@HiltAndroidTest
class ComposeRuleHolder {
    private val hiltRule = HiltAndroidRule(this)

    val composeRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val rules: RuleChain = RuleChain.outerRule(hiltRule).around(composeRule)
}
