package com.johnvallon.listingsearch.common.model

/**
 * Domain model representing a real-estate listing returned by the service.
 */
data class Listing(
    val id: String,
    val address: String,
    val city: String,
    val state: String,
    val zip: String,
    val price: Double,
    val bedrooms: Int,
    val bathrooms: Double,
    val squareFeet: Int,
    val listedDate: String,
    val status: String,
    val description: String,
    val score: Double,
)

/**
 * Search filters used to query listings from the repository.
 */
data class SearchCriteria(
    val minPrice: Double? = null,
    val maxPrice: Double? = null,
    val city: String? = null,
    val keyword: String? = null,
    val minBedrooms: Int? = null,
    val targetBudget: Double? = null,
    val page: Int = 1,
    val pageSize: Int = 10,
)

/**
 * The paged response returned by the repository for a property search.
 */
data class SearchResult(
    val listings: List<Listing>,
    val page: Int,
    val pageSize: Int,
    val totalResults: Int,
    val totalPages: Int,
)
