# Event Mediator Pattern Implementation

## Overview

This document explains the implementation of the **Mediator Pattern** in the Talkeys event management system. The mediator pattern helps decouple event-related components and provides centralized coordination for event operations, state management, and navigation.

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                    Event Mediator Pattern                       │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌─────────────────┐    ┌─────────────────┐    ┌─────────────┐  │
│  │   UI Components │    │  Event Mediator │    │ Repository  │  │
│  │                 │◄──►│                 │◄──►│             │  │
│  │ • ExploreEvents │    │ • Coordinates   │    │ • API calls │  │
│  │ • EventDetail   │    │ • Manages State │    │ • Caching   │  │
│  │ • CreateEvent   │    │ • Handles Nav   │    │ • Data      │  │
│  └─────────────────┘    └─────────────────┘    └─────────────┘  │
│           ▲                       ▲                      ▲      │
│           │                       │                      │      │
│  ┌─────────────────┐    ┌─────────────────┐    ┌─────────────┐  │
│  │   ViewModels    │    │  Event Listeners│    │ Cache Mgr   │  │
│  │                 │    │                 │    │             │  │
│  │ • Mediated VM   │    │ • UI Updates    │    │ • LRU Cache │  │
│  │ • Legacy VM     │    │ • Error Events  │    │ • TTL       │  │
│  └─────────────────┘    └─────────────────┘    └─────────────┘  │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

## Components Overview

### 1. Core Interfaces

#### `EventMediator` Interface
The main contract defining all mediator operations:

```kotlin
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
    suspend fun registerForEvent(eventId: String): Boolean
    suspend fun shareEvent(event: EventResponse): Boolean
    
    // Event Creation Flow
    fun startEventCreation()
    fun proceedToNextStep(stepData: Map<String, Any>)
    fun submitEvent(eventData: Map<String, Any>): Boolean
    
    // Navigation Coordination
    fun navigateToEventDetail(eventId: String)
    fun navigateToEventCreation()
    
    // Error Handling
    fun clearErrors()
    fun handleError(error: Throwable, context: String)
}
```

#### `EventListener` Interface
For components that want to react to event changes:

```kotlin
interface EventListener {
    fun onEventUpdated(event: EventResponse)
    fun onEventLiked(eventId: String, isLiked: Boolean)
    fun onEventRegistered(eventId: String, isRegistered: Boolean)
    fun onEventsRefreshed(events: List<EventResponse>)
    fun onError(operation: EventOperation, error: Throwable)
}
```

### 2. Implementation Classes

#### `EventMediatorImpl`
The concrete mediator implementation that:
- **Coordinates** all event operations across components
- **Manages state** using StateFlow for reactive updates  
- **Handles navigation** between event screens
- **Processes** event actions (like, share, register)
- **Coordinates** the multi-step event creation flow
- **Manages filtering** and search operations
- **Handles errors** centrally with proper logging

Key Features:
- **Reactive State Management**: Uses StateFlow for all state
- **Listener Pattern**: Notifies registered components of changes
- **Thread Safety**: Uses ConcurrentHashMap for listeners
- **Error Handling**: Centralized error processing with context
- **Navigation Abstraction**: Decouples navigation from business logic
- **Caching Integration**: Works with existing LRU cache system

#### `EventMediatorProvider`
Singleton provider for mediator instances:

```kotlin
object EventMediatorProvider {
    fun getMediator(context: Context): EventMediator
    fun setNavController(navController: NavController)
    fun addListener(key: String, listener: EventListener)
    fun removeListener(key: String)
    fun clearInstance()
}
```

### 3. Enhanced ViewModel

#### `EventMediatedViewModel`
An enhanced ViewModel that uses the mediator pattern:

```kotlin
class EventMediatedViewModel(
    private val context: Context
) : ViewModel(), EventListener {
    
    private val mediator = EventMediatorProvider.getMediator(context)
    
    // Expose mediator state
    val eventList: StateFlow<List<EventResponse>> = mediator.eventList
    val isLoading: StateFlow<Boolean> = mediator.isLoading
    val errorMessage: StateFlow<String?> = mediator.errorMessage
    
    // Delegate operations to mediator
    fun fetchAllEvents() = viewModelScope.launch { mediator.fetchAllEvents() }
    fun likeEvent(eventId: String) = viewModelScope.launch { mediator.likeEvent(eventId) }
    fun navigateToEventDetail(eventId: String) = mediator.navigateToEventDetail(eventId)
}
```

## Benefits of the Implementation

### 1. **Separation of Concerns**
- **UI Components**: Focus only on rendering and user interactions
- **Mediator**: Handles business logic and coordination
- **Repository**: Manages data operations and caching
- **ViewModels**: Bridge between UI and mediator

### 2. **Reduced Coupling**
- Components don't directly communicate with each other
- Changes in one component don't directly affect others
- Easy to modify or replace individual components

### 3. **Centralized State Management**
- Single source of truth for all event-related state
- Reactive updates using StateFlow
- Consistent state across all components

### 4. **Enhanced Event Creation Flow**
- Coordinates multi-step event creation process
- Manages step validation and data persistence
- Handles navigation between creation steps
- Supports draft saving and restoration

### 5. **Advanced Filtering and Search**
- Real-time filtering of events
- Search functionality with reactive updates
- Category-based filtering
- Combined filter operations

### 6. **Robust Error Handling**
- Centralized error processing
- Context-aware error messages
- Proper error propagation to UI
- Logging for debugging

