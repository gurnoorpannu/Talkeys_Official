package com.example.talkeys_new.utils

/**
 * PhonePe Configuration
 * 
 * SECURITY WARNING: 
 * - These are your REAL PhonePe credentials
 * - Keep this file secure and never commit to public repositories
 * - Consider using BuildConfig or encrypted storage for production
 */
object PhonePeConfig {
    
    // Your PhonePe Business Dashboard Credentials (Exact terminology from dashboard)
    const val MERCHANT_ID = "M22ZDT307F584"  // PRODUCTION Merchant ID
    const val CLIENT_ID = "SU2504181253408025787154"  // PRODUCTION Client Id
    const val CLIENT_SECRET = "4d9bafd5-1172-4695-a212-beb19efd70ed"  // PRODUCTION Client Secret
    
    // Environment Configuration
    const val IS_PRODUCTION = true // ⚠️ PRODUCTION MODE - REAL MONEY WILL BE CHARGED!
    
    // API Endpoints (you'll need these for backend integration)
    const val SANDBOX_BASE_URL = "https://api-preprod.phonepe.com/apis/pg-sandbox"
    const val PRODUCTION_BASE_URL = "https://api.phonepe.com/apis/hermes"
    
    fun getBaseUrl(): String {
        return if (IS_PRODUCTION) PRODUCTION_BASE_URL else SANDBOX_BASE_URL
    }
    
    fun getEnvironmentName(): String {
        return if (IS_PRODUCTION) "PRODUCTION" else "SANDBOX"
    }
    
    /**
     * Generate unique flow ID for each payment session
     */
    fun generateFlowId(): String {
        return "TALKEYS_${System.currentTimeMillis()}"
    }
    
    /**
     * IMPORTANT NOTES:
     * 
     * 1. CLIENT_SECRET is used for:
     *    - Creating payment requests on your backend
     *    - Generating checksums for API calls
     *    - Verifying webhook responses
     *    - NEVER use this in the mobile app directly
     * 
     * 2. CLIENT_ID is used for:
     *    - SDK initialization (safe to use in app)
     *    - API requests identification
     * 
     * 3. For Production:
     *    - Change IS_PRODUCTION to true
     *    - Set enableLogging to false in TalkeysApplication
     *    - Use PhonePeEnvironment.RELEASE
     *    
     *    Currently in TEST mode:
     *    - IS_PRODUCTION = false
     *    - Using SANDBOX environment
     *    - Logging enabled for debugging
     * 
     * 4. Backend Integration Required:
     *    - Create Order API: Use CLIENT_SECRET to generate checksum
     *    - Order Status API: Use CLIENT_SECRET to verify response
     *    - Webhook handling: Use CLIENT_SECRET to validate callbacks
     */
}