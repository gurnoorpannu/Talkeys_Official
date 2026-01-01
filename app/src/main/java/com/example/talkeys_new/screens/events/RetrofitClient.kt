package com.example.talkeys_new.screens.events

import android.content.Context
import com.example.talkeys_new.network.NetworkConfig
import com.example.talkeys_new.screens.authentication.TokenManager
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

fun provideEventApiService(context: Context): EventApiService {
    val tokenManager = TokenManager(context)

    // Use centralized network configuration with AuthInterceptor
    val client = NetworkConfig.createOkHttpClientWithInterceptors(
        interceptors = listOf(AuthInterceptor(tokenManager)),
        enableCertificatePinning = false // Enable in production
    )

    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.talkeys.xyz/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    return retrofit.create(EventApiService::class.java)
}