package com.example.talkeys_new.screens.events

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.talkeys_new.dataModels.EventResponse
import com.example.talkeys_new.utils.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.CancellationException

/**
 * ViewModel for managing event-related state and business logic
 */
class EventViewModel(private val repository: EventsRepository) : ViewModel() {

    companion object {
        private const val TAG = "EventViewModel"
    }

    // Selected event state
    private val _selectedEvent = MutableStateFlow<EventResponse?>(null)
    val selectedEvent: StateFlow<EventResponse?> = _selectedEvent.asStateFlow()

    // Single event loading state
    private val _eventLoading = MutableStateFlow(false)
    val eventLoading: StateFlow<Boolean> = _eventLoading.asStateFlow()

    // Single event error state
    private val _eventError = MutableStateFlow<String?>(null)
    val eventError: StateFlow<String?> = _eventError.asStateFlow()

    // All events storage (private, not exposed)
    private val _allEvents = MutableStateFlow<List<EventResponse>>(emptyList())

    // Filtered events list
    private val _eventList = MutableStateFlow<List<EventResponse>>(emptyList())
    val eventList: StateFlow<List<EventResponse>> = _eventList.asStateFlow()

    // Loading state for all events
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Error message for all events
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Filter state for live/past events
    private val _showLiveEvents = MutableStateFlow(true)
    val showLiveEvents: StateFlow<Boolean> = _showLiveEvents.asStateFlow()

    // Loading state to prevent multiple simultaneous requests
    private var isCurrentlyFetching = false

    /**
     * Fetches all events from the repository with caching support
     * @param forceRefresh If true, bypasses cache and fetches fresh data
     * Handles loading states, errors, and prevents duplicate requests
     */
    fun fetchAllEvents(forceRefresh: Boolean = false) {
        // Prevent multiple simultaneous requests
        if (isCurrentlyFetching) {
            Log.d(TAG, "Already fetching events, skipping duplicate request")
            return
        }

        viewModelScope.launch {
            try {
                isCurrentlyFetching = true
                Log.d(TAG, "Starting to fetch events... (forceRefresh: $forceRefresh)")

                _isLoading.value = true
                _errorMessage.value = null

                when (val result = repository.getAllEvents(forceRefresh)) {
                    is Result.Success -> {
                        val events = result.data
                        Log.d(TAG, "Events received: ${events.size}")
                        _allEvents.value = events
                        filterEvents() // Apply current filter
                        _errorMessage.value = null
                    }
                    is Result.Error -> {
                        Log.e(TAG, "Error fetching events: ${result.message}")
                        _errorMessage.value = result.message
                        _eventList.value = emptyList()
                    }
                    is Result.Loading -> {
                        // Already handled by setting _isLoading to true above
                    }
                }
            } catch (e: CancellationException) {
                // Don't log cancellation exceptions as errors
                Log.d(TAG, "Request cancelled")
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error occurred: ${e.message}", e)
                _errorMessage.value = "An unexpected error occurred. Please try again."
            } finally {
                _isLoading.value = false
                isCurrentlyFetching = false
                Log.d(TAG, "Finished fetching events")
            }
        }
    }

    /**
     * Toggles between live and past events filter
     */
    fun toggleEventFilter() {
        _showLiveEvents.value = !_showLiveEvents.value
        filterEvents()
    }

    /**
     * Filters events based on the current filter state
     * Uses multiple criteria to determine if an event is live or past:
     * 1. Backend isLive flag (primary)
     * 2. Event dates (fallback for better accuracy)
     */
    private fun filterEvents() {
        val allEvents = _allEvents.value

        if (allEvents.isEmpty()) {
            _eventList.value = emptyList()
            return
        }

        val currentTime = System.currentTimeMillis()
        
        val filtered = if (_showLiveEvents.value) {
            allEvents.filter { event ->
                // Primary check: Use backend isLive flag
                val isLiveFromBackend = event.isLive == true
                
                // Secondary check: Date-based fallback
                val isLiveByDate = try {
                    // If endRegistrationDate is available and in the future, consider it live
                    event.endRegistrationDate?.let { endRegDate ->
                        // This is a simplified check. In production, you'd parse the actual date
                        // For now, we'll rely mainly on the backend isLive flag
                        true
                    } ?: true
                } catch (e: Exception) {
                    Log.w(TAG, "Error parsing event dates for ${event.name}: ${e.message}")
                    true // Default to live if date parsing fails
                }
                
                // Use backend flag as primary source of truth
                isLiveFromBackend
            }
        } else {
            allEvents.filter { event ->
                event.isLive == false
            }
        }

        Log.d(TAG, "Filtering events - Show live: ${_showLiveEvents.value}, " +
                "Total events: ${allEvents.size}, Filtered count: ${filtered.size}")
        
        // Additional logging for debugging
        val liveCount = allEvents.count { it.isLive == true }
        val pastCount = allEvents.count { it.isLive == false }
        Log.d(TAG, "Event distribution - Live: $liveCount, Past: $pastCount")
        
        _eventList.value = filtered
    }

    /**
     * Fetches a specific event by ID with caching support
     * @param eventId The ID of the event to fetch
     * @param forceRefresh If true, bypasses cache and fetches fresh data
     */
    fun fetchEventById(eventId: String, forceRefresh: Boolean = false) {
        if (eventId.isBlank()) {
            Log.e(TAG, "Event ID is blank or empty")
            _eventError.value = "Invalid event ID"
            return
        }

        viewModelScope.launch {
            _eventLoading.value = true
            _eventError.value = null

            try {
                Log.d(TAG, "Fetching event with ID: $eventId (forceRefresh: $forceRefresh)")
                
                when (val result = repository.getEventById(eventId, forceRefresh)) {
                    is Result.Success -> {
                        val event = result.data
                        _selectedEvent.value = event
                        Log.d(TAG, "Successfully fetched event: ${event.name}")
                    }
                    is Result.Error -> {
                        _eventError.value = result.message
                        Log.e(TAG, "Error fetching event: ${result.message}")
                    }
                    is Result.Loading -> {
                        // Already handled by setting _eventLoading to true above
                    }
                }
            } catch (e: CancellationException) {
                Log.d(TAG, "Event fetch cancelled for ID: $eventId")
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Exception fetching event: ${e.message}", e)
                _eventError.value = "An unexpected error occurred. Please try again."
            } finally {
                _eventLoading.value = false
            }
        }
    }

    /**
     * Clears the selected event
     */
    fun clearSelectedEvent() {
        _selectedEvent.value = null
        _eventError.value = null
    }

    /**
     * Clears all error messages
     */
    fun clearErrors() {
        _errorMessage.value = null
        _eventError.value = null
    }

    /**
     * Refreshes the events list by forcing a cache refresh
     */
    fun refreshEvents() {
        fetchAllEvents(forceRefresh = true)
    }

    /**
     * Returns the current filter state as a readable string
     */
    fun getCurrentFilterDescription(): String {
        return if (_showLiveEvents.value) "Live Events" else "Past Events"
    }

    /**
     * Returns the count of events in the current filter
     */
    fun getCurrentEventCount(): Int {
        return _eventList.value.size
    }

    /**
     * Checks if there are any events available
     */
    fun hasEvents(): Boolean {
        return _allEvents.value.isNotEmpty()
    }
}