package com.johnvallon.listingsearch.feature.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * Hosts the search flow screen and binds the view model state to the Compose UI.
 *
 * @param viewModel The Hilt-managed [SearchViewModel] that owns the screen state and actions.
 */
@Composable
fun SearchRoute(viewModel: SearchViewModel = hiltViewModel()) {
    /**
     * Collects the screen state from the view model and re-composes when it changes.
     */
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    SearchScreen(state = state, onAction = viewModel::onAction)
}
