package com.johnvallon.listingsearch.cucumber

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.text.AnnotatedString
import androidx.test.platform.app.InstrumentationRegistry
import com.johnvallon.listingsearch.feature.search.SearchTestTags
import io.cucumber.java.AfterStep
import io.cucumber.java.Before
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When

/**
 * Step definitions for property_search.feature. Every step finds the UI through
 * [SearchTestTags], the same tags the screen sets with Modifier.testTag.
 */
class SearchSteps(private val holder: ComposeRuleHolder) {

    private val compose get() = holder.composeRule

    @Before
    fun resetFakeService() = FakeListingRepository.reset()

    /**
     * Slow mode, so a person can watch the scenario run. Pass the instrumentation argument
     * `stepDelayMs` (for example 1000) to pause after every step. Off by default.
     */
    @AfterStep
    fun pauseToWatch() {
        val delayMs = InstrumentationRegistry.getArguments().getString("stepDelayMs")?.toLongOrNull() ?: 0L
        if (delayMs > 0) {
            compose.waitForIdle()
            Thread.sleep(delayMs)
        }
    }

    // ---- Setup ----

    @Given("the listing service is available")
    fun serviceAvailable() {
        FakeListingRepository.isAvailable = true
    }

    @Given("the listing service is unavailable")
    fun serviceUnavailable() {
        FakeListingRepository.isAvailable = false
    }

    @Given("the app is open on the search screen")
    fun appIsOpen() = waitFor(SearchTestTags.INITIAL_STATE)

    @Given("I have searched for {string}")
    fun haveSearchedFor(city: String) {
        enterText(city, "city")
        tapSearch()
        waitFor(SearchTestTags.RESULTS_LIST)
    }

    // ---- Actions ----

    @When("^I enter \"([^\"]*)\" in the (city|keyword|min price|max price|min bedrooms) field$")
    fun enterText(text: String, field: String) {
        compose.onNodeWithTag(fieldTag(field)).performTextReplacement(text)
    }

    @When("I tap Search")
    fun tapSearch() {
        compose.onNodeWithTag(SearchTestTags.SEARCH_BUTTON).performClick()
    }

    @When("I tap Clear")
    fun tapClear() {
        compose.onNodeWithTag(SearchTestTags.CLEAR_BUTTON).performClick()
    }

    @When("I tap Try again")
    fun tapTryAgain() {
        compose.onNodeWithTag(SearchTestTags.RETRY_BUTTON).performClick()
    }

    @When("I tap the filter summary bar")
    fun tapSummaryBar() {
        waitFor(SearchTestTags.FILTER_SUMMARY_BAR)
        compose.onNodeWithTag(SearchTestTags.FILTER_SUMMARY_BAR).performClick()
    }

    // ---- Checks ----

    @Then("I see the results list")
    fun seeResults() {
        waitFor(SearchTestTags.RESULTS_LIST)
        compose.onNodeWithTag(SearchTestTags.RESULTS_LIST).assertIsDisplayed()
    }

    @Then("I see {string} as the result count")
    fun seeResultCount(text: String) {
        waitFor(SearchTestTags.RESULTS_COUNT)
        compose.onNodeWithTag(SearchTestTags.RESULTS_COUNT).assertTextEquals(text)
    }

    @Then("I see the listing {string}")
    fun seeListing(address: String) {
        compose.onNodeWithTag(SearchTestTags.RESULTS_LIST).performScrollToNode(hasText(address, substring = true))
        compose.onNodeWithText(address, substring = true).assertIsDisplayed()
    }

    @Then("the filters are collapsed into the summary bar")
    fun filtersCollapsed() {
        waitFor(SearchTestTags.FILTER_SUMMARY_BAR)
        compose.onNodeWithTag(SearchTestTags.CITY_FIELD).assertDoesNotExist()
    }

    @Then("the search filters are open")
    fun filtersOpen() {
        waitFor(SearchTestTags.CITY_FIELD)
        compose.onNodeWithTag(SearchTestTags.CITY_FIELD).assertIsDisplayed()
    }

    @Then("I see the no results message")
    fun seeNoResults() = waitFor(SearchTestTags.EMPTY_STATE)

    @Then("I see the network error message")
    fun seeNetworkError() = waitFor(SearchTestTags.ERROR_STATE)

    @Then("I see the start screen")
    fun seeStartScreen() = waitFor(SearchTestTags.INITIAL_STATE)

    @Then("I see the price error {string}")
    fun seePriceError(message: String) {
        waitFor(SearchTestTags.VALIDATION_MESSAGE)
        compose.onNodeWithText(message).assertIsDisplayed()
    }

    @Then("^the (city|keyword|min price|max price|min bedrooms) field shows \"([^\"]*)\"$")
    fun fieldShows(field: String, text: String) {
        compose.onNodeWithTag(fieldTag(field))
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.EditableText, AnnotatedString(text)))
    }

    @Then("^the (city|keyword|min price|max price|min bedrooms) field is empty$")
    fun fieldIsEmpty(field: String) = fieldShows(field, "")

    // ---- Helpers ----

    private fun fieldTag(field: String) = when (field) {
        "city" -> SearchTestTags.CITY_FIELD
        "keyword" -> SearchTestTags.KEYWORD_FIELD
        "min price" -> SearchTestTags.MIN_PRICE_FIELD
        "max price" -> SearchTestTags.MAX_PRICE_FIELD
        "min bedrooms" -> SearchTestTags.MIN_BEDROOMS_FIELD
        else -> error("Unknown field: $field")
    }

    /** Waits up to 5 seconds for an element with [tag] to appear (searches and loading are async). */
    private fun waitFor(tag: String) {
        compose.waitUntil(timeoutMillis = 5_000) {
            compose.onAllNodesWithTag(tag).fetchSemanticsNodes().isNotEmpty()
        }
    }
}
