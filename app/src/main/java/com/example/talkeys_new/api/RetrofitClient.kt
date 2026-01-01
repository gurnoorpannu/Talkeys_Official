package com.example.talkeys_new.api

import com.example.talkeys_new.network.NetworkConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://api.talkeys.xyz/"
    
    private fun getRetrofit(): Retrofit {
        // Use centralized network configuration with security settings
        val client = NetworkConfig.createOkHttpClient(
            enableCertificatePinning = false // Enable in production
        )
        
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }
    
    fun provideDashboardApiService(): DashboardApiService {
        return getRetrofit().create(DashboardApiService::class.java)
    }
}
