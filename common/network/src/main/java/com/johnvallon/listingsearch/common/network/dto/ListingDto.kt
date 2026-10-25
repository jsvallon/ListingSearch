package com.johnvallon.listingsearch.common.network.dto

import kotlinx.serialization.Serializable

/**
 * API payload for a single listing returned by the backend search endpoint.
 */
@Serializable
data class ListingDto(
    val id: String,
    val source: String? = null,
    val address: String,
    val city: String,
    val state: String,
    val zip: String,
    val price: Double,
    val bedrooms: Int,
    val bathrooms: Double,
    val sqft: Int,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val listedDate: String,
    val status: String,
    val description: String,
    val amenities: AmenitiesDto? = null,
    val duplicateGroupId: String? = null,
    val nearDuplicate: NearDuplicateDto? = null,
    val score: Double = 0.0,
)

/**
 * Optional amenity metadata included in a listing payload.
 */
@Serializable
data class AmenitiesDto(val petFriendly: Boolean = false, val parking: Boolean = false)

/**
 * Metadata that indicates the listing is a near-duplicate of another property.
 */
@Serializable
data class NearDuplicateDto(val ofId: String, val confidence: Double)

/**
 * Response model returned by the listings search API.
 */
@Serializable
data class SearchResponseDto(
    val results: List<ListingDto>,
    val page: Int,
    val pageSize: Int,
    val totalResults: Int,
    val totalPages: Int,
)

/**
 * Error payload returned by the API when a request fails.
 */
@Serializable
data class ErrorResponseDto(val error: String)
