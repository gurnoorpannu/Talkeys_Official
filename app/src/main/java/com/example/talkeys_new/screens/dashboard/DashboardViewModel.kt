package com.example.talkeys_new.screens.dashboard

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.talkeys_new.api.DashboardRepository
import com.example.talkeys_new.api.UserEventsResponse
import com.example.talkeys_new.api.UserProfileResponse
import com.example.talkeys_new.dataModels.EventResponse
import com.example.talkeys_new.utils.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.CancellationException

/**
 * ViewModel for managing dashboard-related state and business logic
 * Handles user profile, events, and activity data with proper error handling
 */
class DashboardViewModel(private val repository: DashboardRepository) : ViewModel() {

    companion object {
        private const val TAG = "DashboardViewModel"
    }

    // User profile state
    private val _userProfile = MutableStateFlow<UserProfileResponse?>(null)
    val userProfile: StateFlow<UserProfileResponse?> = _userProfile.asStateFlow()

    // User events state - expose as List<EventResponse> for easier consumption
    private val _userEvents = MutableStateFlow<List<EventResponse>>(emptyList())
    val userEvents: StateFlow<List<EventResponse>> = _userEvents.asStateFlow()
    
    // Internal state for full response
    private val _userEventsResponse = MutableStateFlow<UserEventsResponse?>(null)

    // Recent activity state
    private val _recentActivity = MutableStateFlow<Map<String, Any>?>(null)
    val recentActivity: StateFlow<Map<String, Any>?> = _recentActivity.asStateFlow()

    // Unified loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // Unified error state
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    // Individual loading states for specific operations
    private val _profileLoading = MutableStateFlow(false)
    val profileLoading: StateFlow<Boolean> = _profileLoading.asStateFlow()

    private val _eventsLoading = MutableStateFlow(false)
    val eventsLoading: StateFlow<Boolean> = _eventsLoading.asStateFlow()

    private val _activityLoading = MutableStateFlow(false)
    val activityLoading: StateFlow<Boolean> = _activityLoading.asStateFlow()

    // Individual error states
    private val _profileError = MutableStateFlow<String?>(null)
    val profileError: StateFlow<String?> = _profileError.asStateFlow()

    private val _eventsError = MutableStateFlow<String?>(null)
    val eventsError: StateFlow<String?> = _eventsError.asStateFlow()

    private val _activityError = MutableStateFlow<String?>(null)
    val activityError: StateFlow<String?> = _activityError.asStateFlow()

    // Prevent multiple simultaneous requests
    private var isLoadingProfile = false
    private var isLoadingEvents = false
    private var isLoadingActivity = false

    /**
     * Fetches user profile information
     */
    fun fetchUserProfile() {
        if (isLoadingProfile) {
            Log.d(TAG, "Already fetching profile, skipping duplicate request")
            return
        }

        viewModelScope.launch {
            try {
                isLoadingProfile = true
                _profileLoading.value = true
                _profileError.value = null

                when (val result = repository.getUserProfile()) {
                    is Result.Success -> {
                        _userProfile.value = result.data
                        Log.d(TAG, "Successfully fetched user profile")
                    }
                    is Result.Error -> {
                        _profileError.value = result.message
                        Log.e(TAG, "Error fetching user profile: ${result.message}")
                    }
                    is Result.Loading -> {
                        // Already handled by setting _profileLoading to true
                    }
                }
            } catch (e: CancellationException) {
                Log.d(TAG, "Profile fetch cancelled")
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error fetching profile: ${e.message}", e)
                _profileError.value = "An unexpected error occurred. Please try again."
            } finally {
                _profileLoading.value = false
                isLoadingProfile = false
            }
        }
    }

