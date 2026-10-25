package com.johnvallon.listingsearch.common.data.di

import com.johnvallon.listingsearch.common.data.ListingRepository
import com.johnvallon.listingsearch.common.data.ListingRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that binds the repository implementation to the repository contract.
 */
@Module @InstallIn(SingletonComponent::class)
abstract class DataModule {
    /**
     * Provides the concrete repository implementation to the app's dependency graph.
     */
    @Binds @Singleton abstract fun bindListingRepository(impl: ListingRepositoryImpl): ListingRepository
}
