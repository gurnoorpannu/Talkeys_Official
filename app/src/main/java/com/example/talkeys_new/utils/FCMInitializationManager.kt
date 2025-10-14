package com.example.talkeys_new.utils

import android.content.Context
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.messaging.FirebaseMessaging

object FCMInitializationManager {
    private const val TAG = "FCMInitManager"

    /**
     * Check if FCM auto-initialization is enabled
     */
    fun isAutoInitEnabled(): Boolean {
        val isEnabled = FirebaseMessaging.getInstance().isAutoInitEnabled
        Log.d(TAG, "FCM Auto-init enabled: $isEnabled")
        return isEnabled
    }

    /**
     * Enable FCM auto-initialization
     * This will automatically generate tokens and enable messaging
     */
    fun enableAutoInit() {
        Log.d(TAG, "🔄 Enabling FCM auto-initialization...")
        val wasEnabled = FirebaseMessaging.getInstance().isAutoInitEnabled
        
        FirebaseMessaging.getInstance().isAutoInitEnabled = true
        
        if (!wasEnabled) {
            Log.d(TAG, "✅ FCM auto-initialization enabled (was previously disabled)")
            Log.d(TAG, "🔑 Token generation will now begin automatically")
        } else {
            Log.d(TAG, "ℹ️ FCM auto-initialization was already enabled")
        }
    }

    /**
     * Disable FCM auto-initialization
     * This prevents automatic token generation
     */
    fun disableAutoInit() {
        Log.d(TAG, "🔄 Disabling FCM auto-initialization...")
        FirebaseMessaging.getInstance().isAutoInitEnabled = false
        Log.d(TAG, "❌ FCM auto-initialization disabled")
    }

    /**
     * Check if Firebase Analytics collection is enabled
     */
    fun isAnalyticsEnabled(context: Context): Boolean {
        // Note: There's no direct way to check this, so we'll use a stored preference
        val sharedPref = context.getSharedPreferences("fcm_prefs", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("analytics_enabled", true) // Default to true
    }

    /**
     * Enable Firebase Analytics collection
     */
    fun enableAnalytics(context: Context) {
        Log.d(TAG, "🔄 Enabling Firebase Analytics...")
        Firebase.analytics.setAnalyticsCollectionEnabled(true)
        
        // Store the state
        val sharedPref = context.getSharedPreferences("fcm_prefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putBoolean("analytics_enabled", true)
            apply()
        }
        Log.d(TAG, "✅ Firebase Analytics enabled")
    }

    /**
     * Disable Firebase Analytics collection
     */
    fun disableAnalytics(context: Context) {
        Log.d(TAG, "🔄 Disabling Firebase Analytics...")
        Firebase.analytics.setAnalyticsCollectionEnabled(false)
        
        // Store the state
        val sharedPref = context.getSharedPreferences("fcm_prefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putBoolean("analytics_enabled", false)
            apply()
        }
        Log.d(TAG, "❌ Firebase Analytics disabled")
    }

    /**
     * Get comprehensive initialization status
     */
    fun getInitializationStatus(context: Context): InitializationStatus {
        return InitializationStatus(
            fcmAutoInitEnabled = isAutoInitEnabled(),
            analyticsEnabled = isAnalyticsEnabled(context)
        )
    }

    /**
     * Log current initialization status
     */
    fun logInitializationStatus(context: Context) {
        val status = getInitializationStatus(context)
        Log.d(TAG, "=== FCM Initialization Status ===")
        Log.d(TAG, "FCM Auto-Init: ${if (status.fcmAutoInitEnabled) "✅ Enabled" else "❌ Disabled"}")
        Log.d(TAG, "Analytics: ${if (status.analyticsEnabled) "✅ Enabled" else "❌ Disabled"}")
        
        if (!status.fcmAutoInitEnabled) {
            Log.d(TAG, "🔒 FCM is disabled - no tokens will be generated")
            Log.d(TAG, "📋 User consent required to enable notifications")
        }
        
        if (!status.analyticsEnabled) {
            Log.d(TAG, "🔒 Analytics disabled - no usage data collected")
        }
        
        Log.d(TAG, "================================")
    }

    /**
     * Check if FCM was disabled by manifest configuration
     */
    fun wasDisabledByManifest(): Boolean {
        // If auto-init is disabled and we haven't explicitly enabled it,
        // it was likely disabled by manifest
        return !isAutoInitEnabled()
    }

    /**
     * Enable both FCM and Analytics (recommended for production)
     */
    fun enableAll(context: Context) {
        Log.d(TAG, "🚀 Enabling all Firebase services...")
        
        if (wasDisabledByManifest()) {
            Log.d(TAG, "🔓 Overriding manifest settings - enabling FCM")
        }
        
        enableAutoInit()
        enableAnalytics(context)
        Log.d(TAG, "✅ All Firebase services enabled")
        Log.d(TAG, "💡 These settings persist across app restarts")
    }

    /**
     * Disable both FCM and Analytics (for privacy-focused scenarios)
     */
    fun disableAll(context: Context) {
        Log.d(TAG, "🛑 Disabling all Firebase services...")
        disableAutoInit()
        disableAnalytics(context)
        Log.d(TAG, "❌ All Firebase services disabled")
    }

    data class InitializationStatus(
        val fcmAutoInitEnabled: Boolean,
        val analyticsEnabled: Boolean
    )
}