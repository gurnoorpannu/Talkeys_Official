package com.example.talkeys_new.screens.events

import android.util.Log
import com.example.talkeys_new.dataModels.EventResponse
import com.example.talkeys_new.utils.NetworkUtils
import com.example.talkeys_new.utils.Result
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
     * Get all events from the API
     * @return Result containing List<EventResponse> or error
     */
    suspend fun getAllEvents(): Result<List<EventResponse>> = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d(TAG, "Making API call to getAllEvents...")
            
            val result = NetworkUtils.safeApiCall {
                api.getAllEvents()
            }
            
            when (result) {
                is Result.Success -> {
                    val events = result.data.data.events
                    Log.d(TAG, "Events extracted: ${events.size}")
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
     * Get event by ID from the API
     * @param eventId The ID of the event to retrieve
     * @return Result containing EventResponse or error
     */
    suspend fun getEventById(eventId: String): Result<EventResponse> = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d(TAG, "Making API call to getEventById with ID: $eventId")
            
            if (eventId.isBlank()) {
                return@withContext Result.Error(
                    exception = IllegalArgumentException("Event ID cannot be empty"),
                    message = "Event ID cannot be empty"
                )
            }
            
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