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
}