package com.johnvallon.listingsearch.feature.search

import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.johnvallon.listingsearch.common.model.Listing
import java.text.NumberFormat
import kotlin.math.roundToInt

/**
 * Displays the property search screen and manages filter panel visibility while keeping search
 * state owned by the view model.
 *
 * @param state The current screen state.
 * @param onAction Callback invoked for search-related actions from the UI.
 * @param modifier Optional modifier applied to the root layout.
 * @param initialFiltersExpanded Whether the filter panel starts open once results exist. Used by
 * previews to show the collapsed summary bar; the app keeps the default.
 */
@Composable
fun SearchScreen(
    state: SearchUiState,
    onAction: (SearchAction) -> Unit,
    modifier: Modifier = Modifier,
    initialFiltersExpanded: Boolean = true,
) {
    val validationError = state.errorMessage?.takeIf { it == SearchMessages.PRICE_RANGE_INVALID }

    // Purely visual: whether the filter panel is open or shrunk to the summary bar.
    // All search state still lives in SearchViewModel / SearchUiState.
    var filtersExpanded by rememberSaveable { mutableStateOf(initialFiltersExpanded) }
    val showCollapsedBar = state.hasSearched && validationError == null && !filtersExpanded

    val keyboard = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val submitSearch: () -> Unit = {
        keyboard?.hide()
        focusManager.clearFocus()
        filtersExpanded = false
        onAction(SearchAction.SearchClicked)
    }
    val clearSearch: () -> Unit = {
        focusManager.clearFocus()
        filtersExpanded = true
        onAction(SearchAction.ClearClicked)
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .windowInsetsPadding(
                WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom)
            ),
    ) {
        val panelMaxHeight = maxHeight
        Column(Modifier.fillMaxSize()) {
            SearchPanel(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = panelMaxHeight),
            ) {
                if (showCollapsedBar) {
                    CollapsedFilterBar(
                        summary = filterSummary(state),
                        onClick = { filtersExpanded = true },
                    )
                } else {
                    SearchFilters(
                        state = state,
                        validationError = validationError,
                        canHide = state.hasSearched && validationError == null,
                        onHide = {
                            focusManager.clearFocus()
                            filtersExpanded = false
                        },
                        onAction = onAction,
                        onSearch = submitSearch,
                        onClear = clearSearch,
                    )
                }
            }
            SearchContent(
                state = state,
                validationError = validationError,
                onRetry = { onAction(SearchAction.RetryClicked) },
                onEditFilters = { filtersExpanded = true },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun SearchPanel(modifier: Modifier, content: @Composable () -> Unit) {
    // Full-bleed header: runs edge to edge and under the status bar, separated from the
    // results by a hairline instead of floating as a card.
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            Modifier
                .windowInsetsPadding(WindowInsets.statusBars.only(WindowInsetsSides.Top))
                .animateContentSize()
        ) {
            Box(Modifier.weight(1f, fill = false)) { content() }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        }
    }
}

@Composable
private fun SearchHeader(canHide: Boolean, onHide: () -> Unit) {
    Row(verticalAlignment = Alignment.Top) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                stringResource(R.string.search_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.semantics { heading() },
            )
            Text(
                stringResource(R.string.search_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        if (canHide) {
            TextButton(onClick = onHide, modifier = Modifier.testTag(SearchTestTags.HIDE_FILTERS_BUTTON)) {
                Text(stringResource(R.string.search_hide))
                Spacer(Modifier.width(4.dp))
                Icon(SearchIcons.ChevronUp, contentDescription = null, modifier = Modifier.size(18.dp))
            }
        }
    }
}

/**
 * Renders the compact filter form used to change search criteria.
 */
@Composable
private fun SearchFilters(
    state: SearchUiState,
    validationError: String?,
    canHide: Boolean,
    onHide: () -> Unit,
    onAction: (SearchAction) -> Unit,
    onSearch: () -> Unit,
    onClear: () -> Unit,
) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SearchHeader(canHide = canHide, onHide = onHide)

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            FilterField(
                value = state.city,
                onValueChange = { onAction(SearchAction.CityChanged(it)) },
                label = stringResource(R.string.field_city),
                testTag = SearchTestTags.CITY_FIELD,
                icon = SearchIcons.Pin,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
            )
            FilterField(
                value = state.keyword,
                onValueChange = { onAction(SearchAction.KeywordChanged(it)) },
                label = stringResource(R.string.field_keyword),
                testTag = SearchTestTags.KEYWORD_FIELD,
                icon = SearchIcons.Search,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
            )
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    PriceField(
                        label = stringResource(R.string.field_min_price),
                        value = state.minPrice,
                        isError = validationError != null,
                        onValueChange = { onAction(SearchAction.MinPriceChanged(it)) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag(SearchTestTags.MIN_PRICE_FIELD),
                    )
                    PriceField(
                        label = stringResource(R.string.field_max_price),
                        value = state.maxPrice,
                        isError = validationError != null,
                        onValueChange = { onAction(SearchAction.MaxPriceChanged(it)) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag(SearchTestTags.MAX_PRICE_FIELD),
                    )
                }
                if (validationError != null) {
                    ValidationMessage(validationError)
                }
            }
            FilterField(
                value = state.minBedrooms,
                onValueChange = { onAction(SearchAction.MinBedroomsChanged(it)) },
                label = stringResource(R.string.field_min_bedrooms),
                testTag = SearchTestTags.MIN_BEDROOMS_FIELD,
                icon = SearchIcons.Bed,
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Search,
                onImeSearch = onSearch,
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(
                onClick = onSearch,
                enabled = !state.isLoading,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag(SearchTestTags.SEARCH_BUTTON),
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.action_searching))
                } else {
                    Icon(SearchIcons.Search, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.action_search))
                }
            }
            FilledTonalButton(
                onClick = onClear,
                enabled = !state.isLoading,
                modifier = Modifier
                    .height(48.dp)
                    .testTag(SearchTestTags.CLEAR_BUTTON),
            ) {
                Text(stringResource(R.string.action_clear))
            }
        }
    }
}

