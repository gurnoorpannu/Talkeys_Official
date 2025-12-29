package com.example.talkeys_new.utils

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import com.phonepe.intent.sdk.api.PhonePeKt
import com.phonepe.intent.sdk.api.PhonePeInitException
import com.phonepe.intent.sdk.api.models.PhonePeEnvironment
import com.talkeys.shared.data.payment.PaymentRepository
import com.talkeys.shared.data.payment.Friend
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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
        
        // ✅ Add timeout mechanism for unresponsive payment screens
        CoroutineScope(Dispatchers.IO).launch {
            Log.d(TAG, "🕐 Starting 60-second payment timeout timer...")
            delay(60000) // Wait 60 seconds
            
            currentMerchantOrderId?.let { orderId ->
                Log.d(TAG, "⏰ Payment timeout reached, checking status for: $orderId")
                Log.d(TAG, "💡 This handles cases where PhonePe WebView becomes unresponsive")
                Log.d(TAG, "🔍 WebView might show errors but payment could still succeed")
                
                verifyPaymentStatusOnServer(orderId, authToken) { result ->
                    Log.d(TAG, "📋 Timeout verification result: $result")
                    when (result) {
                        is PaymentResult.Success -> {
                            Log.d(TAG, "✅ Payment succeeded despite WebView timeout/errors!")
                            Log.d(TAG, "🎉 This proves WebView errors are cosmetic")
                            onResult(result)
                            clearCurrentPayment()
                        }
                        is PaymentResult.Pending -> {
                            Log.d(TAG, "⏳ Payment still pending after timeout - will retry")
                            // Don't clear - let user try again
                        }
                        is PaymentResult.Failed -> {
                            Log.d(TAG, "❌ Payment confirmed failed after timeout")
                            onResult(result)
                            clearCurrentPayment()
                        }
                        else -> {
                            Log.d(TAG, "❓ Unknown status after timeout: $result")
                            // Don't override if user is still trying
                        }
                    }
                }
            } ?: run {
                Log.w(TAG, "⚠️ No merchant order ID found for timeout verification")
            }
        }
        
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
                            
                            Log.d(TAG, "✅ Received payment data from backend")
                            Log.d(TAG, "📱 Mobile SDK Environment: ${PhonePeConfig.getEnvironmentName()}")
                            Log.d(TAG, "🏪 Merchant ID: ${PhonePeConfig.MERCHANT_ID}")
                            Log.d(TAG, "🔑 Client ID: ${PhonePeConfig.CLIENT_ID}")
                            Log.d(TAG, "📋 Order ID: $orderId")
                            
                            // ✅ Debug token format
                            debugTokenFormat(paymentToken)
                            
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
     * Initiate PhonePe standard checkout with enhanced app detection and WebView fallback
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
            Log.d(TAG, "🚀 Starting PhonePe checkout for Order ID: $orderId")
            Log.d(TAG, "📱 Token length: ${token.length}, Token preview: ${token.take(50)}...")
            
            // ✅ Enhanced token validation
            if (token.isBlank()) {
                throw IllegalArgumentException("Payment token is empty")
            }
            if (orderId.isBlank()) {
                throw IllegalArgumentException("Order ID is empty")
            }
            
            // ✅ Validate token format - PhonePe tokens should be longer than JWT tokens
            if (token.startsWith("eyJ")) {
                Log.e(TAG, "❌ JWT token detected instead of PhonePe token!")
                Log.e(TAG, "❌ Backend is sending JWT token, but PhonePe expects their own token format")
                Log.e(TAG, "❌ Backend must return the token from PhonePe Create Order API response")
                throw IllegalArgumentException("Invalid token format: JWT token received instead of PhonePe token")
            }
            
            if (token.length < 200) {
                Log.w(TAG, "⚠️ Token seems short for PhonePe format (${token.length} chars)")
                Log.w(TAG, "⚠️ Expected PhonePe tokens are typically 400+ characters")
            }
            
            // ✅ Enhanced PhonePe app detection with multiple checks
            val isPhonePeInstalled = checkPhonePeAppInstallation(activity)
            Log.d(TAG, "📱 PhonePe app installed: $isPhonePeInstalled")
            
            // ✅ Additional validation before SDK call
            Log.d(TAG, "🔍 Pre-checkout validation:")
            Log.d(TAG, "  - SDK Environment: ${PhonePeConfig.getEnvironmentName()}")
            Log.d(TAG, "  - Merchant ID: ${PhonePeConfig.MERCHANT_ID}")
            Log.d(TAG, "  - Client ID: ${PhonePeConfig.CLIENT_ID}")
            Log.d(TAG, "  - Token format: ${if (token.startsWith("http")) "URL" else "Direct token"}")
            Log.d(TAG, "  - Order ID format: $orderId")
            Log.d(TAG, "  - PhonePe App Available: $isPhonePeInstalled")
            
            // ✅ Try PhonePe SDK first (will use app if available, WebView if not)
            try {
                PhonePeKt.startCheckoutPage(
                    context = activity,
                    token = token,
                    orderId = orderId,
                    activityResultLauncher = activityResultLauncher
                )
                
                Log.d(TAG, "✅ PhonePe checkout initiated successfully")
                
                if (!isPhonePeInstalled) {
                    Log.d(TAG, "🌐 Will use WebView fallback since PhonePe app not installed")
                    Log.d(TAG, "💡 WebView may show JavaScript errors - these are cosmetic and don't affect payment")
                }
                
            } catch (ex: Exception) {
                Log.e(TAG, "❌ PhonePe SDK failed, trying manual WebView fallback: ${ex.message}")
                
                // ✅ Manual WebView fallback for edge cases
                startWebViewFallback(activity, token, orderId)
            }
            
        } catch (ex: PhonePeInitException) {
            Log.e(TAG, "❌ PhonePe initialization error: ${ex.message}", ex)
            Log.e(TAG, "SDK Environment: ${PhonePeConfig.getEnvironmentName()}")
            Log.e(TAG, "Merchant ID: ${PhonePeConfig.MERCHANT_ID}")
            Log.e(TAG, "Client ID: ${PhonePeConfig.CLIENT_ID}")
            Log.e(TAG, "💡 Solution: Restart the app to reinitialize PhonePe SDK")
            
            // Try to reinitialize SDK once
            if (reinitializeSDK(activity)) {
                Log.d(TAG, "🔄 SDK reinitialized, retrying payment...")
                startPhonePeCheckout(activity, token, orderId, activityResultLauncher)
            } else {
                handlePaymentError(ex, "PhonePe SDK not initialized properly. Please restart the app.")
            }
        } catch (ex: IllegalArgumentException) {
            Log.e(TAG, "❌ Invalid payment parameters: ${ex.message}", ex)
            Log.e(TAG, "💡 Solution: Check backend token format - should be PhonePe token, not JWT")
            handlePaymentError(ex, "Invalid payment data: ${ex.message}")
        } catch (ex: Exception) {
            Log.e(TAG, "❌ Error starting PhonePe checkout: ${ex.message}", ex)
            Log.e(TAG, "💡 Check token format and environment configuration")
            handlePaymentError(ex, "Transaction could not be started: ${ex.message}")
        }
    }
    
    /**
     * Enhanced PhonePe app installation check with multiple methods
     */
    private fun checkPhonePeAppInstallation(activity: Activity): Boolean {
        val packageManager = activity.packageManager
        
        return try {
            // Method 1: Check package info
            packageManager.getPackageInfo("com.phonepe.app", 0)
            Log.d(TAG, "✅ PhonePe app detected via package info")
            true
        } catch (e: PackageManager.NameNotFoundException) {
            try {
                // Method 2: Check if PhonePe intent can be resolved
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("phonepe://"))
                val resolveInfo = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
                if (resolveInfo != null) {
                    Log.d(TAG, "✅ PhonePe app detected via intent resolution")
                    true
                } else {
                    Log.d(TAG, "❌ PhonePe app not found - will use WebView")
                    false
                }
            } catch (e2: Exception) {
                Log.d(TAG, "❌ PhonePe app not installed: ${e2.message}")
                false
            }
        } catch (e: Exception) {
            Log.w(TAG, "⚠️ Error checking PhonePe installation: ${e.message}")
            false
        }
    }
    
    /**
     * Manual WebView fallback when SDK fails
     */
    private fun startWebViewFallback(activity: Activity, token: String, orderId: String) {
        try {
            Log.d(TAG, "🌐 Starting manual WebView fallback")
            
            // Create PhonePe payment URL
            val paymentUrl = if (token.startsWith("http")) {
                token
            } else {
                // Construct URL from token
                val baseUrl = if (PhonePeConfig.IS_PRODUCTION) {
                    "https://mercury.phonepe.com/transact/pg"
                } else {
                    "https://mercury-t2.phonepe.com/transact/pg"
                }
                "$baseUrl?token=$token"
            }
            
            Log.d(TAG, "🌐 WebView URL: ${paymentUrl.take(100)}...")
            
            // Open in WebView or browser
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(paymentUrl))
            activity.startActivity(intent)
            
            Log.d(TAG, "✅ WebView fallback initiated")
            Log.d(TAG, "💡 User will complete payment in browser/WebView")
            Log.d(TAG, "💡 Use manual verification after payment completion")
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ WebView fallback failed: ${e.message}")
            handlePaymentError(e, "Payment could not be started: ${e.message}")
        }
    }
    
    /**
     * Attempt to reinitialize PhonePe SDK
     */
    private fun reinitializeSDK(activity: Activity): Boolean {
        return try {
            Log.d(TAG, "🔄 Attempting to reinitialize PhonePe SDK...")
            
            val environment = if (PhonePeConfig.IS_PRODUCTION) {
                PhonePeEnvironment.RELEASE
            } else {
                PhonePeEnvironment.SANDBOX
            }
            
            val result = PhonePeKt.init(
                context = activity.applicationContext,
                merchantId = PhonePeConfig.MERCHANT_ID,
                flowId = PhonePeConfig.generateFlowId(),
                phonePeEnvironment = environment,
                enableLogging = !PhonePeConfig.IS_PRODUCTION,
                appId = null
            )
            
            if (result) {
                Log.d(TAG, "✅ PhonePe SDK reinitialized successfully")
                true
            } else {
                Log.e(TAG, "❌ PhonePe SDK reinitialization failed")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error reinitializing SDK: ${e.message}")
            false
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
        Log.d(TAG, "🔍 Handling payment result with code: $resultCode")
        
        // ✅ Enhanced logging for debugging
        data?.let { intent ->
            Log.d(TAG, "📋 Payment result data extras:")
            intent.extras?.let { bundle ->
                for (key in bundle.keySet()) {
                    val value = bundle.get(key)
                    Log.d(TAG, "  $key: $value")
                }
            } ?: Log.d(TAG, "  No extras in result data")
        } ?: Log.d(TAG, "No result data received")
        
        val callback = onPaymentResult ?: currentPaymentCallback
        
        // ✅ CRITICAL FIX: Always verify payment status regardless of result code
        // WebView JavaScript errors can cause wrong result codes even when payment succeeds
        Log.d(TAG, "🚨 WEBVIEW BYPASS: Always verifying payment status due to known WebView issues")
        Log.d(TAG, "💡 JavaScript errors in PhonePe WebView don't indicate payment failure")
        
        currentMerchantOrderId?.let { merchantOrderId ->
            Log.d(TAG, "🔍 Starting payment verification for order: $merchantOrderId")
            Log.d(TAG, "📱 Result code was: $resultCode (ignoring due to WebView issues)")
            
            CoroutineScope(Dispatchers.IO).launch {
                // ✅ Immediate verification + retry mechanism
                Log.d(TAG, "⚡ Immediate verification attempt...")
                verifyPaymentStatusWithRetry(merchantOrderId, currentAuthToken, callback, maxRetries = 3)
            }
        } ?: run {
            Log.e(TAG, "❌ No merchant order ID found for verification")
            CoroutineScope(Dispatchers.Main).launch {
                callback?.invoke(PaymentResult.Error("Payment verification failed: No order ID"))
            }
        }
    }
    
    /**
     * Verify payment status with retry mechanism for WebView issues
     */
    private suspend fun verifyPaymentStatusWithRetry(
        merchantOrderId: String,
        authToken: String?,
        callback: ((PaymentResult) -> Unit)?,
        maxRetries: Int = 3,
        currentAttempt: Int = 1
    ) {
        Log.d(TAG, "🔄 Payment verification attempt $currentAttempt/$maxRetries")
        
        try {
            val result = paymentRepository.verifyPaymentStatus(merchantOrderId, authToken)
            
            result.fold(
                onSuccess = { paymentStatusData ->
                    Log.d(TAG, "✅ Payment verification successful (attempt $currentAttempt)")
                    Log.d(TAG, "📊 Raw Status from backend: '${paymentStatusData.paymentStatus}'")
                    Log.d(TAG, "📊 Status uppercase: '${paymentStatusData.paymentStatus.uppercase()}'")
                    Log.d(TAG, "📊 PassId: ${paymentStatusData.passId}")
                    Log.d(TAG, "📊 PassUUID: ${paymentStatusData.passUUID}")
                    
                    withContext(Dispatchers.Main) {
                        when (paymentStatusData.paymentStatus.uppercase()) {
                            "COMPLETED" -> {
                                Log.d(TAG, "🎉 Payment COMPLETED - WebView issues bypassed!")
                                callback?.invoke(
                                    PaymentResult.Success(
                                        message = "Payment completed successfully",
                                        passId = paymentStatusData.passId,
                                        passUUID = paymentStatusData.passUUID
                                    )
                                )
                                clearCurrentPayment()
                                return@withContext
                            }
                            "FAILED" -> {
                                Log.d(TAG, "❌ Payment FAILED")
                                callback?.invoke(PaymentResult.Failed("Payment failed"))
                                clearCurrentPayment()
                                return@withContext
                            }
                            "PENDING" -> {
                                Log.d(TAG, "⏳ Payment PENDING - will retry...")
                                if (currentAttempt < maxRetries) {
                                    // Retry after delay for pending payments
                                    delay(5000) // Wait 5 seconds
                                    verifyPaymentStatusWithRetry(merchantOrderId, authToken, callback, maxRetries, currentAttempt + 1)
                                } else {
                                    callback?.invoke(PaymentResult.Pending("Payment is still pending after $maxRetries attempts"))
                                    clearCurrentPayment()
                                }
                                return@withContext
                            }
                            else -> {
                                Log.w(TAG, "🤔 Unknown status: ${paymentStatusData.paymentStatus}")
                                if (currentAttempt < maxRetries) {
                                    delay(3000)
                                    verifyPaymentStatusWithRetry(merchantOrderId, authToken, callback, maxRetries, currentAttempt + 1)
                                } else {
                                    callback?.invoke(PaymentResult.Error("Unknown payment status: ${paymentStatusData.paymentStatus}"))
                                    clearCurrentPayment()
                                }
                            }
                        }
                    }
                },
                onFailure = { exception ->
                    Log.e(TAG, "❌ Payment verification failed (attempt $currentAttempt): ${exception.message}")
                    
                    if (currentAttempt < maxRetries) {
                        Log.d(TAG, "🔄 Retrying verification in 3 seconds...")
                        delay(3000)
                        verifyPaymentStatusWithRetry(merchantOrderId, authToken, callback, maxRetries, currentAttempt + 1)
                    } else {
                        Log.e(TAG, "💥 All verification attempts failed")
                        withContext(Dispatchers.Main) {
                            callback?.invoke(PaymentResult.Error("Payment verification failed after $maxRetries attempts: ${exception.message}"))
                        }
                        clearCurrentPayment()
                    }
                }
            )
        } catch (e: Exception) {
            Log.e(TAG, "💥 Unexpected error during verification (attempt $currentAttempt): ${e.message}")
            
            if (currentAttempt < maxRetries) {
                delay(3000)
                verifyPaymentStatusWithRetry(merchantOrderId, authToken, callback, maxRetries, currentAttempt + 1)
            } else {
                withContext(Dispatchers.Main) {
                    callback?.invoke(PaymentResult.Error("Verification error after $maxRetries attempts: ${e.message}"))
                }
                clearCurrentPayment()
            }
        }
    }
    
    /**
     * Verify payment status on server (as per backend developer's specification)
     * GET /api/payment/app-status-check/:merchantOrderId
     */
    fun verifyPaymentStatusOnServer(
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
     * Debug token format and provide recommendations
     */
    private fun debugTokenFormat(token: String) {
        Log.d(TAG, "🔍 Token Analysis:")
        Log.d(TAG, "  Length: ${token.length}")
        Log.d(TAG, "  Preview: ${token.take(50)}...")
        
        when {
            token.startsWith("eyJ") -> {
                Log.e(TAG, "❌ JWT Token Detected!")
                Log.e(TAG, "💡 Backend should return PhonePe token from Create Order API")
                Log.e(TAG, "💡 Check: response.data.instrumentResponse.redirectInfo.url")
            }
            token.startsWith("http") -> {
                Log.w(TAG, "🌐 URL Format Detected")
                Log.w(TAG, "💡 Extracting token parameter for mobile SDK")
            }
            token.length > 400 -> {
                Log.d(TAG, "✅ Looks like proper PhonePe token format")
            }
            token.length < 200 -> {
                Log.w(TAG, "⚠️ Token seems short for PhonePe format")
                Log.w(TAG, "💡 Verify backend is returning correct PhonePe token")
            }
            else -> {
                Log.d(TAG, "🤔 Unknown token format - proceeding with caution")
            }
        }
        
        // Additional validation
        if (token.contains(" ")) {
            Log.w(TAG, "⚠️ Token contains spaces - might be malformed")
        }
        if (token.contains("\n") || token.contains("\r")) {
            Log.w(TAG, "⚠️ Token contains line breaks - might be malformed")
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
            val passUUID: String? = null  // Made optional to match backend response
        ) : PaymentResult()
        
        data class Failed(val message: String) : PaymentResult()
        data class Pending(val message: String) : PaymentResult()
        data class Cancelled(val message: String) : PaymentResult()
        data class Error(val message: String) : PaymentResult()
        
        // Legacy support
        data class Completed(val message: String) : PaymentResult()
    }
    
    /**
     * Manual payment verification for debugging WebView issues
     * Call this method when WebView shows "Something went wrong"
     */
    fun manualPaymentVerification(
        merchantOrderId: String,
        authToken: String? = null,
        onResult: (PaymentResult) -> Unit
    ) {
        Log.d(TAG, "🔍 Manual payment verification requested for: $merchantOrderId")
        Log.d(TAG, "💡 This bypasses WebView issues and checks actual payment status")
        
        verifyPaymentStatusOnServer(merchantOrderId, authToken) { result ->
            Log.d(TAG, "📋 Manual verification result: $result")
            onResult(result)
        }
    }
    
    /**
     * Get current payment details for debugging
     */
    fun getCurrentPaymentInfo(): String {
        return """
            Current Payment Info:
            - Merchant Order ID: ${currentMerchantOrderId ?: "None"}
            - Has Callback: ${currentPaymentCallback != null}
            - Has Auth Token: ${currentAuthToken != null}
            - SDK Environment: ${PhonePeConfig.getEnvironmentName()}
            - Merchant ID: ${PhonePeConfig.MERCHANT_ID}
        """.trimIndent()
    }
    
    /**
     * Handle WebView JavaScript errors - bypass and verify payment
     * Call this when you see JavaScript errors in logs but want to check payment status
     */
    fun handleWebViewError(onResult: (PaymentResult) -> Unit) {
        Log.d(TAG, "🚨 WebView error handler called")
        Log.d(TAG, "💡 This bypasses WebView JavaScript issues and checks actual payment status")
        
        currentMerchantOrderId?.let { merchantOrderId ->
            Log.d(TAG, "🔍 Checking payment status for order: $merchantOrderId")
            
            CoroutineScope(Dispatchers.IO).launch {
                verifyPaymentStatusWithRetry(merchantOrderId, currentAuthToken, { result ->
                    Log.d(TAG, "📋 WebView error bypass result: $result")
                    onResult(result)
                }, maxRetries = 2)
            }
        } ?: run {
            Log.e(TAG, "❌ No current payment to verify")
            onResult(PaymentResult.Error("No active payment to verify"))
        }
    }
    
    /**
     * Force payment verification - use when WebView shows errors
     */
    fun forcePaymentVerification(): String {
        return currentMerchantOrderId?.let { orderId ->
            Log.d(TAG, "🔍 Force verification requested for: $orderId")
            
            CoroutineScope(Dispatchers.IO).launch {
                verifyPaymentStatusWithRetry(orderId, currentAuthToken, currentPaymentCallback, maxRetries = 1)
            }
            
            "Verification started for order: $orderId"
        } ?: "No active payment to verify"
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