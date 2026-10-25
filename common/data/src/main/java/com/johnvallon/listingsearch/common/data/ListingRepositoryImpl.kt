package com.johnvallon.listingsearch.common.data

import com.johnvallon.listingsearch.common.model.Listing
import com.johnvallon.listingsearch.common.model.SearchCriteria
import com.johnvallon.listingsearch.common.model.SearchResult
import com.johnvallon.listingsearch.common.network.ListingApiService
import com.johnvallon.listingsearch.common.network.dto.ListingDto
import javax.inject.Inject

/**
 * Repository implementation that reads listing data from the network API and converts it to app
 * domain models.
 */
class ListingRepositoryImpl @Inject constructor(private val api: ListingApiService) : ListingRepository {
    /**
     * Executes a listing search using the provided criteria.
     */
    override suspend fun search(criteria: SearchCriteria): SearchResult {
        val response = api.searchListings(
            minPrice = criteria.minPrice, maxPrice = criteria.maxPrice,
            city = criteria.city, keyword = criteria.keyword,
            minBedrooms = criteria.minBedrooms, targetBudget = criteria.targetBudget,
            page = criteria.page, pageSize = criteria.pageSize,
        )
        return SearchResult(response.results.map { it.toModel() }, response.page, response.pageSize, response.totalResults, response.totalPages)
    }
}

/**
 * Maps a network DTO into the app-level listing domain model.
 */
private fun ListingDto.toModel() = Listing(
    id = id, address = address, city = city, state = state, zip = zip,
    price = price, bedrooms = bedrooms, bathrooms = bathrooms, squareFeet = sqft,
    listedDate = listedDate, status = status, description = description, score = score,
)
