package com.example.talkeys_new.screens.events

import android.content.Context
import com.example.talkeys_new.screens.authentication.TokenManager
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.logging.HttpLoggingInterceptor

fun provideEventApiService(context: Context): EventApiService {
    val tokenManager = TokenManager(context)

    // Enable logging to debug API calls
    val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    val client = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor(tokenManager))
        .addInterceptor(logging) // Enable this to see API requests/responses
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.talkeys.xyz/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    return retrofit.create(EventApiService::class.java)
}