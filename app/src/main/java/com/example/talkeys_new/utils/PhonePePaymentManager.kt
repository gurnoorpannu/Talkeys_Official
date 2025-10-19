package com.example.talkeys_new.utils

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import com.phonepe.intent.sdk.api.PhonePeKt
import com.phonepe.intent.sdk.api.PhonePeInitException
import com.talkeys.shared.data.payment.PaymentRepository
import com.talkeys.shared.data.payment.Friend
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * PhonePe Payment Manager
 * Handles PhonePe payment integration and checkout flow with backend integration
 */
object PhonePePaymentManager : KoinComponent {
    
    private const val TAG = "PhonePePayment"
    private const val B2B_PG_REQUEST_CODE = 777
    
    // PRODUCTION CONFIGURATION - Should match PhonePeConfig
    private const val PRODUCTION_MODE = PhonePeConfig.IS_PRODUCTION
    
    private val paymentRepository: PaymentRepository by inject()
    
    // Store current payment details for verification
    private var currentMerchantOrderId: String? = null
    private var currentPaymentCallback: ((PaymentResult) -> Unit)? = null
    private var currentAuthToken: String? = null
    
    /**
     * Book ticket and initiate PhonePe payment
     * This method handles the complete flow: book ticket -> get payment token -> start PhonePe
     */
    fun bookTicketAndPay(
        activity: Activity,
        eventId: String,
        passType: String,
        friends: List<Friend>,
        activityResultLauncher: ActivityResultLauncher<Intent>,
        authToken: String? = null,
        onResult: (PaymentResult) -> Unit
    ) {
        currentPaymentCallback = onResult
        currentAuthToken = authToken
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (PRODUCTION_MODE) {
                    Log.i(TAG, "Production: Booking ticket for event: $eventId")
                } else {
                    Log.d(TAG, "Booking ticket for event: $eventId")
                }
                
                val result = paymentRepository.bookTicket(eventId, passType, friends, authToken)
                
                result.fold(
                    onSuccess = { paymentOrderData ->
                        Log.d(TAG, "Ticket booked successfully. Starting PhonePe payment...")
                        
                        // Store merchant order ID for verification
                        currentMerchantOrderId = paymentOrderData.merchantOrderId
                        
                        withContext(Dispatchers.Main) {
                            // ✅ Log what backend is sending
                            val paymentToken = paymentOrderData.token
                            val orderId = paymentOrderData.orderId
                            
                            Log.d(TAG, "Backend returned token: ${paymentToken.take(100)}...")
                            Log.d(TAG, "Token length: ${paymentToken.length}")
                            Log.d(TAG, "Order ID: $orderId")
                            Log.d(TAG, "Is URL format: ${paymentToken.startsWith("http")}")
                            
                            // ✅ Backend developer is correct - mobile apps use token directly
                            Log.d(TAG, "✅ Received PhonePe token from backend")
                            Log.d(TAG, "📱 Mobile SDK Environment: ${PhonePeConfig.getEnvironmentName()}")
                            Log.d(TAG, "🔑 SDK Client ID: ${PhonePeConfig.CLIENT_ID}")
                            
                            if (paymentToken.startsWith("http") && paymentToken.contains("mercury")) {
                                // Backend sent URL (for websites) - extract token for mobile app
                                Log.d(TAG, "🌐 Website URL detected - extracting token for mobile app")
                                val extractedToken = extractTokenFromUrl(paymentToken)
                                Log.d(TAG, "📱 Extracted token for mobile SDK")
                                
                                startPhonePeCheckout(
                                    activity = activity,
                                    token = extractedToken,
                                    orderId = orderId,
                                    activityResultLauncher = activityResultLauncher
                                )
                            } else {
                                // Backend sent token directly (correct for mobile apps)
                                Log.d(TAG, "📱 Direct token received (correct for mobile apps)")
                                Log.d(TAG, "⚠️ If this fails with 'Invalid Token format', check environment mismatch:")
                                Log.d(TAG, "⚠️ Backend environment must match mobile SDK environment")
                                Log.d(TAG, "⚠️ Current SDK: ${PhonePeConfig.getEnvironmentName()}")
                                
                                startPhonePeCheckout(
                                    activity = activity,
                                    token = paymentToken,
                                    orderId = orderId,
                                    activityResultLauncher = activityResultLauncher
                                )
                            }
                        }
                    },
                    onFailure = { exception ->
                        Log.e(TAG, "Failed to book ticket: ${exception.message}", exception)
                        withContext(Dispatchers.Main) {
                            onResult(PaymentResult.Error("Failed to create payment order: ${exception.message}"))
                        }
                    }
                )
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error during ticket booking: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    onResult(PaymentResult.Error("Unexpected error: ${e.message}"))
                }
            }
        }
    }
    
    /**
     * Initiate PhonePe standard checkout
     * 
     * @param activity Current activity context
     * @param token Payment token received from backend (Create Order API response)
     * @param orderId Order ID received from backend (Create Order API response)
     * @param activityResultLauncher Activity result launcher to handle payment result
     */
    private fun startPhonePeCheckout(
        activity: Activity,
        token: String,
        orderId: String,
        activityResultLauncher: ActivityResultLauncher<Intent>
    ) {
        try {
            Log.d(TAG, "Starting PhonePe checkout for Order ID: $orderId")
            Log.d(TAG, "Token length: ${token.length}, Token preview: ${token.take(50)}...")
            
            // ✅ Validate inputs before calling PhonePe SDK
            if (token.isBlank()) {
                throw IllegalArgumentException("Payment token is empty")
            }
            if (orderId.isBlank()) {
                throw IllegalArgumentException("Order ID is empty")
            }
            
            // ✅ Check if PhonePe app is installed
            val packageManager = activity.packageManager
            try {
                packageManager.getPackageInfo("com.phonepe.app", 0)
                Log.d(TAG, "PhonePe app is installed")
            } catch (e: Exception) {
                Log.w(TAG, "PhonePe app might not be installed: ${e.message}")
            }
            
            PhonePeKt.startCheckoutPage(
                context = activity,
                token = token,
                orderId = orderId,
                activityResultLauncher = activityResultLauncher
            )
            
            Log.d(TAG, "PhonePe checkout initiated successfully")
            
        } catch (ex: PhonePeInitException) {
            Log.e(TAG, "PhonePe initialization error: ${ex.message}", ex)
            Log.e(TAG, "SDK Environment: ${PhonePeConfig.getEnvironmentName()}")
            Log.e(TAG, "Client ID: ${PhonePeConfig.CLIENT_ID}")
            handlePaymentError(ex, "PhonePe SDK not initialized properly. Please restart the app.")
        } catch (ex: IllegalArgumentException) {
            Log.e(TAG, "Invalid payment parameters: ${ex.message}", ex)
            handlePaymentError(ex, "Invalid payment data: ${ex.message}")
        } catch (ex: Exception) {
            Log.e(TAG, "Error starting PhonePe checkout: ${ex.message}", ex)
            handlePaymentError(ex, "Transaction could not be started: ${ex.message}")
        }
    }
    
    /**
     * Extract token from PhonePe payment URL
     * URL format: https://mercury-t2.phonepe.com/transact/pg?token=ACTUAL_TOKEN
     */
    private fun extractTokenFromUrl(paymentUrl: String): String {
        return try {
            val uri = android.net.Uri.parse(paymentUrl)
            val token = uri.getQueryParameter("token")
            
            if (token != null) {
                Log.d(TAG, "Extracted token from URL: ${token.take(50)}...")
                token
            } else {
                Log.w(TAG, "No token parameter found in URL, using full URL")
                paymentUrl
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to extract token from URL: ${e.message}")
            paymentUrl
        }
    }
    
    /**
     * Legacy method for direct PhonePe checkout (for backward compatibility)
     */
    fun startCheckout(
        activity: Activity,
        token: String,
        orderId: String,
        activityResultLauncher: ActivityResultLauncher<Intent>
    ) {
        startPhonePeCheckout(activity, token, orderId, activityResultLauncher)
    }
    
    /**
     * Handle payment result from ActivityResultLauncher
     * This method automatically verifies payment status with backend
     * 
     * @param resultCode Result code from the payment activity
     * @param data Intent data from the payment activity
     * @param onPaymentResult Callback to handle the payment result (optional, uses stored callback if not provided)
     */
    fun handlePaymentResult(
        resultCode: Int,
        data: Intent?,
        onPaymentResult: ((PaymentResult) -> Unit)? = null
    ) {
        Log.d(TAG, "Handling payment result with code: $resultCode")
        
        // ✅ Enhanced logging for debugging
        data?.let { intent ->
            Log.d(TAG, "Payment result data extras:")
            intent.extras?.let { bundle ->
                for (key in bundle.keySet()) {
                    val value = bundle.get(key)
                    Log.d(TAG, "  $key: $value")
                }
            } ?: Log.d(TAG, "  No extras in result data")
        } ?: Log.d(TAG, "No result data received")
        
        val callback = onPaymentResult ?: currentPaymentCallback
        
        when (resultCode) {
            Activity.RESULT_OK -> {
                Log.d(TAG, "Payment activity completed. Verifying payment status...")
                
                // ✅ Add delay before verification to allow backend processing
                CoroutineScope(Dispatchers.IO).launch {
                    kotlinx.coroutines.delay(2000) // Wait 2 seconds
                    
                    // Verify payment status with backend
                    currentMerchantOrderId?.let { merchantOrderId ->
                        verifyPaymentStatusOnServer(merchantOrderId, currentAuthToken, callback)
                    } ?: run {
                        Log.e(TAG, "No merchant order ID found for verification")
                        withContext(Dispatchers.Main) {
                            callback?.invoke(PaymentResult.Error("Payment verification failed: No order ID"))
                        }
                    }
                }
            }
            Activity.RESULT_CANCELED -> {
                Log.d(TAG, "Payment was cancelled by user")
                callback?.invoke(PaymentResult.Cancelled("Payment cancelled by user"))
                clearCurrentPayment()
            }
            else -> {
                Log.w(TAG, "Unknown payment result code: $resultCode")
                // ✅ Don't immediately fail - try to verify status anyway
                currentMerchantOrderId?.let { merchantOrderId ->
                    Log.d(TAG, "Attempting verification despite unknown result code")
                    verifyPaymentStatusOnServer(merchantOrderId, currentAuthToken, callback)
                } ?: run {
                    callback?.invoke(PaymentResult.Error("Unknown payment result and no order ID"))
                    clearCurrentPayment()
                }
            }
        }
    }
    
    /**
     * Verify payment status on server (as per backend developer's specification)
     * GET /api/payment/app-status-check/:merchantOrderId
     */
    private fun verifyPaymentStatusOnServer(
        merchantOrderId: String,
        authToken: String? = null,
        onResult: ((PaymentResult) -> Unit)?
    ) {
        Log.d(TAG, "Verifying payment status for order: $merchantOrderId")
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = paymentRepository.verifyPaymentStatus(merchantOrderId, authToken)
                
                result.fold(
                    onSuccess = { paymentStatusData ->
                        Log.d(TAG, "Payment verification successful. Status: ${paymentStatusData.paymentStatus}")
                        
                        withContext(Dispatchers.Main) {
                            when (paymentStatusData.paymentStatus.uppercase()) {
                                "COMPLETED" -> {
                                    onResult?.invoke(
                                        PaymentResult.Success(
                                            message = "Payment completed successfully",
                                            passId = paymentStatusData.passId,
                                            passUUID = paymentStatusData.passUUID
                                        )
                                    )
                                }
                                "FAILED" -> {
                                    onResult?.invoke(PaymentResult.Failed("Payment failed"))
                                }
                                "PENDING" -> {
                                    onResult?.invoke(PaymentResult.Pending("Payment is still pending"))
                                }
                                else -> {
                                    onResult?.invoke(PaymentResult.Error("Unknown payment status: ${paymentStatusData.paymentStatus}"))
                                }
                            }
                        }
                        clearCurrentPayment()
                    },
                    onFailure = { exception ->
                        Log.e(TAG, "Payment verification failed: ${exception.message}", exception)
                        withContext(Dispatchers.Main) {
                            onResult?.invoke(PaymentResult.Error("Payment verification failed: ${exception.message}"))
                        }
                        clearCurrentPayment()
                    }
                )
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error during payment verification: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    onResult?.invoke(PaymentResult.Error("Verification error: ${e.message}"))
                }
                clearCurrentPayment()
            }
        }
    }
    
    /**
     * Clear current payment data
     */
    private fun clearCurrentPayment() {
        currentMerchantOrderId = null
        currentPaymentCallback = null
        currentAuthToken = null
    }
    
    /**
     * Handle payment errors
     */
    private fun handlePaymentError(exception: Exception, message: String) {
        Log.e(TAG, "Payment Error: $message", exception)
        
        currentPaymentCallback?.invoke(PaymentResult.Error(message))
        clearCurrentPayment()
        
        when (exception) {
            is PhonePeInitException -> {
                Log.e(TAG, "SDK needs to be re-initialized")
                // Either re-init the SDK and call startCheckoutPage again 
                // or use other ways to complete your transaction
            }
            else -> {
                Log.e(TAG, "General payment error occurred")
            }
        }
    }
    
    /**
     * Sealed class to represent different payment result states
     */
    sealed class PaymentResult {
        data class Success(
            val message: String,
            val passId: String,
            val passUUID: String
        ) : PaymentResult()
        
        data class Failed(val message: String) : PaymentResult()
        data class Pending(val message: String) : PaymentResult()
        data class Cancelled(val message: String) : PaymentResult()
        data class Error(val message: String) : PaymentResult()
        
        // Legacy support
        data class Completed(val message: String) : PaymentResult()
    }
    
    /**
     * Data class for payment request
     */
    data class PaymentRequest(
        val token: String,
        val orderId: String,
        val amount: Double,
        val description: String? = null
    )
}