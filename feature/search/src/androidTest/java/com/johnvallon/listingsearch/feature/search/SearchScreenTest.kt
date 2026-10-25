package com.johnvallon.listingsearch.feature.search

import android.content.Context
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.text.AnnotatedString
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.johnvallon.listingsearch.common.model.Listing
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI tests for the [SearchScreen] composable on its own: no ViewModel, no network.
 * Each test renders the screen for one [SearchUiState], finds elements by [SearchTestTags],
 * and checks what is shown and which [SearchAction]s the screen sends.
 */
@RunWith(AndroidJUnit4::class)
class SearchScreenTest {

    @get:Rule
    val compose = createComposeRule()

    private val context: Context = ApplicationProvider.getApplicationContext()
    private val actions = mutableListOf<SearchAction>()

    private val listings = listOf(
        listing("A4", "22 Birch Ln", price = 610_000.0, bedrooms = 4),
        listing("B10", "100 Maple Dr", price = 585_000.0, bedrooms = 3),
    )
    private val results = SearchUiState(
        city = "Reston",
        hasSearched = true,
        listings = listings,
        totalResults = listings.size,
    )

    // ---- Start screen and filters ----

    @Test
    fun startScreen_showsAllFieldsAndTheStartMessage() {
        show(SearchUiState())

        listOf(
            SearchTestTags.CITY_FIELD, SearchTestTags.KEYWORD_FIELD, SearchTestTags.MIN_PRICE_FIELD,
            SearchTestTags.MAX_PRICE_FIELD, SearchTestTags.MIN_BEDROOMS_FIELD,
            SearchTestTags.SEARCH_BUTTON, SearchTestTags.CLEAR_BUTTON, SearchTestTags.INITIAL_STATE,
        ).forEach { compose.onNodeWithTag(it).assertIsDisplayed() }
        compose.onNodeWithText(string(R.string.initial_title)).assertIsDisplayed()
        compose.onNodeWithTag(SearchTestTags.HIDE_FILTERS_BUTTON).assertDoesNotExist()
        compose.onNodeWithTag(SearchTestTags.FILTER_SUMMARY_BAR).assertDoesNotExist()
    }

    @Test
    fun typingInEachField_sendsTheMatchingAction() {
        show(SearchUiState())

        compose.onNodeWithTag(SearchTestTags.CITY_FIELD).performTextInput("Reston")
        compose.onNodeWithTag(SearchTestTags.KEYWORD_FIELD).performTextInput("garage")
        compose.onNodeWithTag(SearchTestTags.MIN_PRICE_FIELD).performTextInput("300000")
        compose.onNodeWithTag(SearchTestTags.MAX_PRICE_FIELD).performTextInput("700000")
        compose.onNodeWithTag(SearchTestTags.MIN_BEDROOMS_FIELD).performTextInput("2")

        assertEquals(
            listOf(
                SearchAction.CityChanged("Reston"),
                SearchAction.KeywordChanged("garage"),
                SearchAction.MinPriceChanged("300000"),
                SearchAction.MaxPriceChanged("700000"),
                SearchAction.MinBedroomsChanged("2"),
            ),
            actions,
        )
    }

    @Test
    fun fieldsShowTheValuesFromState() {
        show(SearchUiState(city = "Reston", keyword = "garage", minPrice = "300000", maxPrice = "700000", minBedrooms = "2"))

        assertFieldText(SearchTestTags.CITY_FIELD, "Reston")
        assertFieldText(SearchTestTags.KEYWORD_FIELD, "garage")
        assertFieldText(SearchTestTags.MIN_PRICE_FIELD, "300000")
        assertFieldText(SearchTestTags.MAX_PRICE_FIELD, "700000")
        assertFieldText(SearchTestTags.MIN_BEDROOMS_FIELD, "2")
    }

    @Test
    fun tappingSearch_sendsSearchClicked() {
        show(SearchUiState(city = "Reston"))

        compose.onNodeWithTag(SearchTestTags.SEARCH_BUTTON).performClick()

        assertEquals(listOf(SearchAction.SearchClicked), actions)
    }

    @Test
    fun keyboardSearchOnBedrooms_sendsSearchClicked() {
        show(SearchUiState(city = "Reston"))

        compose.onNodeWithTag(SearchTestTags.MIN_BEDROOMS_FIELD).performImeAction()

        assertEquals(listOf(SearchAction.SearchClicked), actions)
    }

