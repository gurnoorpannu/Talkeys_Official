package com.talkeys.shared.data.payment

import com.talkeys.shared.network.PaymentApiService
import com.talkeys.shared.config.ProductionConfig
import co.touchlab.kermit.Logger

class PaymentRepository(private val paymentApiService: PaymentApiService) {
    
    private val logger = Logger.withTag("PaymentRepository")
    
    init {
        // Production validation
        if (ProductionConfig.IS_PRODUCTION) {
            logger.i { "PaymentRepository initialized in PRODUCTION mode" }
        }
    }
    
    /**
     * Book ticket and get payment order details
     */
    suspend fun bookTicket(
        eventId: String,
        passType: String,
        friends: List<Friend>,
        authToken: String? = null
    ): Result<PaymentOrderData> {
        // Production validation
        if (friends.size > ProductionConfig.MAX_FRIENDS_PER_BOOKING) {
            return Result.failure(Exception("Maximum ${ProductionConfig.MAX_FRIENDS_PER_BOOKING} friends allowed per booking"))
        }
        
        logger.i { "Production: Booking ticket for event: $eventId, passType: $passType, friends: ${friends.size}" }
        
        val request = BookTicketRequest(
            eventId = eventId,
            passType = passType,
            friends = friends
        )
        
        return try {
            val result = paymentApiService.bookTicketApp(request, authToken)
            
            result.fold(
                onSuccess = { response ->
                    if (response.success && response.data != null) {
                        logger.d { "Ticket booking successful. Order ID: ${response.data.merchantOrderId}" }
                        Result.success(response.data)
                    } else {
                        val error = "Booking failed: ${response.message}"
                        logger.e { error }
                        Result.failure(Exception(error))
                    }
                },
                onFailure = { exception ->
                    logger.e(exception) { "Network error during ticket booking" }
                    Result.failure(exception)
                }
            )
        } catch (e: Exception) {
            logger.e(e) { "Unexpected error during ticket booking" }
            Result.failure(e)
        }
    }
    
    /**
     * Verify payment status after PhonePe payment completion
     */
    suspend fun verifyPaymentStatus(merchantOrderId: String, authToken: String? = null): Result<PaymentStatusData> {
        logger.d { "Verifying payment status for order: $merchantOrderId" }
        
        return try {
            val result = paymentApiService.checkPaymentStatus(merchantOrderId, authToken)
            
            result.fold(
                onSuccess = { response ->
                    if (response.success && response.data != null) {
                        logger.d { "Payment status: ${response.status} for order: $merchantOrderId" }
                        Result.success(response.data)
                    } else {
                        val error = "Payment verification failed for order: $merchantOrderId"
                        logger.e { error }
                        Result.failure(Exception(error))
                    }
                },
                onFailure = { exception ->
                    logger.e(exception) { "Network error during payment verification" }
                    Result.failure(exception)
                }
            )
        } catch (e: Exception) {
            logger.e(e) { "Unexpected error during payment verification" }
            Result.failure(e)
        }
    }
}