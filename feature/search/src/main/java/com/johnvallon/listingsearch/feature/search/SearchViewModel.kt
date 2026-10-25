package com.johnvallon.listingsearch.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.johnvallon.listingsearch.common.data.SearchListingsUseCase
import com.johnvallon.listingsearch.common.model.SearchCriteria
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

/**
 * Manages search form state and executes listing searches.
 *
 * This ViewModel sanitizes user input, validates price ranges, calls [SearchListingsUseCase],
 * and exposes loading, result, and error state via [uiState].
 */
@HiltViewModel
class SearchViewModel @Inject constructor(private val searchListings: SearchListingsUseCase) : ViewModel() {
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    /**
     * Handles a user interaction and updates the screen state accordingly.
     *
     * @param action The action triggered by the UI.
     */
    fun onAction(action: SearchAction) {
        when (action) {
            is SearchAction.MinPriceChanged -> update { copy(minPrice = action.value.numeric()) }
            is SearchAction.MaxPriceChanged -> update { copy(maxPrice = action.value.numeric()) }
            is SearchAction.CityChanged -> update { copy(city = action.value) }
            is SearchAction.KeywordChanged -> update { copy(keyword = action.value) }
            is SearchAction.MinBedroomsChanged -> update { copy(minBedrooms = action.value.filter(Char::isDigit)) }
            SearchAction.SearchClicked, SearchAction.RetryClicked -> search()
            SearchAction.ClearClicked -> _uiState.value = SearchUiState()
        }
    }

    /**
     * Executes a listing search using the current filter values and updates the UI state.
     */
    private fun search() {
        val state = _uiState.value
        val min = state.minPrice.toDoubleOrNull()
        val max = state.maxPrice.toDoubleOrNull()
        if (min != null && max != null && min > max) {
            update { copy(errorMessage = SearchMessages.PRICE_RANGE_INVALID, hasSearched = true) }
            return
        }
        viewModelScope.launch {
            update { copy(isLoading = true, errorMessage = null) }
            try {
                val result = searchListings(SearchCriteria(
                    minPrice = min, maxPrice = max,
                    city = state.city.trim().ifBlank { null },
                    keyword = state.keyword.trim().ifBlank { null },
                    minBedrooms = state.minBedrooms.toIntOrNull(),
                ))
                update { copy(isLoading = false, listings = result.listings, hasSearched = true, totalResults = result.totalResults) }
            } catch (_: IOException) {
                update { copy(isLoading = false, hasSearched = true, errorMessage = SearchMessages.SERVICE_UNREACHABLE) }
            } catch (e: Exception) {
                update { copy(isLoading = false, hasSearched = true, errorMessage = e.message ?: SearchMessages.UNKNOWN_ERROR) }
            }
        }
    }

    /**
     * Applies a state mutation using the current UI state as the receiver.
     */
    private fun update(block: SearchUiState.() -> SearchUiState) = _uiState.update(block)

    /**
     * Keeps only numeric characters and a single decimal separator in price input.
     */
    private fun String.numeric(): String = filter { it.isDigit() || it == '.' }.let { value ->
        if (value.count { it == '.' } <= 1) value else dropLast(1)
    }
}
