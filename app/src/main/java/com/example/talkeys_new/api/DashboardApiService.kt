package com.example.talkeys_new.api

import com.example.talkeys_new.dataModels.EventResponse
import retrofit2.Response
import retrofit2.http.*

data class UserEventsResponse(
    val events: List<EventResponse>
)

data class UserProfileResponse(
    val _id: String,
    val name: String,
    val email: String,
    val displayName: String? = null,
    val about: String? = null,
    val pronouns: String? = null,
    val avatarUrl: String? = null,
    val likedEvents: List<String> = emptyList()
)

interface DashboardApiService {
    
    @GET("dashboard/profile")
    suspend fun getUserProfile(
        @Header("Authorization") token: String
    ): Response<UserProfileResponse>
    
    @PATCH("dashboard/profile")
    suspend fun updateUserProfile(
        @Header("Authorization") token: String,
        @Body profileData: Map<String, String>
    ): Response<UserProfileResponse>
    
    @GET("dashboard/events")
    suspend fun getUserEvents(
        @Header("Authorization") token: String,
        @Query("type") type: String, // "registered", "bookmarked", "hosted"
        @Query("status") status: String? = null, // "past", "upcoming"
        @Query("period") period: String? = null // "1m", "6m", "1y"
    ): Response<UserEventsResponse>
    
    @GET("dashboard/activity")
    suspend fun getRecentActivity(
        @Header("Authorization") token: String,
        @Query("range") range: String? = "1m"
    ): Response<Map<String, Any>>
}