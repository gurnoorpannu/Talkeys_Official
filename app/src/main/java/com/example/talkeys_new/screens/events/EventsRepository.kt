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
        return api.getEventById(eventId)
    }
}