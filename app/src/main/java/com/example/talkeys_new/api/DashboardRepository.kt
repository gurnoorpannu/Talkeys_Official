package com.example.talkeys_new.api

import android.util.Log
import com.example.talkeys_new.dataModels.EventResponse
import com.example.talkeys_new.screens.authentication.TokenManager
import com.example.talkeys_new.utils.Constants
import com.example.talkeys_new.utils.NetworkUtils
import com.example.talkeys_new.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

/**
 * Repository class that handles all dashboard-related API calls
 * Implements proper error handling using Result wrapper and follows clean architecture principles
 */
class DashboardRepository(
    private val apiService: DashboardApiService,
    private val tokenManager: TokenManager
) {
    private val TAG = Constants.Logging.DASHBOARD_TAG
    
    /**
     * Get user profile information
     * @return Result containing UserProfileResponse or error
     */
    suspend fun getUserProfile(): Result<UserProfileResponse> = withContext(Dispatchers.IO) {
        Log.d(TAG, "Fetching user profile...")

        val tokenResult = tokenManager.getToken()
        
        if (tokenResult is Result.Error) {
            Log.e(TAG, "Failed to retrieve token: ${tokenResult.message}")
            return@withContext Result.networkError("Failed to retrieve auth token: ${tokenResult.message}")
        }
        
        val token = (tokenResult as Result.Success).data
        if (token.isNullOrEmpty()) {
            Log.e(TAG, "Token is missing or empty")
            return@withContext Result.authError("Authentication token is missing")
        }

        Log.d(TAG, "Making API call to get user profile")
        return@withContext NetworkUtils.safeApiCall {
            apiService.getUserProfile("Bearer $token")
        }.also { result ->
            when (result) {
                is Result.Success -> Log.d(TAG, "User profile fetched successfully")
                is Result.Error -> Log.e(TAG, "Failed to fetch user profile: ${result.message}")
                is Result.Loading -> Log.d(TAG, "Loading user profile...")
            }
        }
    }
    
    /**
     * Update user profile information
     * @param profileData Map of profile fields to update
     * @return Result containing updated UserProfileResponse or error
     */
    suspend fun updateUserProfile(profileData: Map<String, String>): Result<UserProfileResponse> = withContext(Dispatchers.IO) {
        Log.d(TAG, "Updating user profile with data: ${profileData.keys}")

        val tokenResult = tokenManager.getToken()
        
        if (tokenResult is Result.Error) {
            Log.e(TAG, "Failed to retrieve token: ${tokenResult.message}")
            return@withContext Result.networkError("Failed to retrieve auth token: ${tokenResult.message}")
        }
        
        val token = (tokenResult as Result.Success).data
        if (token.isNullOrEmpty()) {
            Log.e(TAG, "Token is missing or empty")
            return@withContext Result.authError("Authentication token is missing")
        }

        Log.d(TAG, "Making API call to update user profile")
        return@withContext NetworkUtils.safeApiCall {
            apiService.updateUserProfile("Bearer $token", profileData)
        }.also { result ->
            when (result) {
                is Result.Success -> Log.d(TAG, "User profile updated successfully")
                is Result.Error -> Log.e(TAG, "Failed to update user profile: ${result.message}")
                is Result.Loading -> Log.d(TAG, "Updating user profile...")
            }
        }
    }
    
    /**
     * Get user events based on type and filters
     * @param type Event type (use Constants.Events.TYPE_* values)
     * @param status Optional status filter (use Constants.Events.STATUS_* values)
     * @param period Optional time period filter (use Constants.Events.PERIOD_* values)
     * @return Result containing UserEventsResponse or error
     */
    suspend fun getUserEvents(
        type: String,
        status: String? = null,
        period: String? = null
    ): Result<UserEventsResponse> = withContext(Dispatchers.IO) {
        Log.d(TAG, "Fetching user events - type: $type, status: $status, period: $period")

        val tokenResult = tokenManager.getToken()
        
        if (tokenResult is Result.Error) {
            Log.e(TAG, "Failed to retrieve token: ${tokenResult.message}")
            return@withContext Result.networkError("Failed to retrieve auth token: ${tokenResult.message}")
        }
        
        val token = (tokenResult as Result.Success).data
        if (token.isNullOrEmpty()) {
            Log.e(TAG, "Token is missing or empty")
            return@withContext Result.authError("Authentication token is missing")
        }

        Log.d(TAG, "Making API call to get user events")
        return@withContext NetworkUtils.safeApiCall {
            apiService.getUserEvents("Bearer $token", type, status, period)
        }.also { result ->
            when (result) {
                is Result.Success -> Log.d(
                    TAG,
                    "User events fetched successfully: ${result.data.events.size} events"
                )

                is Result.Error -> Log.e(TAG, "Failed to fetch user events: ${result.message}")
                is Result.Loading -> Log.d(TAG, "Loading user events...")
            }
        }
    }
    
    /**
     * Get user's recent activity
     * @param range Optional time range (default Constants.Events.PERIOD_1_MONTH)
     * @return Result containing activity data or error
     */
    suspend fun getRecentActivity(range: String? = Constants.Events.PERIOD_1_MONTH): Result<Map<String, Any>> =
        withContext(Dispatchers.IO) {
            Log.d(TAG, "Fetching recent activity for range: $range")

            val tokenResult = tokenManager.getToken()
        
        if (tokenResult is Result.Error) {
            Log.e(TAG, "Failed to retrieve token: ${tokenResult.message}")
            return@withContext Result.networkError("Failed to retrieve auth token: ${tokenResult.message}")
        }
        
        val token = (tokenResult as Result.Success).data
        if (token.isNullOrEmpty()) {
            Log.e(TAG, "Token is missing or empty")
            return@withContext Result.authError("Authentication token is missing")
        }

        Log.d(TAG, "Making API call to get recent activity")
        return@withContext NetworkUtils.safeApiCall {
            apiService.getRecentActivity("Bearer $token", range)
        }.also { result ->
            when (result) {
                is Result.Success -> Log.d(TAG, "Recent activity fetched successfully")
                is Result.Error -> Log.e(TAG, "Failed to fetch recent activity: ${result.message}")
                is Result.Loading -> Log.d(TAG, "Loading recent activity...")
            }
        }
    }
}