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

class EventViewModel(private val repository: EventsRepository) : ViewModel() {

    private val _selectedEvent = MutableStateFlow<EventResponse?>(null)
    val selectedEvent: StateFlow<EventResponse?> = _selectedEvent

    private val _eventLoading = MutableStateFlow(false)
    val eventLoading: StateFlow<Boolean> = _eventLoading

    private val _eventError = MutableStateFlow<String?>(null)
    val eventError: StateFlow<String?> = _eventError


    private val _allEvents = MutableStateFlow<List<EventResponse>>(emptyList())

    private val _eventList = MutableStateFlow<List<EventResponse>>(emptyList())
    val eventList: StateFlow<List<EventResponse>> = _eventList.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _showLiveEvents = MutableStateFlow(true)
    val showLiveEvents: StateFlow<Boolean> = _showLiveEvents.asStateFlow()

    fun fetchAllEvents() {
        viewModelScope.launch {
            try {
                Log.d("EventViewModel", "Starting to fetch events...")
                _isLoading.value = true
                _errorMessage.value = null

                val response: Response<List<EventResponse>> = repository.getAllEvents()

                Log.d("EventViewModel", "Response received: ${response.code()}")
                Log.d("EventViewModel", "Response successful: ${response.isSuccessful}")

                if (response.isSuccessful) {
                    val events = response.body() ?: emptyList()
                    Log.d("EventViewModel", "Events received: ${events.size}")

                    _allEvents.value = events
                    filterEvents() // Apply current filter
                    _errorMessage.value = null
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMsg = "Error: ${response.code()} ${response.message()}"
                    Log.e("EventViewModel", errorMsg)
                    Log.e("EventViewModel", "Error body: $errorBody")
                    _errorMessage.value = errorMsg
                }
            } catch (e: Exception) {
                Log.e("EventViewModel", "Exception occurred: ${e.message}", e)
                _errorMessage.value = "Network error: ${e.message}"
            } finally {
                _isLoading.value = false
                Log.d("EventViewModel", "Finished fetching events")
            }
        }
    }

    fun toggleEventFilter() {
        _showLiveEvents.value = !_showLiveEvents.value
        filterEvents()
    }

    private fun filterEvents() {
        val filtered = if (_showLiveEvents.value) {
            _allEvents.value.filter { it.isLive }
        } else {
            _allEvents.value.filter { !it.isLive }
        }

        Log.d("EventViewModel", "Filtering events - Show live: ${_showLiveEvents.value}, Filtered count: ${filtered.size}")
        _eventList.value = filtered
    }

    fun fetchEventById(eventId: String) {
        viewModelScope.launch {
            _eventLoading.value = true
            _eventError.value = null

            try {
                val response = repository.getEventById(eventId)
                if (response.isSuccessful) {
                    _selectedEvent.value = response.body()
                    Log.d("EventViewModel", "Fetched event: ${response.body()?.name}")
                } else {
                    _eventError.value = "Failed to load event: ${response.message()}"
                    Log.e("EventViewModel", "Error: ${response.code()} ${response.message()}")
                }
            } catch (e: Exception) {
                _eventError.value = "Error: ${e.message}"
                Log.e("EventViewModel", "Exception: ${e.message}", e)
            } finally {
                _eventLoading.value = false
            }
        }
    }


}