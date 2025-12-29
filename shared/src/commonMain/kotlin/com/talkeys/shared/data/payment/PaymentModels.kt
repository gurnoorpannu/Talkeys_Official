package com.talkeys.shared.data.payment

import kotlinx.serialization.Serializable

@Serializable
data class BookTicketRequest(
    val eventId: String,
    val passType: String,
    val friends: List<Friend>
)

@Serializable
data class Friend(
    val name: String,
    val email: String
)

@Serializable
data class BookTicketResponse(
    val success: Boolean,
    val message: String,
    val data: PaymentOrderData?
)

@Serializable
data class PaymentOrderData(
    val passId: String,
    val merchantOrderId: String,
    val orderId: String, // Backend sends "orderId" not "phonePeOrderId"
    val amount: Int,
    val amountInPaisa: Int,
    val totalTickets: Int,
    val token: String, // Backend sends "token" not "paymentUrl"
    val event: EventInfo,
    val qrStrings: List<QrString>,
    val friends: List<Friend>
)

@Serializable
data class EventInfo(
    val id: String
)

@Serializable
data class QrString(
    val personName: String
)

@Serializable
data class PaymentStatusResponse(
    val success: Boolean,
    val status: String, // "COMPLETED", "FAILED", "PENDING"
    val data: PaymentStatusData?
)

@Serializable
data class PaymentStatusData(
    val passId: String,
    val passUUID: String? = null, // Make optional since backend may not always include it
    val paymentStatus: String
)