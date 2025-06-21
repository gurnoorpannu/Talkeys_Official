package com.example.talkeys_new.dataModels

data class UserResponse(
    val accessToken: String,
    val name: String
)

data class VerifyRequest(
    val token: String
)

data class VerifyResponse(
    val accessToken: String,
    val name: String
)

data class EventResponse(
    val _id: String,
    val name: String,
    val category: String,
    val ticketPrice: Any, // Changed from Int to Any to handle both Int and String
    val mode: String,
    val location: String?,
    val duration: String,
    val slots: Int,
    val visibility: String,
    val startDate: String,
    val startTime: String,
    val endRegistrationDate: String,
    val totalSeats: Any, // Changed from Int to Any to handle both Int and String
    val eventDescription: String?,
    val photographs: List<String>?,
    val prizes: String?,
    val isTeamEvent: Boolean,
    val isPaid: Boolean,
    val isLive: Boolean,
    val organizerName: String?,
    val organizerEmail: String?,
    val organizerContact: String?,
)

// Fixed data class to match the actual API response structure
data class EventListResponse(
    val status: String,
    val data: EventData
)

data class EventData(
    val events: List<EventResponse>,
    val pagination: Pagination
)

data class Pagination(
    val total: Int,
    val page: Int,
    val pages: Int,
    val limit: Int
)