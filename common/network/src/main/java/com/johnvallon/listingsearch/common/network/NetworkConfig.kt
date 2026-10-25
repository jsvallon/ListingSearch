package com.johnvallon.listingsearch.common.network

/**
 * Declares the base URL used by the app's Retrofit client.
 */
interface NetworkConfig {
    val baseUrl: String
}

/**
 * Production configuration for the local Android emulator to reach the assessment API.
 */
class DefaultNetworkConfig : NetworkConfig {
    override val baseUrl = "http://10.0.2.2:8000/"
}
