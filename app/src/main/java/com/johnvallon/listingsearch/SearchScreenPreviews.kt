package com.johnvallon.listingsearch

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.johnvallon.listingsearch.common.model.Listing
import com.johnvallon.listingsearch.feature.search.SearchMessages
import com.johnvallon.listingsearch.feature.search.SearchScreen
import com.johnvallon.listingsearch.feature.search.SearchUiState

/*
 * Previews of every Property Search state, each shown on a large phone and a small phone,
 * in light and dark theme. They live in :app because the app theme lives here and
 * :feature:search must not depend on :app.
 */

private const val LARGE_PHONE = "spec:width=411dp,height=891dp"
private const val SMALL_PHONE = "spec:width=360dp,height=640dp"
private const val DARK = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL

/** Shows a preview on a large and a small phone, each in light and dark theme. */
@Preview(name = "Large phone · Light", group = "Large phone", device = LARGE_PHONE)
@Preview(name = "Large phone · Dark", group = "Large phone", device = LARGE_PHONE, uiMode = DARK)
@Preview(name = "Small phone · Light", group = "Small phone", device = SMALL_PHONE)
@Preview(name = "Small phone · Dark", group = "Small phone", device = SMALL_PHONE, uiMode = DARK)
annotation class SearchScreenSizes

private val sampleListings = listOf(
    Listing("1", "1820 Wiehle Avenue", "Reston", "VA", "20190", 425_000.0, 3, 2.0, 1_850, "2026-09-12", "active", "Townhouse with garage.", 0.0),
    Listing("2", "11400 Washington Plaza W #604", "Reston", "VA", "20190", 389_000.0, 2, 2.0, 1_210, "2026-09-20", "active", "Condo with lake view.", 0.0),
    Listing("3", "2250 Hunters Woods Road", "Reston", "VA", "20191", 612_000.0, 4, 3.0, 2_640, "2026-08-30", "pending", "Single family with yard.", 0.0),
    Listing("4", "1610 Lakeport Way", "Reston", "VA", "20194", 515_000.0, 3, 2.5, 2_050, "2026-10-01", "active", "Near the lake trail.", 0.0),
    Listing("5", "12010 Stonewood Square", "Reston", "VA", "20191", 549_000.0, 3, 3.0, 1_980, "2026-10-05", "active", "Renovated townhouse.", 0.0),
)

private val filledFilters = SearchUiState(
    city = "Reston",
    minPrice = "300000",
    maxPrice = "700000",
    minBedrooms = "2",
)

@Composable
private fun PreviewScreen(state: SearchUiState, filtersExpanded: Boolean = true) {
    ListingSearchTheme {
        SearchScreen(state = state, onAction = {}, initialFiltersExpanded = filtersExpanded)
    }
}

@SearchScreenSizes
@Composable
private fun InitialPreview() = PreviewScreen(SearchUiState())

@SearchScreenSizes
@Composable
private fun ResultsPreview() = PreviewScreen(
    filledFilters.copy(hasSearched = true, listings = sampleListings, totalResults = sampleListings.size),
    filtersExpanded = false,
)

@SearchScreenSizes
@Composable
private fun EditingFiltersPreview() = PreviewScreen(
    filledFilters.copy(hasSearched = true, listings = sampleListings, totalResults = sampleListings.size),
    filtersExpanded = true,
)

@SearchScreenSizes
@Composable
private fun LoadingPreview() = PreviewScreen(filledFilters.copy(isLoading = true))

@SearchScreenSizes
@Composable
private fun NoResultsPreview() = PreviewScreen(
    filledFilters.copy(minPrice = "", maxPrice = "250000", minBedrooms = "5", hasSearched = true),
    filtersExpanded = false,
)

@SearchScreenSizes
@Composable
private fun ErrorPreview() = PreviewScreen(
    filledFilters.copy(hasSearched = true, errorMessage = SearchMessages.SERVICE_UNREACHABLE),
    filtersExpanded = false,
)

@SearchScreenSizes
@Composable
private fun PriceValidationPreview() = PreviewScreen(
    SearchUiState(
        city = "Reston",
        minPrice = "600000",
        maxPrice = "400000",
        hasSearched = true,
        errorMessage = SearchMessages.PRICE_RANGE_INVALID,
    ),
)