@Composable
private fun FilterField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    keyboardType: KeyboardType,
    imeAction: ImeAction,
    testTag: String,
    onImeSearch: (() -> Unit)? = null,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp)) },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
        keyboardActions = if (onImeSearch != null) {
            KeyboardActions(onSearch = { onImeSearch() })
        } else {
            KeyboardActions.Default
        },
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag),
    )
}

@Composable
private fun PriceField(
    label: String,
    value: String,
    isError: Boolean,
    onValueChange: (String) -> Unit,
    modifier: Modifier,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        prefix = { Text(stringResource(R.string.price_prefix)) },
        isError = isError,
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
        modifier = modifier,
    )
}

@Composable
private fun ValidationMessage(message: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .testTag(SearchTestTags.VALIDATION_MESSAGE)
            .semantics { liveRegion = LiveRegionMode.Polite },
    ) {
        Icon(
            SearchIcons.Alert,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(16.dp),
        )
        Text(message, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
    }
}

@Composable
private fun CollapsedFilterBar(summary: String, onClick: () -> Unit) {
    val clickLabel = stringResource(R.string.filter_bar_click_label)
    Box(Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
        Surface(
            onClick = onClick,
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
                .testTag(SearchTestTags.FILTER_SUMMARY_BAR)
                .semantics(mergeDescendants = true) {
                    onClick(label = clickLabel) { onClick(); true }
                },
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
            ) {
                Icon(
                    SearchIcons.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp),
                )
                Column(Modifier.weight(1f)) {
                    Text(
                        summary,
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        stringResource(R.string.filter_bar_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                ) {
                    Icon(
                        SearchIcons.Tune,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
        }
    }
}

/**
 * Displays the current state of the results area, including empty, loading, error, and success
 * content based on the view model state.
 */
@Composable
private fun SearchContent(
    state: SearchUiState,
    validationError: String?,
    onRetry: () -> Unit,
    onEditFilters: () -> Unit,
    modifier: Modifier,
) {
    when {
        state.isLoading -> LoadingState(modifier)
        state.errorMessage != null && validationError == null -> ErrorState(onRetry, modifier)
        !state.hasSearched -> InitialState(modifier)
        validationError != null && state.listings.isEmpty() -> InitialState(modifier)
        state.listings.isEmpty() -> EmptyState(onEditFilters, modifier)
        else -> SearchResults(
            listings = state.listings,
            totalResults = state.totalResults,
            modifier = modifier,
        )
    }
}

@Composable
private fun StateMessage(
    icon: ImageVector,
    iconContainer: Color,
    iconTint: Color,
    title: String,
    body: String,
    modifier: Modifier,
    action: (@Composable () -> Unit)? = null,
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 40.dp, vertical = 16.dp),
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(iconContainer),
            ) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(30.dp))
            }
            Spacer(Modifier.height(4.dp))
            Text(
                title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
            )
            Text(
                body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            if (action != null) {
                Spacer(Modifier.height(4.dp))
                action()
            }
        }
    }
}

@Composable
private fun InitialState(modifier: Modifier) {
    StateMessage(
        icon = SearchIcons.Home,
        iconContainer = MaterialTheme.colorScheme.secondaryContainer,
        iconTint = MaterialTheme.colorScheme.onSecondaryContainer,
        title = stringResource(R.string.initial_title),
        body = stringResource(R.string.initial_body),
        modifier = modifier.testTag(SearchTestTags.INITIAL_STATE),
    )
}