    /**
     * Updates user profile information
     * @param profileData Map of profile fields to update
     * @param onSuccess Callback for successful update
     */
    fun updateUserProfile(
        profileData: Map<String, String>,
        onSuccess: () -> Unit = {}
    ) {
        if (isLoadingProfile) {
            Log.d(TAG, "Already updating profile, skipping duplicate request")
            return
        }

        viewModelScope.launch {
            try {
                isLoadingProfile = true
                _profileLoading.value = true
                _profileError.value = null

                when (val result = repository.updateUserProfile(profileData)) {
                    is Result.Success -> {
                        _userProfile.value = result.data
                        Log.d(TAG, "Successfully updated user profile")
                        onSuccess()
                    }
                    is Result.Error -> {
                        _profileError.value = result.message
                        Log.e(TAG, "Error updating user profile: ${result.message}")
                    }
                    is Result.Loading -> {
                        // Already handled by setting _profileLoading to true
                    }
                }
            } catch (e: CancellationException) {
                Log.d(TAG, "Profile update cancelled")
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error updating profile: ${e.message}", e)
                _profileError.value = "An unexpected error occurred. Please try again."
            } finally {
                _profileLoading.value = false
                isLoadingProfile = false
            }
        }
    }

    /**
     * Fetches user events based on type and filters
     * @param type Event type ("registered", "bookmarked", "hosted")
     * @param status Optional status filter ("past", "upcoming")
     * @param period Optional time period filter ("1m", "6m", "1y")
     */
    fun fetchUserEvents(
        type: String,
        status: String? = null,
        period: String? = null
    ) {
        if (isLoadingEvents) {
            Log.d(TAG, "Already fetching events, skipping duplicate request")
            return
        }

        viewModelScope.launch {
            try {
                isLoadingEvents = true
                _eventsLoading.value = true
                _isLoading.value = true
                _eventsError.value = null
                _error.value = null

                when (val result = repository.getUserEvents(type, status, period)) {
                    is Result.Success -> {
                        _userEventsResponse.value = result.data
                        _userEvents.value = result.data.events
                        Log.d(TAG, "Successfully fetched user events: ${result.data.events.size} events")
                    }
                    is Result.Error -> {
                        _eventsError.value = result.message
                        _error.value = result.message
                        _userEvents.value = emptyList()
                        Log.e(TAG, "Error fetching user events: ${result.message}")
                    }
                    is Result.Loading -> {
                        // Already handled by setting _eventsLoading to true
                    }
                }
            } catch (e: CancellationException) {
                Log.d(TAG, "Events fetch cancelled")
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error fetching events: ${e.message}", e)
                _eventsError.value = "An unexpected error occurred. Please try again."
                _error.value = "An unexpected error occurred. Please try again."
                _userEvents.value = emptyList()
            } finally {
                _eventsLoading.value = false
                _isLoading.value = false
                isLoadingEvents = false
            }
        }
    }

    /**
     * Fetches user's recent activity
     * @param range Time range for activity ("1m", "6m", "1y")
     */
    fun fetchRecentActivity(range: String? = "1m") {
        if (isLoadingActivity) {
            Log.d(TAG, "Already fetching activity, skipping duplicate request")
            return
        }

        viewModelScope.launch {
            try {
                isLoadingActivity = true
                _activityLoading.value = true
                _activityError.value = null

                when (val result = repository.getRecentActivity(range)) {
                    is Result.Success -> {
                        _recentActivity.value = result.data
                        Log.d(TAG, "Successfully fetched recent activity")
                    }
                    is Result.Error -> {
                        _activityError.value = result.message
                        Log.e(TAG, "Error fetching recent activity: ${result.message}")
                    }
                    is Result.Loading -> {
                        // Already handled by setting _activityLoading to true
                    }
                }
            } catch (e: CancellationException) {
                Log.d(TAG, "Activity fetch cancelled")
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error fetching activity: ${e.message}", e)
                _activityError.value = "An unexpected error occurred. Please try again."
            } finally {
                _activityLoading.value = false
                isLoadingActivity = false
            }
        }
    }

    /**
     * Clears all error messages
     */
    fun clearErrors() {
        _profileError.value = null
        _eventsError.value = null
        _activityError.value = null
        _error.value = null
    }

    /**
     * Refreshes all dashboard data
     */
    fun refreshDashboard() {
        fetchUserProfile()
        fetchUserEvents("registered")
        fetchRecentActivity()
    }
}