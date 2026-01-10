package com.example.talkeys_new.network

import com.example.talkeys_new.BuildConfig
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

/**
 * NetworkConfig provides centralized network security configuration.
 * 
 * Features:
 * - Certificate pinning for API endpoints
 * - Configurable network timeouts
 * - Debug-only HTTP logging
 * - Reusable OkHttpClient factory
 */
object NetworkConfig {

    private const val API_HOST = "api.talkeys.xyz"
    
    // Network timeout configuration (in seconds)
    private const val CONNECT_TIMEOUT = 30L
    private const val READ_TIMEOUT = 30L
    private const val WRITE_TIMEOUT = 30L

    /**
     * Certificate pinning configuration for API endpoints.
     * 
     * IMPORTANT: These are placeholder SHA-256 pins.
     * Replace with actual certificate pins before production deployment.
     * 
     * To get your certificate pins, run:
     * openssl s_client -connect api.talkeys.xyz:443 | openssl x509 -pubkey -noout | openssl pkey -pubin -outform der | openssl dgst -sha256 -binary | openssl enc -base64
     */
    private val certificatePinner = CertificatePinner.Builder()
        .add(
            API_HOST,
            // Placeholder pins - MUST be replaced with actual pins
            "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=",
            "sha256/BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB="
        )
        .build()

    /**
     * Creates a configured OkHttpClient with security settings.
     * 
     * @param enableCertificatePinning Whether to enable certificate pinning (default: false for development)
     * @return Configured OkHttpClient instance
     */
    fun createOkHttpClient(enableCertificatePinning: Boolean = false): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)

        // Enable certificate pinning only when explicitly requested
        // Typically enabled in production builds
        if (enableCertificatePinning) {
            builder.certificatePinner(certificatePinner)
        }

        // Add logging interceptor only in DEBUG builds
        // This prevents sensitive data from being logged in production
        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            builder.addInterceptor(loggingInterceptor)
        }

        return builder.build()
    }

    /**
     * Creates an OkHttpClient with custom interceptors.
     * Useful when you need to add authentication or other custom interceptors.
     * 
     * @param interceptors List of custom interceptors to add
     * @param enableCertificatePinning Whether to enable certificate pinning
     * @return Configured OkHttpClient instance
     */
    fun createOkHttpClientWithInterceptors(
        interceptors: List<okhttp3.Interceptor>,
        enableCertificatePinning: Boolean = false
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)

        // Add custom interceptors first (e.g., auth interceptor)
        interceptors.forEach { builder.addInterceptor(it) }

        // Enable certificate pinning if requested
        if (enableCertificatePinning) {
            builder.certificatePinner(certificatePinner)
        }

        // Add logging interceptor last (only in DEBUG)
        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            builder.addInterceptor(loggingInterceptor)
        }

        return builder.build()
    }
}
