package com.example.talkeys_new.screens.events

import android.util.Log
import com.example.talkeys_new.dataModels.EventResponse
import com.example.talkeys_new.utils.NetworkUtils
import com.example.talkeys_new.utils.Result
import com.example.talkeys_new.utils.cache.CacheManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.io.IOException

/**
 * Repository class that handles all events-related API calls
 * Implements proper error handling using Result wrapper
 */
class EventsRepository(private val api: EventApiService) {
    private val TAG = "EventsRepository"
    
    /**
     * Get all events from the API with caching support
     * @param forceRefresh If true, bypasses cache and fetches fresh data
     * @return Result containing List<EventResponse> or error
     */
    suspend fun getAllEvents(forceRefresh: Boolean = false): Result<List<EventResponse>> = withContext(Dispatchers.IO) {
        return@withContext try {
            // Check cache first unless force refresh is requested
            if (!forceRefresh) {
                val cachedEvents = CacheManager.eventsCache.get(CacheManager.Keys.ALL_EVENTS)
                if (cachedEvents != null) {
                    Log.d(TAG, "Returning cached events: ${cachedEvents.size}")
                    return@withContext Result.Success(cachedEvents)
                }
            }
            
            Log.d(TAG, "Making API call to getAllEvents...")
            
            val result = NetworkUtils.safeApiCall {
                api.getAllEvents()
            }
            
            when (result) {
                is Result.Success -> {
                    val events = result.data.data.events
                    Log.d(TAG, "Events extracted: ${events.size}")
                    
                    // Cache the events
                    CacheManager.eventsCache.put(CacheManager.Keys.ALL_EVENTS, events)
                    Log.d(TAG, "Events cached successfully")
                    
                    Result.Success(events)
                }
                is Result.Error -> {
                    Log.e(TAG, "API call failed: ${result.message}")
                    result
                }
                else -> Result.Error(
                    exception = IllegalStateException("Unexpected result state"),
                    message = "Unexpected result state"
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception in getAllEvents: ${e.message}", e)
            Result.Error(
                exception = e,
                message = "Failed to load events: ${e.message}"
            )
        }
    }
    
    /**
     * Get event by ID from the API with caching support
     * @param eventId The ID of the event to retrieve
     * @param forceRefresh If true, bypasses cache and fetches fresh data
     * @return Result containing EventResponse or error
     */
    suspend fun getEventById(eventId: String, forceRefresh: Boolean = false): Result<EventResponse> = withContext(Dispatchers.IO) {
        return@withContext try {
            if (eventId.isBlank()) {
                return@withContext Result.Error(
                    exception = IllegalArgumentException("Event ID cannot be empty"),
                    message = "Event ID cannot be empty"
                )
            }
            
            // Check cache first unless force refresh is requested
            if (!forceRefresh) {
                val cacheKey = CacheManager.Keys.eventDetail(eventId)
                val cachedEvent = CacheManager.eventDetailsCache.get(cacheKey)
                if (cachedEvent != null) {
                    Log.d(TAG, "Returning cached event: ${cachedEvent.name}")
                    return@withContext Result.Success(cachedEvent)
                }
            }
            
            Log.d(TAG, "Making API call to getEventById with ID: $eventId")
            
            val result = NetworkUtils.safeApiCall {
                api.getEventById(eventId)
            }
            
            when (result) {
                is Result.Success -> {
                    val eventDetail = result.data.data
                    if (eventDetail != null) {
                        // Convert EventDetail to EventResponse
                        val eventResponse = EventResponse(
                            _id = eventDetail._id,
                            name = eventDetail.name,
                            category = eventDetail.category,
                            ticketPrice = eventDetail.ticketPrice,
                            mode = eventDetail.mode,
                            location = eventDetail.location,
                            duration = eventDetail.duration,
                            slots = eventDetail.slots,
                            visibility = eventDetail.visibility,
                            startDate = eventDetail.startDate,
                            startTime = eventDetail.startTime,
                            endRegistrationDate = eventDetail.endRegistrationDate,
                            totalSeats = eventDetail.totalSeats,
                            eventDescription = eventDetail.eventDescription,
                            photographs = eventDetail.photographs,
                            prizes = eventDetail.prizes,
                            isTeamEvent = eventDetail.isTeamEvent,
                            isPaid = eventDetail.isPaid,
                            isLive = eventDetail.isLive,
                            organizerName = eventDetail.organizerName,
                            organizerEmail = eventDetail.organizerEmail,
                            organizerContact = eventDetail.organizerContact
                        )
                        
                        // Cache the event details
                        val cacheKey = CacheManager.Keys.eventDetail(eventId)
                        CacheManager.eventDetailsCache.put(cacheKey, eventResponse)
                        Log.d(TAG, "Event details cached for ID: $eventId")
                        
                        Log.d(TAG, "Converted event: ${eventResponse.name}")
                        Result.Success(eventResponse)
                    } else {
                        Log.e(TAG, "Event data is null")
                        Result.Error(
                            exception = IOException("Event not found"),
                            message = "Event not found"
                        )
                    }
                }
                is Result.Error -> {
                    Log.e(TAG, "API call failed: ${result.message}")
                    result
                }
                else -> Result.Error(
                    exception = IllegalStateException("Unexpected result state"),
                    message = "Unexpected result state"
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception in getEventById: ${e.message}", e)
            Result.Error(
                exception = e,
                message = "Failed to load event details: ${e.message}"
            )
        }
    }
}