package com.johnvallon.listingsearch.common.network

import com.johnvallon.listingsearch.common.network.dto.SearchResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit API contract for the property search backend.
 */
interface ListingApiService {
    /**
     * Searches for listings matching the supplied filters.
     *
     * @param minPrice Minimum price filter.
     * @param maxPrice Maximum price filter.
     * @param city City name filter.
     * @param keyword Keyword text match.
     * @param minBedrooms Minimum bedroom filter.
     * @param targetBudget Budget filter used by the API.
     * @param page Requested page index.
     * @param pageSize Number of results requested per page.
     * @return The paginated response containing listing data.
     */
    @GET("listings/search")
    suspend fun searchListings(
        @Query("minPrice") minPrice: Double? = null,
        @Query("maxPrice") maxPrice: Double? = null,
        @Query("city") city: String? = null,
        @Query("keyword") keyword: String? = null,
        @Query("minBedrooms") minBedrooms: Int? = null,
        @Query("targetBudget") targetBudget: Double? = null,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 10,
    ): SearchResponseDto
}
