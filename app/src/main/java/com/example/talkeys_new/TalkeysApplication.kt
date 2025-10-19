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
        val environment = if (PhonePeConfig.IS_PRODUCTION) {
            PhonePeEnvironment.RELEASE
        } else {
            PhonePeEnvironment.SANDBOX
        }
        
        val result = PhonePeKt.init(
            context = this,
            merchantId = PhonePeConfig.CLIENT_ID, // Using Client ID as merchantId parameter
            flowId = PhonePeConfig.generateFlowId(),
            phonePeEnvironment = environment, // ✅ Use the correct environment
            enableLogging = !PhonePeConfig.IS_PRODUCTION, // ✅ Disable logging in production
            appId = null // Optional parameter
        )
        
        if (result) {
            // PhonePe SDK initialized successfully
            android.util.Log.d("PhonePe", "PhonePe SDK initialized successfully")
            android.util.Log.d("PhonePe", "Environment: ${PhonePeConfig.getEnvironmentName()}")
            android.util.Log.d("PhonePe", "Client ID: ${PhonePeConfig.CLIENT_ID}")
        } else {
            // Some error occurred in SDK initialization
            android.util.Log.e("PhonePe", "PhonePe SDK initialization failed")
            android.util.Log.e("PhonePe", "Environment: ${PhonePeConfig.getEnvironmentName()}")
            // NOTE: SDK is not in the state to use. Hence, no other method should be called.
        }
    }
}