### 7. **Navigation Abstraction**
- Decouples navigation logic from business logic
- Consistent navigation patterns
- Easy to modify navigation flows

## Usage Examples

### 1. Basic Event Operations

```kotlin
// In a Composable
@Composable
fun ExploreEventsScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: EventMediatedViewModel = viewModel { EventMediatedViewModel(context) }
    
    // Set navigation controller
    LaunchedEffect(navController) {
        EventMediatorProvider.setNavController(navController)
    }
    
    // Collect state
    val events by viewModel.filteredEvents.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    // Load events
    LaunchedEffect(Unit) {
        viewModel.fetchAllEvents()
    }
    
    // UI implementation with mediator-powered actions
    EventsList(
        events = events,
        isLoading = isLoading,
        onEventClick = { event -> viewModel.navigateToEventDetail(event._id) },
        onLikeEvent = { eventId -> viewModel.likeEvent(eventId) },
        onShareEvent = { event -> viewModel.shareEvent(event) }
    )
}
```

### 2. Event Creation Flow

```kotlin
// Start event creation
viewModel.startEventCreation()

// Navigate through steps
viewModel.proceedToNextStep(mapOf(
    "organizerName" to "John Doe",
    "email" to "john@example.com"
))

// Submit final event
viewModel.submitEvent(completeEventData)
```

### 3. Advanced Filtering

```kotlin
// Apply search filter
viewModel.applySearchFilter("Music")

// Filter by category
viewModel.applyCategoryFilter("Entertainment")

// Toggle live/past events
viewModel.toggleEventFilter()

// Clear all filters
viewModel.clearAllFilters()
```

### 4. Custom Event Listener

```kotlin
class CustomEventHandler : EventListener {
    override fun onEventUpdated(event: EventResponse) {
        // Custom handling for event updates
        analytics.track("event_updated", mapOf("eventId" to event._id))
    }
    
    override fun onEventLiked(eventId: String, isLiked: Boolean) {
        // Custom handling for likes
        if (isLiked) {
            showToast("Event added to favorites!")
        }
    }
    
    override fun onError(operation: EventOperation, error: Throwable) {
        // Custom error handling
        errorTracker.log(operation.name, error)
    }
}

// Register the listener
EventMediatorProvider.addListener("CustomHandler", CustomEventHandler())
```

## Integration with Existing Code

The mediator pattern is designed to work alongside the existing codebase:

### 1. **Gradual Migration**
- Existing ViewModels can continue to work
- New screens can use the mediated approach
- Gradual migration path available

### 2. **Backwards Compatibility**
- Existing API calls and repository methods are preserved
- Cache system integration maintains performance
- No breaking changes to data models

### 3. **Enhanced Functionality**
- Additional features like advanced filtering
- Better error handling and state management
- Improved navigation coordination

## Testing Strategy

### 1. **Unit Tests**
```kotlin
@Test
fun `mediator should fetch events successfully`() = runTest {
    val mediator = EventMediatorImpl(mockRepository, mockContext, testScope)
    
    whenever(mockRepository.getAllEvents(false)).thenReturn(Result.Success(mockEvents))
    
    mediator.fetchAllEvents()
    
    assertEquals(mockEvents, mediator.eventList.value)
}
```

### 2. **Integration Tests**
```kotlin
@Test
fun `event creation flow should work end-to-end`() = runTest {
    val mediator = EventMediatorProvider.getMediator(testContext)
    
    mediator.startEventCreation()
    
    assertEquals(1, mediator.currentCreationStep.value)
    
    mediator.proceedToNextStep(step1Data)
    
    assertEquals(2, mediator.currentCreationStep.value)
}
```

### 3. **UI Tests**
```kotlin
@Test
fun `explore events screen should display filtered events`() {
    composeTestRule.setContent {
        ExploreEventsScreenWithMediator(mockNavController)
    }
    
    composeTestRule.onNodeWithText("Live Events").assertExists()
    composeTestRule.onNodeWithText("Search events...").assertExists()
}
```

## Performance Considerations

### 1. **StateFlow Efficiency**
- Uses StateFlow for reactive state management
- Automatic deduplication of state updates
- Efficient memory usage with proper cleanup

### 2. **Listener Management**
- ConcurrentHashMap for thread-safe listener operations
- Automatic cleanup on ViewModel destruction
- Weak reference patterns where appropriate

### 3. **Caching Integration**
- Maintains existing LRU cache performance
- Smart cache invalidation on data changes
- Optimistic updates for better UX

## Future Enhancements

### 1. **Offline Support**
- Enhanced offline event management
- Sync capabilities when connection restored
- Conflict resolution strategies

### 2. **Real-time Updates**
- WebSocket integration for live updates
- Push notification handling
- Real-time event status changes

### 3. **Analytics Integration**
- Built-in analytics tracking
- User interaction monitoring
- Performance metrics collection

### 4. **Advanced Search**
- Elasticsearch integration
- Voice search capabilities
- AI-powered event recommendations

## Conclusion

The Event Mediator Pattern implementation provides:

1. **Better Architecture**: Clean separation of concerns with centralized coordination
2. **Enhanced UX**: Reactive state management and optimistic updates  
3. **Maintainability**: Reduced coupling and clear component responsibilities
4. **Extensibility**: Easy to add new features and modify existing ones
5. **Testability**: Clear interfaces and dependency injection support
6. **Performance**: Efficient state management and caching integration

The implementation maintains backward compatibility while providing a path forward for enhanced event management capabilities in the Talkeys application.
