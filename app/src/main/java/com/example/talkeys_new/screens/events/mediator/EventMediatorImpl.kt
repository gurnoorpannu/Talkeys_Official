package com.example.talkeys_new.screens.events.mediator

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.navigation.NavController
import com.example.talkeys_new.dataModels.EventResponse
import com.example.talkeys_new.screens.events.EventsRepository
import com.example.talkeys_new.utils.Result
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Concrete implementation of EventMediator that coordinates all event-related operations,
 * communications, and state management across different components
 */
class EventMediatorImpl(
    private val repository: EventsRepository,
    private val context: Context,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
) : EventMediator {
    
    companion object {
        private const val TAG = "EventMediatorImpl"
    }
    
    // Navigation controller (set externally)
    private var navController: NavController? = null
    
    // Event listeners registry
    private val listeners = ConcurrentHashMap<String, EventListener>()
    
    // State flows for event data
    private val _eventList = MutableStateFlow<List<EventResponse>>(emptyList())
    override val eventList: StateFlow<List<EventResponse>> = _eventList.asStateFlow()
    
    private val _selectedEvent = MutableStateFlow<EventResponse?>(null)
    override val selectedEvent: StateFlow<EventResponse?> = _selectedEvent.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    override val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    // Event creation flow state
    private val _currentCreationStep = MutableStateFlow(0)
    val currentCreationStep: StateFlow<Int> = _currentCreationStep.asStateFlow()
    
    private val _creationSteps = MutableStateFlow<List<EventCreationStep>>(emptyList())
    val creationSteps: StateFlow<List<EventCreationStep>> = _creationSteps.asStateFlow()
    
    // Filter state
    private val _showLiveEvents = MutableStateFlow(true)
    val showLiveEvents: StateFlow<Boolean> = _showLiveEvents.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()
    
    // Filtered events based on current filters
    private val _filteredEvents = MutableStateFlow<List<EventResponse>>(emptyList())
    val filteredEvents: StateFlow<List<EventResponse>> = _filteredEvents.asStateFlow()
    
    // Liked events tracking (local state)
    private val _likedEvents = MutableStateFlow<Set<String>>(emptySet())
    val likedEvents: StateFlow<Set<String>> = _likedEvents.asStateFlow()
    
    private var isCurrentlyFetching = false
    
    init {
        // Setup reactive filtering
        combine(
            eventList,
            showLiveEvents,
            searchQuery,
            selectedCategory
        ) { events, showLive, query, category ->
            applyFilters(events, showLive, query, category)
        }.onEach { filtered ->
            _filteredEvents.value = filtered
        }.launchIn(coroutineScope)
    }
    
    // Public method to set navigation controller
    fun setNavController(navController: NavController) {
        this.navController = navController
    }
    
    // Event listener management
    fun addListener(key: String, listener: EventListener) {
        listeners[key] = listener
    }
    
    fun removeListener(key: String) {
        listeners.remove(key)
    }
    
    // ===== Event Data Operations =====
    
    override suspend fun fetchAllEvents(forceRefresh: Boolean) {
        if (isCurrentlyFetching && !forceRefresh) {
            Log.d(TAG, "Already fetching events, skipping duplicate request")
            return
        }
        
        try {
            isCurrentlyFetching = true
            _isLoading.value = true
            _errorMessage.value = null
            
            Log.d(TAG, "Fetching all events (forceRefresh: $forceRefresh)")
            
            when (val result = repository.getAllEvents(forceRefresh)) {
                is Result.Success -> {
                    val events = result.data
                    _eventList.value = events
                    _errorMessage.value = null
                    
                    // Notify listeners
                    notifyListeners { it.onEventsRefreshed(events) }
                    
                    Log.d(TAG, "Successfully fetched ${events.size} events")
                }
                is Result.Error -> {
                    val errorMsg = result.message
                    _errorMessage.value = errorMsg
                    handleError(result.exception ?: Exception(errorMsg), "fetchAllEvents")
                    Log.e(TAG, "Error fetching events: $errorMsg")
                }
                is Result.Loading -> {
                    // Already handled by setting _isLoading to true
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception in fetchAllEvents", e)
            handleError(e, "fetchAllEvents")
        } finally {
            _isLoading.value = false
            isCurrentlyFetching = false
        }
    }
    
    override suspend fun fetchEventById(eventId: String, forceRefresh: Boolean) {
        if (eventId.isBlank()) {
            handleError(IllegalArgumentException("Event ID cannot be empty"), "fetchEventById")
            return
        }
        
        try {
            _isLoading.value = true
            _errorMessage.value = null
            
            Log.d(TAG, "Fetching event by ID: $eventId (forceRefresh: $forceRefresh)")
            
            when (val result = repository.getEventById(eventId, forceRefresh)) {
                is Result.Success -> {
                    val event = result.data
                    _selectedEvent.value = event
                    
                    // Also update the event in the list if it exists
                    val currentList = _eventList.value.toMutableList()
                    val index = currentList.indexOfFirst { it._id == eventId }
                    if (index != -1) {
                        currentList[index] = event
                        _eventList.value = currentList
                    }
                    
                    // Notify listeners
                    notifyListeners { it.onEventUpdated(event) }
                    
                    Log.d(TAG, "Successfully fetched event: ${event.name}")
                }
                is Result.Error -> {
                    val errorMsg = result.message
                    _errorMessage.value = errorMsg
                    handleError(result.exception ?: Exception(errorMsg), "fetchEventById")
                    Log.e(TAG, "Error fetching event by ID: $errorMsg")
                }
                is Result.Loading -> {
                    // Already handled by setting _isLoading to true
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception in fetchEventById", e)
            handleError(e, "fetchEventById")
        } finally {
            _isLoading.value = false
        }
    }
    
    override suspend fun refreshEvents() {
        Log.d(TAG, "Refreshing events")
        fetchAllEvents(forceRefresh = true)
    }
    
    // ===== Event Actions =====
    
    override suspend fun likeEvent(eventId: String): Boolean {
        return try {
            // Update local state immediately for better UX
            val currentLiked = _likedEvents.value.toMutableSet()
            currentLiked.add(eventId)
            _likedEvents.value = currentLiked
            
            // TODO: Implement API call for liking event
            // For now, just simulate success
            
            notifyListeners { it.onEventLiked(eventId, true) }
            Log.d(TAG, "Event liked: $eventId")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error liking event: $eventId", e)
            handleError(e, "likeEvent")
            false
        }
    }
    
    override suspend fun unlikeEvent(eventId: String): Boolean {
        return try {
            // Update local state immediately
            val currentLiked = _likedEvents.value.toMutableSet()
            currentLiked.remove(eventId)
            _likedEvents.value = currentLiked
            
            // TODO: Implement API call for unliking event
            // For now, just simulate success
            
            notifyListeners { it.onEventLiked(eventId, false) }
            Log.d(TAG, "Event unliked: $eventId")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error unliking event: $eventId", e)
            handleError(e, "unlikeEvent")
            false
        }
    }
    
    override suspend fun registerForEvent(eventId: String): Boolean {
        return try {
            // TODO: Implement API call for event registration
            // For now, just simulate success
            
            notifyListeners { it.onEventRegistered(eventId, true) }
            Log.d(TAG, "Registered for event: $eventId")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error registering for event: $eventId", e)
            handleError(e, "registerForEvent")
            false
        }
    }
    
    override suspend fun shareEvent(event: EventResponse): Boolean {
        return try {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Check out this event: ${event.name}")
                putExtra(
                    Intent.EXTRA_TEXT, 
                    "I found this interesting event: ${event.name}\n\n" +
                    "${event.eventDescription ?: "No description available"}\n\n" +
                    "Date: ${event.startDate}\n" +
                    "Time: ${event.startTime}\n" +
                    "Location: ${event.location ?: "Online"}"
                )
            }
            
            val chooserIntent = Intent.createChooser(shareIntent, "Share Event")
            chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooserIntent)
            
            Log.d(TAG, "Event shared: ${event.name}")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error sharing event: ${event.name}", e)
            handleError(e, "shareEvent")
            false
        }
    }
    
    // ===== Event Creation Flow Coordination =====
    
    override fun startEventCreation() {
        Log.d(TAG, "Starting event creation flow")
        _currentCreationStep.value = 1
        _creationSteps.value = initializeCreationSteps()
        navigateToEventCreation()
    }
    
    override fun proceedToNextStep(stepData: Map<String, Any>) {
        val currentStep = _currentCreationStep.value
        val steps = _creationSteps.value.toMutableList()
        
        if (currentStep <= steps.size) {
            // Update current step data
            steps[currentStep - 1] = steps[currentStep - 1].copy(
                data = stepData,
                isValid = true
            )
            
            // Move to next step if not the last one
            if (currentStep < steps.size) {
                _currentCreationStep.value = currentStep + 1
            }
            
            _creationSteps.value = steps
            Log.d(TAG, "Proceeded to step ${_currentCreationStep.value}")
        }
    }
    
    override fun goToPreviousStep() {
        val currentStep = _currentCreationStep.value
        if (currentStep > 1) {
            _currentCreationStep.value = currentStep - 1
            Log.d(TAG, "Moved back to step ${_currentCreationStep.value}")
        }
    }
    
    override fun saveEventDraft(stepData: Map<String, Any>) {
        // TODO: Implement draft saving to local storage or cache
        Log.d(TAG, "Event draft saved")
    }
    
    override fun submitEvent(eventData: Map<String, Any>): Boolean {
        return try {
            // TODO: Implement API call to create event
            Log.d(TAG, "Event submitted successfully")
            
            // Reset creation flow
            _currentCreationStep.value = 0
            _creationSteps.value = emptyList()
            
            // Navigate back to event list
            navigateToEventList()
            
            // Refresh events to show the new one
            coroutineScope.launch {
                refreshEvents()
            }
            
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error submitting event", e)
            handleError(e, "submitEvent")
            false
        }
    }
    
    override fun cancelEventCreation() {
        Log.d(TAG, "Event creation cancelled")
        _currentCreationStep.value = 0
        _creationSteps.value = emptyList()
        navigateToEventList()
    }
    
    // ===== Filter and Search Operations =====
    
    override fun toggleEventFilter() {
        _showLiveEvents.value = !_showLiveEvents.value
        Log.d(TAG, "Filter toggled - Show live events: ${_showLiveEvents.value}")
    }
    
    override fun applySearchFilter(query: String) {
        _searchQuery.value = query
        Log.d(TAG, "Search filter applied: $query")
    }
    
    override fun applyCategoryFilter(category: String) {
        _selectedCategory.value = category
        Log.d(TAG, "Category filter applied: $category")
    }
    
    override fun clearAllFilters() {
        _searchQuery.value = ""
        _selectedCategory.value = null
        _showLiveEvents.value = true
        Log.d(TAG, "All filters cleared")
    }
    
    // ===== Navigation Coordination =====
    
    override fun navigateToEventDetail(eventId: String) {
        navController?.navigate("eventDetail/$eventId")
        Log.d(TAG, "Navigating to event detail: $eventId")
    }
    
    override fun navigateToEventCreation() {
        navController?.navigate("createEvent1")
        Log.d(TAG, "Navigating to event creation")
    }
    
    override fun navigateToEventList() {
        navController?.navigate("exploreEvents")
        Log.d(TAG, "Navigating to event list")
    }
    
    // ===== Error Handling =====
    
    override fun clearErrors() {
        _errorMessage.value = null
        Log.d(TAG, "Errors cleared")
    }
    
    override fun handleError(error: Throwable, context: String) {
        val errorMessage = "Error in $context: ${error.localizedMessage ?: error.message ?: "Unknown error"}"
        _errorMessage.value = errorMessage
        
        // Determine operation type for listeners
        val operation = when (context) {
            "fetchAllEvents" -> EventOperation.FETCH_ALL
            "fetchEventById" -> EventOperation.FETCH_BY_ID
            "likeEvent" -> EventOperation.LIKE
            "unlikeEvent" -> EventOperation.UNLIKE
            "registerForEvent" -> EventOperation.REGISTER
            "shareEvent" -> EventOperation.SHARE
            "submitEvent" -> EventOperation.CREATE
            else -> EventOperation.FETCH_ALL
        }
        
        notifyListeners { it.onError(operation, error) }
        Log.e(TAG, "Error handled: $errorMessage", error)
    }
    
    // ===== Cleanup =====
    
    override fun cleanup() {
        Log.d(TAG, "Cleaning up EventMediator")
        listeners.clear()
        coroutineScope.cancel()
    }
    
    // ===== Private Helper Methods =====
    
    private fun applyFilters(
        events: List<EventResponse>,
        showLive: Boolean,
        query: String,
        category: String?
    ): List<EventResponse> {
        var filtered = events
        
        // Filter by live/past status
        filtered = if (showLive) {
            filtered.filter { it.isLive == true }
        } else {
            filtered.filter { it.isLive == false }
        }
        
        // Filter by search query
        if (query.isNotBlank()) {
            filtered = filtered.filter { event ->
                event.name.contains(query, ignoreCase = true) ||
                event.eventDescription?.contains(query, ignoreCase = true) == true ||
                event.category.contains(query, ignoreCase = true)
            }
        }
        
        // Filter by category
        category?.let { cat ->
            filtered = filtered.filter { it.category == cat }
        }
        
        return filtered
    }
    
    private fun initializeCreationSteps(): List<EventCreationStep> {
        return listOf(
            EventCreationStep(1, "Organizer Information"),
            EventCreationStep(2, "Event Details"),
            EventCreationStep(3, "Date and Time"),
            EventCreationStep(4, "Location and Mode"),
            EventCreationStep(5, "Pricing and Seats"),
            EventCreationStep(6, "Review and Submit")
        )
    }
    
    private inline fun notifyListeners(action: (EventListener) -> Unit) {
        listeners.values.forEach { listener ->
            try {
                action(listener)
            } catch (e: Exception) {
                Log.e(TAG, "Error notifying listener", e)
            }
        }
    }
}
