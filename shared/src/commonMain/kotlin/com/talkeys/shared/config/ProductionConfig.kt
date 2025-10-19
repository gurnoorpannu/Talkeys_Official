package com.talkeys.shared.config

/**
 * Production Configuration
 * Centralized configuration for production environment
 */
object ProductionConfig {
    
    // Environment Settings
    const val IS_PRODUCTION = true // Production mode - backend is ready
    const val IS_DEBUG_LOGGING_ENABLED = false
    
    // API Configuration
    const val API_BASE_URL = "https://api.talkeys.xyz"
    const val API_TIMEOUT_SECONDS = 30L
    const val API_RETRY_COUNT = 3
    
    // PhonePe Configuration
    const val PHONEPE_ENVIRONMENT = "PRODUCTION" // "SANDBOX" or "PRODUCTION"
    const val PHONEPE_CLIENT_ID = "SU2504181253408025787154"
    
    // Payment Configuration
    const val DEFAULT_CURRENCY = "INR"
    const val PAYMENT_TIMEOUT_MINUTES = 15
    
    // Security Settings
    const val ENABLE_SSL_PINNING = true
    const val ENABLE_REQUEST_ENCRYPTION = true
    
    // Feature Flags
    const val ENABLE_PAYMENT_ANALYTICS = true
    const val ENABLE_CRASH_REPORTING = true
    const val ENABLE_PERFORMANCE_MONITORING = true
    
    // Validation Settings
    const val MIN_PAYMENT_AMOUNT = 1.0
    const val MAX_PAYMENT_AMOUNT = 100000.0
    const val MAX_FRIENDS_PER_BOOKING = 10
    
    /**
     * Get API base URL based on environment
     */
    fun getApiBaseUrl(): String {
        return if (IS_PRODUCTION) {
            API_BASE_URL
        } else {
            "https://staging.api.talkeys.xyz" // Fallback for staging
        }
    }
    
    /**
     * Get PhonePe environment string
     */
    fun getPhonePeEnvironment(): String {
        return if (IS_PRODUCTION) "PRODUCTION" else "SANDBOX"
    }
    
    /**
     * Check if feature is enabled
     */
    fun isFeatureEnabled(feature: String): Boolean {
        return when (feature) {
            "payment_analytics" -> ENABLE_PAYMENT_ANALYTICS
            "crash_reporting" -> ENABLE_CRASH_REPORTING
            "performance_monitoring" -> ENABLE_PERFORMANCE_MONITORING
            else -> false
        }
    }
}