package com.johnvallon.listingsearch.common.network.di

import com.johnvallon.listingsearch.common.network.ListingApiService
import com.johnvallon.listingsearch.common.network.NetworkConfig
import com.johnvallon.listingsearch.common.network.DefaultNetworkConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import okhttp3.MediaType.Companion.toMediaType
import javax.inject.Singleton

/**
 * Hilt module that wires the network stack for Retrofit and the API service.
 */
@Module @InstallIn(SingletonComponent::class)
object NetworkModule {
    /**
     * Creates the JSON serializer used for API responses.
     */
    @Provides @Singleton fun json(): Json = Json { ignoreUnknownKeys = true; coerceInputValues = true }

    /**
     * Supplies the app's base network configuration.
     */
    @Provides @Singleton fun config(): NetworkConfig = DefaultNetworkConfig()

    /**
     * Creates the shared OkHttp client used by Retrofit.
     */
    @Provides @Singleton fun okHttp(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }).build()

    /**
     * Initializes the Retrofit client with the app's JSON converter and base URL.
     */
    @Provides @Singleton fun retrofit(json: Json, client: OkHttpClient, config: NetworkConfig): Retrofit = Retrofit.Builder()
        .baseUrl(config.baseUrl)
        .client(client)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    /**
     * Exposes the generated Retrofit API interface for dependency injection.
     */
    @Provides @Singleton fun api(retrofit: Retrofit): ListingApiService = retrofit.create(ListingApiService::class.java)
}