@Composable
private fun LoadingState(modifier: Modifier) {
    Box(
        modifier = modifier
            .testTag(SearchTestTags.LOADING_STATE)
            .semantics { liveRegion = LiveRegionMode.Polite },
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            CircularProgressIndicator()
            Text(
                stringResource(R.string.loading_message),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun EmptyState(onEditFilters: () -> Unit, modifier: Modifier) {
    StateMessage(
        icon = SearchIcons.SearchOff,
        iconContainer = MaterialTheme.colorScheme.surfaceContainerHigh,
        iconTint = MaterialTheme.colorScheme.onSurfaceVariant,
        title = stringResource(R.string.empty_title),
        body = stringResource(R.string.empty_body),
        modifier = modifier.testTag(SearchTestTags.EMPTY_STATE),
        action = {
            OutlinedButton(
                onClick = onEditFilters,
                modifier = Modifier.testTag(SearchTestTags.EDIT_FILTERS_BUTTON),
            ) { Text(stringResource(R.string.action_edit_filters)) }
        },
    )
}

@Composable
private fun ErrorState(onRetry: () -> Unit, modifier: Modifier) {
    StateMessage(
        icon = SearchIcons.WifiOff,
        iconContainer = MaterialTheme.colorScheme.errorContainer,
        iconTint = MaterialTheme.colorScheme.onErrorContainer,
        title = stringResource(R.string.error_title),
        body = stringResource(R.string.error_body),
        modifier = modifier
            .testTag(SearchTestTags.ERROR_STATE)
            .semantics { liveRegion = LiveRegionMode.Polite },
        action = {
            OutlinedButton(
                onClick = onRetry,
                modifier = Modifier.testTag(SearchTestTags.RETRY_BUTTON),
            ) { Text(stringResource(R.string.action_try_again)) }
        },
    )
}

@Composable
private fun SearchResults(listings: List<Listing>, totalResults: Int, modifier: Modifier) {
    val count = if (totalResults > 0) totalResults else listings.size
    LazyColumn(
        modifier = modifier.testTag(SearchTestTags.RESULTS_LIST),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item(key = "results-header") {
            Text(
                text = if (listings.size < count) {
                    // The API pages results (pageSize 10). Say how many are shown so the header
                    // never promises listings the user can't scroll to.
                    pluralStringResource(R.plurals.results_count_partial, count, listings.size, count)
                } else {
                    pluralStringResource(R.plurals.results_count, count, count)
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .testTag(SearchTestTags.RESULTS_COUNT)
                    .semantics { heading() },
            )
        }
        items(items = listings, key = { it.id }) { listing ->
            ListingItem(listing)
        }
    }
}

/**
 * Renders a single property card with the address, price, and key listing details.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ListingItem(listing: Listing) {
    val priceFormat = remember { NumberFormat.getCurrencyInstance().apply { maximumFractionDigits = 0 } }
    val integerFormat = remember { NumberFormat.getIntegerInstance() }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(SearchTestTags.listingCard(listing.id))
            .semantics(mergeDescendants = true) {},
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    listing.address,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    stringResource(R.string.listing_location, listing.city, listing.state, listing.zip),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            FlowRow(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    priceFormat.format(listing.price),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(end = 8.dp),
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.align(Alignment.CenterVertically),
                ) {
                    val beds = listing.bedrooms.toString()
                    ListingFact(
                        SearchIcons.Bed,
                        pluralStringResource(R.plurals.listing_beds, if (beds == "1") 1 else 2, beds),
                    )
                    ListingFact(
                        SearchIcons.Bath,
                        pluralStringResource(
                            R.plurals.listing_baths,
                            if (listing.bathrooms == 1.0) 1 else 2,
                            formatBath(listing.bathrooms),
                        ),
                    )
                    ListingFact(
                        SearchIcons.Area,
                        stringResource(R.string.listing_sqft, integerFormat.format(listing.squareFeet)),
                    )
                }
            }
        }
    }
}

@Composable
private fun ListingFact(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(16.dp),
        )
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun filterSummary(state: SearchUiState): String {
    val any = stringResource(R.string.filter_summary_any)
    val parts = buildList {
        state.city.trim().takeIf { it.isNotEmpty() }?.let(::add)
        state.keyword.trim().takeIf { it.isNotEmpty() }?.let {
            add(stringResource(R.string.filter_summary_keyword, it))
        }
        if (state.minPrice.isNotBlank() || state.maxPrice.isNotBlank()) {
            add(
                stringResource(
                    R.string.filter_summary_price_range,
                    shortPrice(state.minPrice) ?: any,
                    shortPrice(state.maxPrice) ?: any,
                )
            )
        }
        state.minBedrooms.takeIf { it.isNotBlank() }?.let {
            add(stringResource(R.string.filter_summary_beds, it))
        }
    }
    return if (parts.isEmpty()) {
        stringResource(R.string.filter_summary_all)
    } else {
        parts.joinToString(stringResource(R.string.filter_summary_separator))
    }
}

@Composable
private fun shortPrice(value: String): String? {
    val amount = value.toDoubleOrNull() ?: return null
    return when {
        amount >= 1_000_000 -> {
            val millions = (amount / 100_000).roundToInt() / 10.0
            val text = if (millions % 1.0 == 0.0) millions.toInt().toString() else millions.toString()
            stringResource(R.string.price_short_millions, text)
        }
        amount >= 1_000 -> stringResource(R.string.price_short_thousands, (amount / 1_000).roundToInt().toString())
        else -> stringResource(R.string.price_short_plain, amount.roundToInt().toString())
    }
}

private fun formatBath(value: Double) = if (value % 1.0 == 0.0) value.toInt().toString() else value.toString()
