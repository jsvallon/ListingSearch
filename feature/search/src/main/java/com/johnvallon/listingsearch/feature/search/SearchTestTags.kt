package com.johnvallon.listingsearch.feature.search

/**
 * Test tags for the search screen. UI tests and Cucumber scenarios find elements by these tags,
 * so the screen and the tests share one source of truth.
 */
object SearchTestTags {
    const val CITY_FIELD = "search_city_field"
    const val KEYWORD_FIELD = "search_keyword_field"
    const val MIN_PRICE_FIELD = "search_min_price_field"
    const val MAX_PRICE_FIELD = "search_max_price_field"
    const val MIN_BEDROOMS_FIELD = "search_min_bedrooms_field"
    const val SEARCH_BUTTON = "search_button"
    const val CLEAR_BUTTON = "search_clear_button"
    const val HIDE_FILTERS_BUTTON = "search_hide_filters_button"
    const val FILTER_SUMMARY_BAR = "search_filter_summary_bar"
    const val VALIDATION_MESSAGE = "search_validation_message"
    const val INITIAL_STATE = "search_initial_state"
    const val LOADING_STATE = "search_loading_state"
    const val EMPTY_STATE = "search_empty_state"
    const val ERROR_STATE = "search_error_state"
    const val RETRY_BUTTON = "search_retry_button"
    const val EDIT_FILTERS_BUTTON = "search_edit_filters_button"
    const val RESULTS_LIST = "search_results_list"
    const val RESULTS_COUNT = "search_results_count"

    /** Tag for one listing card, e.g. "search_listing_card_42". */
    fun listingCard(id: String) = "search_listing_card_$id"
}