    @Test
    fun tappingClear_sendsClearClicked() {
        show(SearchUiState(city = "Reston"))

        compose.onNodeWithTag(SearchTestTags.CLEAR_BUTTON).performClick()

        assertEquals(listOf(SearchAction.ClearClicked), actions)
    }

    // ---- Loading ----

    @Test
    fun loading_showsSpinnerAndDisablesTheButtons() {
        show(SearchUiState(city = "Reston", isLoading = true))

        compose.onNodeWithTag(SearchTestTags.LOADING_STATE).assertIsDisplayed()
        compose.onNodeWithTag(SearchTestTags.SEARCH_BUTTON).assertIsNotEnabled()
        compose.onNodeWithTag(SearchTestTags.CLEAR_BUTTON).assertIsNotEnabled()
    }

    @Test
    fun notLoading_buttonsAreEnabled() {
        show(SearchUiState())

        compose.onNodeWithTag(SearchTestTags.SEARCH_BUTTON).assertIsEnabled()
        compose.onNodeWithTag(SearchTestTags.CLEAR_BUTTON).assertIsEnabled()
    }

    // ---- Results ----

    @Test
    fun results_showCountAndOneCardPerListing() {
        show(results, filtersExpanded = false)

        compose.onNodeWithTag(SearchTestTags.RESULTS_LIST).assertIsDisplayed()
        compose.onNodeWithTag(SearchTestTags.RESULTS_COUNT)
            .assertTextEquals(plural(R.plurals.results_count, 2, 2))
        listings.forEach { listing ->
            compose.onNodeWithTag(SearchTestTags.RESULTS_LIST)
                .performScrollToNode(hasText(listing.address, substring = true))
            compose.onNodeWithTag(SearchTestTags.listingCard(listing.id)).assertIsDisplayed()
        }
    }

    @Test
    fun listingCard_showsAddressLocationAndFacts() {
        show(results, filtersExpanded = false)

        compose.onNodeWithTag(SearchTestTags.listingCard("A4"))
            .assert(hasText("22 Birch Ln", substring = true))
            .assert(hasText(string(R.string.listing_location, "Reston", "VA", "20194"), substring = true))
            .assert(hasText(plural(R.plurals.listing_beds, 2, "4"), substring = true))
            .assert(hasText(plural(R.plurals.listing_baths, 2, "3"), substring = true))
            .assert(hasText(string(R.string.listing_sqft, "2,100"), substring = true))
    }

    @Test
    fun partialPage_saysHowManyAreShown() {
        show(results.copy(totalResults = 12), filtersExpanded = false)

        compose.onNodeWithTag(SearchTestTags.RESULTS_COUNT)
            .assertTextEquals(plural(R.plurals.results_count_partial, 12, 2, 12))
    }

    // ---- Collapsing filters ----

    @Test
    fun afterSearch_filtersCollapseIntoTheSummaryBar() {
        show(results, filtersExpanded = false)

        compose.onNodeWithTag(SearchTestTags.FILTER_SUMMARY_BAR).assertIsDisplayed()
        compose.onNodeWithTag(SearchTestTags.CITY_FIELD).assertDoesNotExist()
    }

    @Test
    fun tappingSummaryBar_opensTheFilters() {
        show(results, filtersExpanded = false)

        compose.onNodeWithTag(SearchTestTags.FILTER_SUMMARY_BAR).performClick()

        compose.onNodeWithTag(SearchTestTags.CITY_FIELD).assertIsDisplayed()
        assertFieldText(SearchTestTags.CITY_FIELD, "Reston")
        compose.onNodeWithTag(SearchTestTags.FILTER_SUMMARY_BAR).assertDoesNotExist()
    }

    @Test
    fun tappingHide_collapsesTheFilters() {
        show(results, filtersExpanded = true)

        compose.onNodeWithTag(SearchTestTags.HIDE_FILTERS_BUTTON).performClick()

        compose.onNodeWithTag(SearchTestTags.FILTER_SUMMARY_BAR).assertIsDisplayed()
        compose.onNodeWithTag(SearchTestTags.CITY_FIELD).assertDoesNotExist()
    }

    // ---- No results, error, validation ----

