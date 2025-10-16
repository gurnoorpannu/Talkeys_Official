package com.example.talkeys_new

import android.app.Application
import com.phonepe.intent.sdk.api.PhonePeKt
import com.phonepe.intent.sdk.api.models.PhonePeEnvironment

class TalkeysApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize PhonePe SDK
        initializePhonePeSDK()
    }
    
    private fun initializePhonePeSDK() {
        val result = PhonePeKt.init(
            context = this,
            merchantId = "MID", // Replace with your actual Merchant ID
            flowId = "FLOW_ID", // Replace with your actual Flow ID or unique user ID
            phonePeEnvironment = PhonePeEnvironment.SANDBOX, // Use RELEASE for production
            enableLogging = true, // Set to false in production
            appId = null // Optional parameter
        )
        
        if (result) {
            // PhonePe SDK initialized successfully
            android.util.Log.d("PhonePe", "PhonePe SDK initialized successfully")
        } else {
            // Some error occurred in SDK initialization
            android.util.Log.e("PhonePe", "PhonePe SDK initialization failed")
            // NOTE: SDK is not in the state to use. Hence, no other method should be called.
        }
    }
}