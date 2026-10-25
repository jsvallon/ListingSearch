package com.johnvallon.listingsearch.feature.search

import com.johnvallon.listingsearch.common.data.ListingRepository
import com.johnvallon.listingsearch.common.data.SearchListingsUseCase
import com.johnvallon.listingsearch.common.model.SearchCriteria
import com.johnvallon.listingsearch.common.model.SearchResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    @Before fun setUp() = Dispatchers.setMain(dispatcher)
    @After fun tearDown() = Dispatchers.resetMain()

    @Test fun invalidPriceRange_isRejectedBeforeRepositoryCall() = runTest(dispatcher) {
        val repository = FakeRepository()
        val vm = SearchViewModel(SearchListingsUseCase(repository))
        vm.onAction(SearchAction.MinPriceChanged("600000")); vm.onAction(SearchAction.MaxPriceChanged("400000")); vm.onAction(SearchAction.SearchClicked)
        assertNotNull(vm.uiState.value.errorMessage)
        assertEquals(0, repository.calls)
    }

    @Test fun successfulSearch_updatesResults() = runTest(dispatcher) {
        val repository = FakeRepository()
        val vm = SearchViewModel(SearchListingsUseCase(repository))
        vm.onAction(SearchAction.CityChanged("Reston")); vm.onAction(SearchAction.SearchClicked); dispatcher.scheduler.advanceUntilIdle()
        assertEquals(true, vm.uiState.value.hasSearched)
        assertEquals(0, vm.uiState.value.listings.size)
        assertEquals(1, repository.calls)
    }
}
private class FakeRepository : ListingRepository {
    var calls = 0
    override suspend fun search(criteria: SearchCriteria): SearchResult { calls++; return SearchResult(emptyList(),1,10,0,1) }
}
