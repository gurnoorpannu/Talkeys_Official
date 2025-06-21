package com.example.talkeys_new.screens.events

import com.example.talkeys_new.dataModels.EventListResponse
import com.example.talkeys_new.dataModels.EventResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface EventApiService {

    @GET("getEvents")
    suspend fun getAllEvents(): Response<EventListResponse>

    @GET("getEventById/{id}")
    suspend fun getEventById(
        @Path("id") eventId: String
    ): Response<EventResponse>
}

