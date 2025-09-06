package com.example.talkeys_new.screens.events.mediator

import com.example.talkeys_new.dataModels.EventResponse
import kotlinx.coroutines.flow.StateFlow

/**
 * Event Mediator interface that defines the contract for managing
 * event-related communications and operations across different components
 */
interface EventMediator {
    
    // Event Data Operations
    suspend fun fetchAllEvents(forceRefresh: Boolean = false)
    suspend fun fetchEventById(eventId: String, forceRefresh: Boolean = false)
    suspend fun refreshEvents()
    
    // Event State Management
    val eventList: StateFlow<List<EventResponse>>
    val selectedEvent: StateFlow<EventResponse?>
    val isLoading: StateFlow<Boolean>
    val errorMessage: StateFlow<String?>
    
    // Event Actions
    suspend fun likeEvent(eventId: String): Boolean
    suspend fun unlikeEvent(eventId: String): Boolean
    suspend fun registerForEvent(eventId: String): Boolean
    suspend fun shareEvent(event: EventResponse): Boolean
    
    // Event Creation Flow Coordination
    fun startEventCreation()
    fun proceedToNextStep(stepData: Map<String, Any>)
    fun goToPreviousStep()
    fun saveEventDraft(stepData: Map<String, Any>)
    fun submitEvent(eventData: Map<String, Any>): Boolean
    fun cancelEventCreation()
    
    // Filter and Search Operations  
    fun toggleEventFilter()
    fun applySearchFilter(query: String)
    fun applyCategoryFilter(category: String)
    fun clearAllFilters()
    
    // Navigation Coordination
    fun navigateToEventDetail(eventId: String)
    fun navigateToEventCreation()
    fun navigateToEventList()
    
    // Error Handling
    fun clearErrors()
    fun handleError(error: Throwable, context: String)
    
    // Cleanup
    fun cleanup()
}

/**
 * Event Mediator Component interface for components that want to participate
 * in the event mediation system
 */
interface EventMediatorComponent {
    fun setMediator(mediator: EventMediator)
    fun getMediator(): EventMediator?
}

/**
 * Abstract base class for Event Mediator Components
 */
abstract class BaseEventMediatorComponent : EventMediatorComponent {
    private var _mediator: EventMediator? = null
    
    override fun setMediator(mediator: EventMediator) {
        this._mediator = mediator
    }
    
    override fun getMediator(): EventMediator? = _mediator
    
    protected fun getMediatorInstance(): EventMediator? = _mediator
}

/**
 * Event Creation Step Data class for the multi-step creation flow
 */
data class EventCreationStep(
    val stepNumber: Int,
    val stepName: String,
    val data: Map<String, Any> = emptyMap(),
    val isValid: Boolean = false,
    val errors: List<String> = emptyList()
)

/**
 * Event Action Result sealed class to represent operation results
 */
sealed class EventActionResult {
    object Success : EventActionResult()
    data class Error(val message: String, val exception: Throwable? = null) : EventActionResult()
    object InProgress : EventActionResult()
}

/**
 * Event Operation Types enum for tracking operations
 */
enum class EventOperation {
    FETCH_ALL,
    FETCH_BY_ID,
    LIKE,
    UNLIKE,
    REGISTER,
    SHARE,
    CREATE,
    UPDATE,
    DELETE,
    FILTER,
    SEARCH
}

/**
 * Event Listener interface for components that want to listen to event changes
 */
interface EventListener {
    fun onEventUpdated(event: EventResponse)
    fun onEventDeleted(eventId: String)
    fun onEventLiked(eventId: String, isLiked: Boolean)
    fun onEventRegistered(eventId: String, isRegistered: Boolean)
    fun onEventsRefreshed(events: List<EventResponse>)
    fun onError(operation: EventOperation, error: Throwable)
}
