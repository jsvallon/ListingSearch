package com.johnvallon.listingsearch.cucumber

import com.johnvallon.listingsearch.common.data.ListingRepository
import com.johnvallon.listingsearch.common.data.di.DataModule
import com.johnvallon.listingsearch.common.model.Listing
import com.johnvallon.listingsearch.common.model.SearchCriteria
import com.johnvallon.listingsearch.common.model.SearchResult
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import java.io.IOException
import javax.inject.Singleton

/**
 * Stands in for the real listing service during scenarios, so they don't need server.py and
 * always see the same data. Steps can switch it to "unavailable" to test the error screen.
 *
 * It filters the same way server.py does: city is an exact match ignoring case, keyword is
 * "contains" on address + description, plus price range and minimum bedrooms.
 */
object FakeListingRepository : ListingRepository {
    var isAvailable = true

    val listings = listOf(
        listing("1", "1820 Wiehle Avenue", "Reston", "20190", 425_000.0, 3, 2.0, 1_850, "Townhouse with garage"),
        listing("2", "11400 Washington Plaza W #604", "Reston", "20190", 389_000.0, 2, 2.0, 1_210, "Condo with lake view"),
        listing("3", "2250 Hunters Woods Road", "Reston", "20191", 612_000.0, 4, 3.0, 2_640, "Single family with yard"),
        listing("4", "1610 Lakeport Way", "Reston", "20194", 515_000.0, 3, 2.5, 2_050, "Townhouse near the lake trail"),
        listing("5", "12010 Stonewood Square", "Reston", "20191", 549_000.0, 3, 3.0, 1_980, "Renovated townhouse"),
        listing("6", "745 Elden Street", "Herndon", "20170", 469_000.0, 3, 2.0, 1_720, "Renovated with yard"),
    )

    fun reset() {
        isAvailable = true
    }

    override suspend fun search(criteria: SearchCriteria): SearchResult {
        if (!isAvailable) throw IOException("Listing service unavailable (test)")
        val matches = listings.filter { listing ->
            (criteria.city == null || listing.city.equals(criteria.city!!, ignoreCase = true)) &&
                (criteria.keyword == null ||
                    "${listing.address} ${listing.description}".contains(criteria.keyword!!, ignoreCase = true)) &&
                (criteria.minPrice == null || listing.price >= criteria.minPrice!!) &&
                (criteria.maxPrice == null || listing.price <= criteria.maxPrice!!) &&
                (criteria.minBedrooms == null || listing.bedrooms >= criteria.minBedrooms!!)
        }
        return SearchResult(matches, page = 1, pageSize = matches.size, totalResults = matches.size, totalPages = 1)
    }

    private fun listing(
        id: String, address: String, city: String, zip: String, price: Double,
        bedrooms: Int, bathrooms: Double, squareFeet: Int, description: String,
    ) = Listing(
        id = id, address = address, city = city, state = "VA", zip = zip, price = price,
        bedrooms = bedrooms, bathrooms = bathrooms, squareFeet = squareFeet,
        listedDate = "2026-10-01", status = "active", description = description, score = 0.0,
    )
}

/** Replaces the app's real repository binding with [FakeListingRepository] in all scenarios. */
@Module
@TestInstallIn(components = [SingletonComponent::class], replaces = [DataModule::class])
object FakeDataModule {
    @Provides
    @Singleton
    fun listingRepository(): ListingRepository = FakeListingRepository
}
