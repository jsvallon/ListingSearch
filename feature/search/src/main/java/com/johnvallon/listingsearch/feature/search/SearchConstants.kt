package com.johnvallon.listingsearch.feature.search

/** Default value for text fields in [SearchUiState]. */
const val EMPTY_STRING = ""

/**
 * Messages the SearchViewModel puts into [SearchUiState.errorMessage].
 * Kept in one place so the ViewModel, the screen and tests all refer to the same text.
 */
object SearchMessages {
    const val PRICE_RANGE_INVALID = "Minimum price cannot be greater than maximum price."
    const val SERVICE_UNREACHABLE = "Cannot reach the listing service. Make sure server.py is running."
    const val UNKNOWN_ERROR = "Something went wrong."
}
