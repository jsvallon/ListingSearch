package com.johnvallon.listingsearch.feature.search

import com.johnvallon.listingsearch.common.model.Listing

/**
 * Immutable UI state for the property search screen.
 */
data class SearchUiState(
    val minPrice: String = EMPTY_STRING,
    val maxPrice: String = EMPTY_STRING,
    val city: String = EMPTY_STRING,
    val keyword: String = EMPTY_STRING,
    val minBedrooms: String = EMPTY_STRING,
    val isLoading: Boolean = false,
    val listings: List<Listing> = emptyList(),
    val hasSearched: Boolean = false,
    val errorMessage: String? = null,
    val totalResults: Int = 0,
)

/**
 * User actions that update the search screen state.
 */
sealed interface SearchAction {
    data class MinPriceChanged(val value: String) : SearchAction
    data class MaxPriceChanged(val value: String) : SearchAction
    data class CityChanged(val value: String) : SearchAction
    data class KeywordChanged(val value: String) : SearchAction
    data class MinBedroomsChanged(val value: String) : SearchAction
    data object SearchClicked : SearchAction
    data object RetryClicked : SearchAction
    data object ClearClicked : SearchAction
}
