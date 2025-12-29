package com.example.talkeys_new

import android.app.Application
import com.phonepe.intent.sdk.api.PhonePeKt
import com.phonepe.intent.sdk.api.models.PhonePeEnvironment
import com.example.talkeys_new.utils.PhonePeConfig

class TalkeysApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize PhonePe SDK
        initializePhonePeSDK()
    }
    
    private fun initializePhonePeSDK() {
        try {
            val environment = if (PhonePeConfig.IS_PRODUCTION) {
                PhonePeEnvironment.RELEASE
            } else {
                PhonePeEnvironment.SANDBOX
            }
            
            android.util.Log.d("PhonePe", "🚀 Initializing PhonePe SDK...")
            android.util.Log.d("PhonePe", "📱 Environment: ${PhonePeConfig.getEnvironmentName()}")
            android.util.Log.d("PhonePe", "🏪 Merchant ID: ${PhonePeConfig.MERCHANT_ID}")
            android.util.Log.d("PhonePe", "🔑 Client ID: ${PhonePeConfig.CLIENT_ID}")
            
            val result = PhonePeKt.init(
                context = this,
                merchantId = PhonePeConfig.MERCHANT_ID,
                flowId = PhonePeConfig.generateFlowId(),
                phonePeEnvironment = environment,
                enableLogging = !PhonePeConfig.IS_PRODUCTION, // Enable logging in debug
                appId = null // Optional - can be your app's package name
            )
            
            if (result) {
                android.util.Log.d("PhonePe", "✅ PhonePe SDK initialized successfully")
                android.util.Log.d("PhonePe", "🎯 Ready for payments in ${PhonePeConfig.getEnvironmentName()} mode")
                
                // Verify SDK state
                verifySDKState()
            } else {
                android.util.Log.e("PhonePe", "❌ PhonePe SDK initialization failed")
                android.util.Log.e("PhonePe", "💡 Check merchant credentials and environment settings")
                android.util.Log.e("PhonePe", "💡 Ensure backend and mobile SDK use same environment")
                
                // Log troubleshooting info
                logTroubleshootingInfo()
            }
        } catch (e: Exception) {
            android.util.Log.e("PhonePe", "💥 Exception during SDK initialization: ${e.message}", e)
            logTroubleshootingInfo()
        }
    }
    
    /**
     * Verify SDK state after initialization
     */
    private fun verifySDKState() {
        try {
            android.util.Log.d("PhonePe", "🔍 Verifying SDK state...")
            
            // Check if PhonePe app is installed
            val packageManager = packageManager
            try {
                packageManager.getPackageInfo("com.phonepe.app", 0)
                android.util.Log.d("PhonePe", "📱 PhonePe app is installed - will use native app")
            } catch (e: Exception) {
                android.util.Log.d("PhonePe", "🌐 PhonePe app not installed - will use WebView fallback")
            }
            
            android.util.Log.d("PhonePe", "✅ SDK verification complete")
            
        } catch (e: Exception) {
            android.util.Log.w("PhonePe", "⚠️ SDK verification failed: ${e.message}")
        }
    }
    
    /**
     * Log troubleshooting information
     */
    private fun logTroubleshootingInfo() {
        android.util.Log.e("PhonePe", "🔧 TROUBLESHOOTING INFO:")
        android.util.Log.e("PhonePe", "  - Merchant ID: ${PhonePeConfig.MERCHANT_ID}")
        android.util.Log.e("PhonePe", "  - Client ID: ${PhonePeConfig.CLIENT_ID}")
        android.util.Log.e("PhonePe", "  - Environment: ${PhonePeConfig.getEnvironmentName()}")
        android.util.Log.e("PhonePe", "  - Production Mode: ${PhonePeConfig.IS_PRODUCTION}")
        android.util.Log.e("PhonePe", "")
        android.util.Log.e("PhonePe", "💡 COMMON SOLUTIONS:")
        android.util.Log.e("PhonePe", "  1. Verify merchant credentials in PhonePeConfig")
        android.util.Log.e("PhonePe", "  2. Ensure backend uses same environment (PRODUCTION/SANDBOX)")
        android.util.Log.e("PhonePe", "  3. Check internet connectivity")
        android.util.Log.e("PhonePe", "  4. Restart app to reinitialize SDK")
    }
}