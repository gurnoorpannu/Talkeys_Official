package com.example.talkeys_new.screens.events

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.talkeys_new.dataModels.EventResponse
import com.example.talkeys_new.screens.events.mediator.*
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Enhanced EventViewModel that uses the EventMediator pattern for better
 * separation of concerns and centralized event management
 */
class EventMediatedViewModel(
    private val context: Context
) : ViewModel(), EventListener {

    companion object {
        private const val TAG = "EventMediatedViewModel"
        private const val LISTENER_KEY = "EventMediatedViewModel"
    }

    // Get mediator instance
    private val mediator = EventMediatorProvider.getMediator(context)

    // Expose state flows from mediator
    val eventList: StateFlow<List<EventResponse>> = mediator.eventList
    val selectedEvent: StateFlow<EventResponse?> = mediator.selectedEvent
    val isLoading: StateFlow<Boolean> = mediator.isLoading
    val errorMessage: StateFlow<String?> = mediator.errorMessage

    // Additional properties for filtered events if using EventMediatorImpl
    val filteredEvents: StateFlow<List<EventResponse>>
        get() = (mediator as? EventMediatorImpl)?.filteredEvents 
            ?: mediator.eventList

    val showLiveEvents: StateFlow<Boolean>
        get() = (mediator as? EventMediatorImpl)?.showLiveEvents 
            ?: kotlinx.coroutines.flow.MutableStateFlow(true)

    val searchQuery: StateFlow<String>
        get() = (mediator as? EventMediatorImpl)?.searchQuery 
            ?: kotlinx.coroutines.flow.MutableStateFlow("")

    val likedEvents: StateFlow<Set<String>>
        get() = (mediator as? EventMediatorImpl)?.likedEvents 
            ?: kotlinx.coroutines.flow.MutableStateFlow(emptySet())

    init {
        // Register as listener
        EventMediatorProvider.addListener(LISTENER_KEY, this)
        Log.d(TAG, "EventMediatedViewModel initialized and registered as listener")
    }

    // ===== Event Data Operations =====

    /**
     * Fetch all events
     * @param forceRefresh If true, bypasses cache and fetches fresh data
     */
    fun fetchAllEvents(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            mediator.fetchAllEvents(forceRefresh)
        }
    }

    /**
     * Fetch a specific event by ID
     * @param eventId The ID of the event to fetch
     * @param forceRefresh If true, bypasses cache and fetches fresh data
     */
    fun fetchEventById(eventId: String, forceRefresh: Boolean = false) {
        viewModelScope.launch {
            mediator.fetchEventById(eventId, forceRefresh)
        }
    }

    /**
     * Refresh all events
     */
    fun refreshEvents() {
        viewModelScope.launch {
            mediator.refreshEvents()
        }
    }

    // ===== Event Actions =====

    /**
     * Like an event
     * @param eventId The ID of the event to like
     */
    fun likeEvent(eventId: String) {
        viewModelScope.launch {
            val success = mediator.likeEvent(eventId)
            Log.d(TAG, "Like event $eventId: $success")
        }
    }

    /**
     * Unlike an event
     * @param eventId The ID of the event to unlike
     */
    fun unlikeEvent(eventId: String) {
        viewModelScope.launch {
            val success = mediator.unlikeEvent(eventId)
            Log.d(TAG, "Unlike event $eventId: $success")
        }
    }

    /**
     * Register for an event
     * @param eventId The ID of the event to register for
     */
    fun registerForEvent(eventId: String) {
        viewModelScope.launch {
            val success = mediator.registerForEvent(eventId)
            Log.d(TAG, "Register for event $eventId: $success")
        }
    }

    /**
     * Share an event
     * @param event The event to share
     */
    fun shareEvent(event: EventResponse) {
        viewModelScope.launch {
            val success = mediator.shareEvent(event)
            Log.d(TAG, "Share event ${event.name}: $success")
        }
    }

    // ===== Filter and Search Operations =====

    /**
     * Toggle between live and past events filter
     */
    fun toggleEventFilter() {
        mediator.toggleEventFilter()
    }

    /**
     * Apply search filter
     * @param query Search query string
     */
    fun applySearchFilter(query: String) {
        mediator.applySearchFilter(query)
    }

    /**
     * Apply category filter
     * @param category Category to filter by
     */
    fun applyCategoryFilter(category: String) {
        mediator.applyCategoryFilter(category)
    }

    /**
     * Clear all filters
     */
    fun clearAllFilters() {
        mediator.clearAllFilters()
    }

    // ===== Navigation =====

    /**
     * Navigate to event detail
     * @param eventId The ID of the event
     */
    fun navigateToEventDetail(eventId: String) {
        mediator.navigateToEventDetail(eventId)
    }

    /**
     * Navigate to event creation
     */
    fun navigateToEventCreation() {
        mediator.navigateToEventCreation()
    }

    /**
     * Navigate to event list
     */
    fun navigateToEventList() {
        mediator.navigateToEventList()
    }

    // ===== Event Creation Flow =====

    /**
     * Start event creation flow
     */
    fun startEventCreation() {
        mediator.startEventCreation()
    }

    /**
     * Proceed to next step in event creation
     * @param stepData Data from current step
     */
    fun proceedToNextStep(stepData: Map<String, Any>) {
        mediator.proceedToNextStep(stepData)
    }

    /**
     * Go to previous step in event creation
     */
    fun goToPreviousStep() {
        mediator.goToPreviousStep()
    }

    /**
     * Save event draft
     * @param stepData Current step data
     */
    fun saveEventDraft(stepData: Map<String, Any>) {
        mediator.saveEventDraft(stepData)
    }

    /**
     * Submit event for creation
     * @param eventData Complete event data
     */
    fun submitEvent(eventData: Map<String, Any>) {
        val success = mediator.submitEvent(eventData)
        Log.d(TAG, "Submit event: $success")
    }

    /**
     * Cancel event creation
     */
    fun cancelEventCreation() {
        mediator.cancelEventCreation()
    }

    // ===== Error Handling =====

    /**
     * Clear all errors
     */
    fun clearErrors() {
        mediator.clearErrors()
    }

    // ===== Utility Methods =====

    /**
     * Check if an event is liked
     * @param eventId The ID of the event
     * @return True if the event is liked
     */
    fun isEventLiked(eventId: String): Boolean {
        return likedEvents.value.contains(eventId)
    }

    /**
     * Get current filter description
     */
    fun getCurrentFilterDescription(): String {
        return if (showLiveEvents.value) "Live Events" else "Past Events"
    }

    /**
     * Get current event count
     */
    fun getCurrentEventCount(): Int {
        return filteredEvents.value.size
    }

    /**
     * Check if there are any events available
     */
    fun hasEvents(): Boolean {
        return eventList.value.isNotEmpty()
    }

    // ===== EventListener Implementation =====

    override fun onEventUpdated(event: EventResponse) {
        Log.d(TAG, "Event updated: ${event.name}")
        // Additional handling if needed
    }

    override fun onEventDeleted(eventId: String) {
        Log.d(TAG, "Event deleted: $eventId")
        // Additional handling if needed
    }

    override fun onEventLiked(eventId: String, isLiked: Boolean) {
        Log.d(TAG, "Event $eventId liked: $isLiked")
        // Additional handling if needed
    }

    override fun onEventRegistered(eventId: String, isRegistered: Boolean) {
        Log.d(TAG, "Event $eventId registered: $isRegistered")
        // Additional handling if needed
    }

    override fun onEventsRefreshed(events: List<EventResponse>) {
        Log.d(TAG, "Events refreshed: ${events.size} events")
        // Additional handling if needed
    }

    override fun onError(operation: EventOperation, error: Throwable) {
        Log.e(TAG, "Error in operation $operation", error)
        // Additional handling if needed
    }

    // ===== ViewModel Lifecycle =====

    override fun onCleared() {
        super.onCleared()
        // Unregister listener
        EventMediatorProvider.removeListener(LISTENER_KEY)
        Log.d(TAG, "EventMediatedViewModel cleared and listener unregistered")
    }
}
