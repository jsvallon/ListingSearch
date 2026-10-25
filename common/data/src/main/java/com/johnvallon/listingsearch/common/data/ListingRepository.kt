package com.johnvallon.listingsearch.common.data

import com.johnvallon.listingsearch.common.model.SearchCriteria
import com.johnvallon.listingsearch.common.model.SearchResult

/**
 * Repository contract for retrieving listings based on the provided search criteria.
 */
interface ListingRepository {
    /**
     * Searches for listings that match the provided filters.
     *
     * @param criteria The filter criteria to apply.
     * @return The paginated result set for the matching listings.
     */
    suspend fun search(criteria: SearchCriteria): SearchResult
}
