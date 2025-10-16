package com.example.talkeys_new.utils

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import com.phonepe.intent.sdk.api.PhonePeKt
import com.phonepe.intent.sdk.api.PhonePeInitException

/**
 * PhonePe Payment Manager
 * Handles PhonePe payment integration and checkout flow
 */
object PhonePePaymentManager {
    
    private const val TAG = "PhonePePayment"
    
    /**
     * Initiate PhonePe standard checkout
     * 
     * @param activity Current activity context
     * @param token Payment token received from backend (Create Order API response)
     * @param orderId Order ID received from backend (Create Order API response)
     * @param activityResultLauncher Activity result launcher to handle payment result
     */
    fun startCheckout(
        activity: Activity,
        token: String,
        orderId: String,
        activityResultLauncher: ActivityResultLauncher<Intent>
    ) {
        try {
            Log.d(TAG, "Starting PhonePe checkout for Order ID: $orderId")
            
            PhonePeKt.startCheckoutPage(
                context = activity,
                token = token,
                orderId = orderId,
                activityResultLauncher = activityResultLauncher
            )
            
            Log.d(TAG, "PhonePe checkout initiated successfully")
            
        } catch (ex: PhonePeInitException) {
            Log.e(TAG, "PhonePe initialization error: ${ex.message}", ex)
            handlePaymentError(ex, "PhonePe SDK not initialized properly")
        } catch (ex: Exception) {
            Log.e(TAG, "Error starting PhonePe checkout: ${ex.message}", ex)
            handlePaymentError(ex, "Transaction could not be started")
        }
    }
    
    /**
     * Handle payment result from ActivityResultLauncher
     * Call this method in your activity result callback
     * 
     * @param resultCode Result code from the payment activity
     * @param data Intent data from the payment activity
     * @param onPaymentResult Callback to handle the payment result
     */
    fun handlePaymentResult(
        resultCode: Int,
        data: Intent?,
        onPaymentResult: (PaymentResult) -> Unit
    ) {
        Log.d(TAG, "Handling payment result with code: $resultCode")
        
        when (resultCode) {
            Activity.RESULT_OK -> {
                Log.d(TAG, "Payment activity completed successfully")
                // Note: This doesn't mean payment was successful
                // Always call Order Status API to get the actual payment status
                onPaymentResult(PaymentResult.Completed("Payment flow completed. Check order status."))
            }
            Activity.RESULT_CANCELED -> {
                Log.d(TAG, "Payment was cancelled by user")
                onPaymentResult(PaymentResult.Cancelled("Payment cancelled by user"))
            }
            else -> {
                Log.w(TAG, "Unknown payment result code: $resultCode")
                onPaymentResult(PaymentResult.Error("Unknown payment result"))
            }
        }
    }
    
    /**
     * Handle payment errors
     */
    private fun handlePaymentError(exception: Exception, message: String) {
        Log.e(TAG, "Payment Error: $message", exception)
        
        // You can add specific error handling logic here
        // For example, show error dialog, retry logic, etc.
        
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
        data class Completed(val message: String) : PaymentResult()
        data class Cancelled(val message: String) : PaymentResult()
        data class Error(val message: String) : PaymentResult()
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