package com.johnvallon.listingsearch.common.data

import com.johnvallon.listingsearch.common.model.SearchCriteria
import javax.inject.Inject

/**
 * Use case that delegates a search request to the listing repository.
 */
class SearchListingsUseCase @Inject constructor(private val repository: ListingRepository) {
    /**
     * Runs the repository query for the provided search filters.
     *
     * @param criteria Filter values to pass to the repository.
     * @return The matching search results.
     */
    suspend operator fun invoke(criteria: SearchCriteria) = repository.search(criteria)
}