    @Test
    fun noResults_showsMessage_andEditFiltersOpensTheFilters() {
        show(SearchUiState(city = "Alexandria", hasSearched = true), filtersExpanded = false)

        compose.onNodeWithTag(SearchTestTags.EMPTY_STATE).assertIsDisplayed()
        compose.onNodeWithText(string(R.string.empty_title)).assertIsDisplayed()

        compose.onNodeWithTag(SearchTestTags.EDIT_FILTERS_BUTTON).performClick()

        compose.onNodeWithTag(SearchTestTags.CITY_FIELD).assertIsDisplayed()
    }

    @Test
    fun networkError_showsFriendlyMessage_andRetrySendsRetryClicked() {
        show(
            SearchUiState(city = "Reston", hasSearched = true, errorMessage = SearchMessages.SERVICE_UNREACHABLE),
            filtersExpanded = false,
        )

        compose.onNodeWithTag(SearchTestTags.ERROR_STATE).assertIsDisplayed()
        compose.onNodeWithText(string(R.string.error_body)).assertIsDisplayed()
        // The technical message from the ViewModel is never shown to the user.
        compose.onNodeWithText(SearchMessages.SERVICE_UNREACHABLE).assertDoesNotExist()

        compose.onNodeWithTag(SearchTestTags.RETRY_BUTTON).performClick()

        assertEquals(listOf(SearchAction.RetryClicked), actions)
    }

    @Test
    fun priceValidation_showsMessage_andKeepsTheFiltersOpen() {
        show(
            SearchUiState(
                minPrice = "600000",
                maxPrice = "400000",
                hasSearched = true,
                errorMessage = SearchMessages.PRICE_RANGE_INVALID,
            ),
            filtersExpanded = false,
        )

        compose.onNodeWithTag(SearchTestTags.VALIDATION_MESSAGE).assertIsDisplayed()
        compose.onNodeWithText(SearchMessages.PRICE_RANGE_INVALID).assertIsDisplayed()
        compose.onNodeWithTag(SearchTestTags.CITY_FIELD).assertIsDisplayed()
        compose.onNodeWithTag(SearchTestTags.FILTER_SUMMARY_BAR).assertDoesNotExist()
        compose.onNodeWithTag(SearchTestTags.HIDE_FILTERS_BUTTON).assertDoesNotExist()
        compose.onNodeWithTag(SearchTestTags.ERROR_STATE).assertDoesNotExist()
    }

    // ---- Helpers ----

    /**
     * Shows the screen starting from [initialState]. Like the real ViewModel, typing updates the
     * state, so text fields behave as they do in the app. Every action is also recorded in [actions].
     */
    private fun show(initialState: SearchUiState, filtersExpanded: Boolean = true) {
        compose.setContent {
            var state by remember { mutableStateOf(initialState) }
            MaterialTheme {
                SearchScreen(
                    state = state,
                    onAction = { action ->
                        actions += action
                        state = state.applyTyping(action)
                    },
                    initialFiltersExpanded = filtersExpanded,
                )
            }
        }
    }

    /** Applies field edits the way SearchViewModel does; other actions don't change state here. */
    private fun SearchUiState.applyTyping(action: SearchAction): SearchUiState = when (action) {
        is SearchAction.CityChanged -> copy(city = action.value)
        is SearchAction.KeywordChanged -> copy(keyword = action.value)
        is SearchAction.MinPriceChanged -> copy(minPrice = action.value)
        is SearchAction.MaxPriceChanged -> copy(maxPrice = action.value)
        is SearchAction.MinBedroomsChanged -> copy(minBedrooms = action.value)
        else -> this
    }

    private fun assertFieldText(tag: String, text: String) {
        compose.onNodeWithTag(tag)
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.EditableText, AnnotatedString(text)))
    }

    private fun string(id: Int, vararg args: Any): String = context.getString(id, *args)

    private fun plural(id: Int, quantity: Int, vararg args: Any): String =
        context.resources.getQuantityString(id, quantity, *args)

    private fun listing(id: String, address: String, price: Double, bedrooms: Int) = Listing(
        id = id, address = address, city = "Reston", state = "VA", zip = "20194", price = price,
        bedrooms = bedrooms, bathrooms = 3.0, squareFeet = 2_100, listedDate = "2026-10-01",
        status = "active", description = "Test listing", score = 0.0,
    )
}
