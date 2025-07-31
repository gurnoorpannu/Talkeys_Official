package com.example.talkeys_new.screens.events

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.talkeys_new.dataModels.EventResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
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
     * Fetches all events from the repository
     * Handles loading states, errors, and prevents duplicate requests
     */
    fun fetchAllEvents() {
        // Prevent multiple simultaneous requests
        if (isCurrentlyFetching) {
            Log.d(TAG, "Already fetching events, skipping duplicate request")
            return
        }

        viewModelScope.launch {
            try {
                isCurrentlyFetching = true
                Log.d(TAG, "Starting to fetch events...")

                _isLoading.value = true
                _errorMessage.value = null

                val response: Response<List<EventResponse>> = repository.getAllEvents()

                Log.d(TAG, "Response received: ${response.code()}")
                Log.d(TAG, "Response successful: ${response.isSuccessful}")

                if (response.isSuccessful) {
                    val events = response.body()

                    if (events != null) {
                        Log.d(TAG, "Events received: ${events.size}")
                        _allEvents.value = events
                        filterEvents() // Apply current filter
                        _errorMessage.value = null
                    } else {
                        Log.w(TAG, "Response body is null")
                        _errorMessage.value = "No events data received"
                        _eventList.value = emptyList()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMsg = when (response.code()) {
                        401 -> "Unauthorized access. Please login again."
                        403 -> "Access forbidden. Check your permissions."
                        404 -> "Events not found."
                        408 -> "Request timeout. Please try again."
                        429 -> "Too many requests. Please wait before retrying."
                        500 -> "Server error. Please try again later."
                        502, 503, 504 -> "Service temporarily unavailable. Please try again later."
                        else -> "Error: ${response.code()} ${response.message()}"
                    }

                    Log.e(TAG, "HTTP Error: $errorMsg")
                    Log.e(TAG, "Error body: $errorBody")
                    _errorMessage.value = errorMsg
                }
            } catch (e: CancellationException) {
                // Don't log cancellation exceptions as errors
                Log.d(TAG, "Request cancelled")
                throw e
            } catch (e: java.net.SocketTimeoutException) {
                Log.e(TAG, "Network timeout: ${e.message}")
                _errorMessage.value = "Network timeout. Please check your connection and try again."
            } catch (e: java.net.UnknownHostException) {
                Log.e(TAG, "Network error - no internet: ${e.message}")
                _errorMessage.value = "No internet connection. Please check your network and try again."
            } catch (e: java.net.ConnectException) {
                Log.e(TAG, "Connection failed: ${e.message}")
                _errorMessage.value = "Unable to connect to server. Please try again later."
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
     */
    private fun filterEvents() {
        val allEvents = _allEvents.value

        if (allEvents.isEmpty()) {
            _eventList.value = emptyList()
            return
        }

        val filtered = if (_showLiveEvents.value) {
            allEvents.filter { event ->
                event.isLive == true // Handle potential null values
            }
        } else {
            allEvents.filter { event ->
                event.isLive == false
            }
        }

        Log.d(TAG, "Filtering events - Show live: ${_showLiveEvents.value}, " +
                "Total events: ${allEvents.size}, Filtered count: ${filtered.size}")
        _eventList.value = filtered
    }

    /**
     * Fetches a specific event by ID
     * @param eventId The ID of the event to fetch
     */
    fun fetchEventById(eventId: String) {
        if (eventId.isBlank()) {
            Log.e(TAG, "Event ID is blank or empty")
            _eventError.value = "Invalid event ID"
            return
        }

        viewModelScope.launch {
            _eventLoading.value = true
            _eventError.value = null

            try {
                Log.d(TAG, "Fetching event with ID: $eventId")
                val response = repository.getEventById(eventId)

                if (response.isSuccessful) {
                    val event = response.body()
                    if (event != null) {
                        _selectedEvent.value = event
                        Log.d(TAG, "Successfully fetched event: ${event.name}")
                    } else {
                        _eventError.value = "Event data not found"
                        Log.w(TAG, "Event response body is null for ID: $eventId")
                    }
                } else {
                    val errorMsg = when (response.code()) {
                        401 -> "Unauthorized access. Please login again."
                        403 -> "Access forbidden."
                        404 -> "Event not found."
                        408 -> "Request timeout. Please try again."
                        429 -> "Too many requests. Please wait before retrying."
                        500 -> "Server error. Please try again later."
                        502, 503, 504 -> "Service temporarily unavailable. Please try again later."
                        else -> "Failed to load event: ${response.message()}"
                    }

                    _eventError.value = errorMsg
                    Log.e(TAG, "Error fetching event: ${response.code()} ${response.message()}")
                }
            } catch (e: CancellationException) {
                Log.d(TAG, "Event fetch cancelled for ID: $eventId")
                throw e
            } catch (e: java.net.SocketTimeoutException) {
                Log.e(TAG, "Network timeout fetching event: ${e.message}")
                _eventError.value = "Network timeout. Please try again."
            } catch (e: java.net.UnknownHostException) {
                Log.e(TAG, "Network error fetching event: ${e.message}")
                _eventError.value = "No internet connection. Please check your network."
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
     * Refreshes the events list
     */
    fun refreshEvents() {
        fetchAllEvents()
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