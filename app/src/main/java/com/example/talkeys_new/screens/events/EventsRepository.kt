package com.example.talkeys_new.screens.events

import android.util.Log
import com.example.talkeys_new.dataModels.EventResponse
import retrofit2.Response

class EventsRepository(private val api: EventApiService) {

    suspend fun getAllEvents(): Response<List<EventResponse>> {
        return try {
            Log.d("EventsRepository", "Making API call to getAllEvents...")
            val response = api.getAllEvents()

            Log.d("EventsRepository", "API response code: ${response.code()}")
            Log.d("EventsRepository", "API response message: ${response.message()}")

            if (response.isSuccessful) {
                val body = response.body()
                Log.d("EventsRepository", "Response body: $body")

                val events = body?.data?.events ?: emptyList()
                Log.d("EventsRepository", "Events extracted: ${events.size}")

                events.forEachIndexed { index, event ->
                    Log.d("EventsRepository", "Event $index: ${event.name} - isLive: ${event.isLive}")
                }

                Response.success(events)
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("EventsRepository", "API call failed with code: ${response.code()}")
                Log.e("EventsRepository", "Error body: $errorBody")
                Response.error(response.code(), response.errorBody()!!)
            }
        } catch (e: Exception) {
            Log.e("EventsRepository", "Exception in getAllEvents: ${e.message}", e)
            throw e
        }
    }

    suspend fun getEventById(eventId: String): Response<EventResponse> {
        return try {
            Log.d("EventsRepository", "Making API call to getEventById with ID: $eventId")
            val response = api.getEventById(eventId)

            Log.d("EventsRepository", "API response code: ${response.code()}")

            if (response.isSuccessful) {
                val body = response.body()
                Log.d("EventsRepository", "Response body: $body")

                val eventDetail = body?.data
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

                    Log.d("EventsRepository", "Converted event: ${eventResponse.name}")
                    Response.success(eventResponse)
                } else {
                    Log.e("EventsRepository", "Event data is null")
                    Response.error(404, response.errorBody()!!)
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("EventsRepository", "API call failed with code: ${response.code()}")
                Log.e("EventsRepository", "Error body: $errorBody")
                Response.error(response.code(), response.errorBody()!!)
            }
        } catch (e: Exception) {
            Log.e("EventsRepository", "Exception in getEventById: ${e.message}", e)
            throw e
        }
    }
}