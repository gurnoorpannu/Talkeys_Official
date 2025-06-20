package com.example.talkeys_new.screens.authentication


import com.example.talkeys_new.dataModels.UserResponse
import retrofit2.http.POST
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Header

// This interface defines the structure for our API calls related to authentication
interface AuthService {
    // This function makes a POST request to the "verify" endpoint
    // It takes the JWT token in the Authorization header (prefixed with "Bearer ")
    // The function is marked with `suspend` because it's a coroutine (runs asynchronously)
    // It returns a Response object wrapping UserResponse, which contains data from the server
    @POST("verify")
    suspend fun verifyToken(
        @Header("Authorization") bearerToken: String
    ): Response<UserResponse>

    // This object helps us create an instance of the AuthService using Retrofit
    object RetrofitClient {
        // The base URL of our backend server (all API calls will be made relative to this URL)
        private const val BASE_URL = "https://api.talkeys.xyz/"

        // 'instance' is the singleton object that we use to access AuthService functions
        val instance: AuthService by lazy {
            // We build the Retrofit instance only once (lazy initialization)
            Retrofit.Builder()
                .baseUrl(BASE_URL) // Set the base URL for all network calls
                .addConverterFactory(GsonConverterFactory.create()) // Convert JSON responses to Kotlin objects
                .build() // Build the Retrofit object
                .create(AuthService::class.java) // Create an implementation of AuthService interface
        }
    }
}