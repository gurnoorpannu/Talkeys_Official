package com.talkeys.shared.network

import com.talkeys.shared.data.payment.BookTicketRequest
import com.talkeys.shared.data.payment.BookTicketResponse
import com.talkeys.shared.data.payment.PaymentStatusResponse
import io.ktor.client.call.*
import io.ktor.client.plugins.timeout
import io.ktor.client.request.*
import io.ktor.http.*

class PaymentApiService(private val apiClient: ApiClient) {
    
    /**
     * Book ticket and create payment order
     * POST /api/book-ticket-app
     */
    suspend fun bookTicketApp(request: BookTicketRequest, authToken: String? = null): Result<BookTicketResponse> {
        return try {
            val response = apiClient.httpClient.post("${ApiClient.BASE_URL}/api/book-ticket-app") {
                contentType(ContentType.Application.Json)
                
                // Production: Add authentication header as required by backend
                authToken?.let { token ->
                    header("Authorization", "Bearer $token")
                    // Debug: Check if we're sending the token
                    println("PaymentAPI: Sending auth token (length: ${token.length})")
                } ?: run {
                    println("PaymentAPI: ERROR - No auth token provided!")
                }
                
                // Debug: Log the full request details
                println("PaymentAPI: POST ${ApiClient.BASE_URL}/api/book-ticket-app")
                println("PaymentAPI: Request body: $request")
                
                // Request timeout (30 seconds)
                timeout {
                    requestTimeoutMillis = 30000
                }
                
                setBody(request)
            }
            
            if (response.status.isSuccess()) {
                val bookTicketResponse = response.body<BookTicketResponse>()
                println("PaymentAPI: Success response received")
                Result.success(bookTicketResponse)
            } else {
                println("PaymentAPI: Error response - Status: ${response.status.value}")
                println("PaymentAPI: Error description: ${response.status.description}")
                // Try to get error body for more details
                try {
                    val errorBody = response.body<String>()
                    println("PaymentAPI: Error body: $errorBody")
                } catch (e: Exception) {
                    println("PaymentAPI: Could not read error body: ${e.message}")
                }
                Result.failure(Exception("HTTP ${response.status.value}: ${response.status.description}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Check payment status after PhonePe payment completion
     * GET /api/payment/app-status-check/:merchantOrderId
     */
    suspend fun checkPaymentStatus(merchantOrderId: String, authToken: String? = null): Result<PaymentStatusResponse> {
        return try {
            val response = apiClient.httpClient.get("${ApiClient.BASE_URL}/api/payment/app-status-check/$merchantOrderId") {
                
                // Add authentication header if token is provided
                authToken?.let { token ->
                    header("Authorization", "Bearer $token")
                }
                
                // Request timeout (30 seconds)
                timeout {
                    requestTimeoutMillis = 30000
                }
            }
            
            if (response.status.isSuccess()) {
                val statusResponse = response.body<PaymentStatusResponse>()
                Result.success(statusResponse)
            } else {
                Result.failure(Exception("HTTP ${response.status.value}: ${response.status.description}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}