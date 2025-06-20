package com.example.talkeys_new.screens.authentication


import com.example.talkeys_new.dataModels.UserResponse
import retrofit2.http.POST
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Header

interface AuthService {
    @POST("verify")
    suspend fun verifyToken(
        @Header("Authorization") bearerToken: String
    ): Response<UserResponse>

    // Helper to build Retrofit
    object RetrofitClient {
        private const val BASE_URL = "https://api.talkeys.xyz/"
        val instance: AuthService by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(AuthService::class.java)
        }
    }
}