package com.example.talkeys_new.api

import android.util.Log
import com.example.talkeys_new.dataModels.EventResponse
import com.example.talkeys_new.screens.authentication.TokenManager
import com.example.talkeys_new.utils.Constants
import com.example.talkeys_new.utils.NetworkUtils
import com.example.talkeys_new.utils.Result
import com.example.talkeys_new.utils.cache.CacheManager
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
     * Get user profile information with caching support
     * @param forceRefresh If true, bypasses cache and fetches fresh data
     * @return Result containing UserProfileResponse or error
     */
    suspend fun getUserProfile(forceRefresh: Boolean = false): Result<UserProfileResponse> = withContext(Dispatchers.IO) {
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

        // Check cache first unless force refresh is requested
        if (!forceRefresh) {
            val cacheKey = CacheManager.Keys.userProfile("current") // Using "current" as default user ID
            val cachedProfile = CacheManager.userProfileCache.get(cacheKey)
            if (cachedProfile != null) {
                Log.d(TAG, "Returning cached user profile")
                return@withContext Result.Success(cachedProfile)
            }
        }

        Log.d(TAG, "Making API call to get user profile")
        return@withContext NetworkUtils.safeApiCall {
            apiService.getUserProfile("Bearer $token")
        }.also { result ->
            when (result) {
                is Result.Success -> {
                    // Cache the user profile
                    val cacheKey = CacheManager.Keys.userProfile("current")
                    CacheManager.userProfileCache.put(cacheKey, result.data)
                    Log.d(TAG, "User profile fetched and cached successfully")
                }
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
                is Result.Success -> {
                    // Update cache with new profile data
                    val cacheKey = CacheManager.Keys.userProfile("current")
                    CacheManager.userProfileCache.put(cacheKey, result.data)
                    Log.d(TAG, "User profile updated and cached successfully")
                }
                is Result.Error -> Log.e(TAG, "Failed to update user profile: ${result.message}")
                is Result.Loading -> Log.d(TAG, "Updating user profile...")
            }
        }
    }
    
    /**
     * Get user events based on type and filters with caching support
     * @param type Event type (use Constants.Events.TYPE_* values)
     * @param status Optional status filter (use Constants.Events.STATUS_* values)
     * @param period Optional time period filter (use Constants.Events.PERIOD_* values)
     * @param forceRefresh If true, bypasses cache and fetches fresh data
     * @return Result containing UserEventsResponse or error
     */
    suspend fun getUserEvents(
        type: String,
        status: String? = null,
        period: String? = null,
        forceRefresh: Boolean = false
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

        // Check cache first unless force refresh is requested
        if (!forceRefresh) {
            val cacheKey = CacheManager.Keys.userEvents(type, status, period)
            val cachedEvents = CacheManager.userEventsCache.get(cacheKey)
            if (cachedEvents != null) {
                Log.d(TAG, "Returning cached user events: ${cachedEvents.size} events")
                // Create UserEventsResponse from cached data
                val cachedResponse = UserEventsResponse(events = cachedEvents)
                return@withContext Result.Success(cachedResponse)
            }
        }

        Log.d(TAG, "Making API call to get user events")
        return@withContext NetworkUtils.safeApiCall {
            apiService.getUserEvents("Bearer $token", type, status, period)
        }.also { result ->
            when (result) {
                is Result.Success -> {
                    // Cache the user events
                    val cacheKey = CacheManager.Keys.userEvents(type, status, period)
                    CacheManager.userEventsCache.put(cacheKey, result.data.events)
                    Log.d(TAG, "User events fetched and cached successfully: ${result.data.events.size} events")
                }
                is Result.Error -> Log.e(TAG, "Failed to fetch user events: ${result.message}")
                is Result.Loading -> Log.d(TAG, "Loading user events...")
            }
        }
    }
    
    /**
     * Get user's recent activity with caching support
     * @param range Optional time range (default Constants.Events.PERIOD_1_MONTH)
     * @param forceRefresh If true, bypasses cache and fetches fresh data
     * @return Result containing activity data or error
     */
    suspend fun getRecentActivity(
        range: String? = Constants.Events.PERIOD_1_MONTH,
        forceRefresh: Boolean = false
    ): Result<Map<String, Any>> = withContext(Dispatchers.IO) {
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

        // Check cache first unless force refresh is requested
        if (!forceRefresh) {
            val cacheKey = CacheManager.Keys.recentActivity(range)
            val cachedActivity = CacheManager.recentActivityCache.get(cacheKey)
            if (cachedActivity != null) {
                Log.d(TAG, "Returning cached recent activity")
                return@withContext Result.Success(cachedActivity)
            }
        }

        Log.d(TAG, "Making API call to get recent activity")
        return@withContext NetworkUtils.safeApiCall {
            apiService.getRecentActivity("Bearer $token", range)
        }.also { result ->
            when (result) {
                is Result.Success -> {
                    // Cache the recent activity
                    val cacheKey = CacheManager.Keys.recentActivity(range)
                    CacheManager.recentActivityCache.put(cacheKey, result.data)
                    Log.d(TAG, "Recent activity fetched and cached successfully")
                }
                is Result.Error -> Log.e(TAG, "Failed to fetch recent activity: ${result.message}")
                is Result.Loading -> Log.d(TAG, "Loading recent activity...")
            }
        }
    }
}