package com.example.talkeys_new.utils

/**
 * 🔍 API #2: ORDER STATUS (CRITICAL FOR VERIFICATION)
 * 
 * This is called AFTER payment to verify if it was successful
 * 
 * 📱 MOBILE APP WILL SEND:
 * GET /api/payments/status/{orderId}
 * 
 * 🔧 WHAT YOU NEED TO DO:
 * 
 * 1. Take orderId from mobile app
 * 2. Call PhonePe Order Status API
 * 3. Update payment status in your database
 * 4. Return final status to mobile app
 * 
 * 📤 PHONEPE API CALL YOU MAKE:
 * GET https://api-preprod.phonepe.com/apis/pg-sandbox/pg/v1/status/{merchantId}/{orderId}
 * Headers:
 * - Content-Type: application/json
 * - X-VERIFY: SHA256("/pg/v1/status/{merchantId}/{orderId}" + client_secret) + "###" + "1"
 * 
 * 📥 PHONEPE WILL RETURN:
 * {
 *   "success": true,
 *   "code": "PAYMENT_SUCCESS",
 *   "message": "Your payment is successful.",
 *   "data": {
 *     "merchantId": "SU2504181253408025787154",
 *     "merchantTransactionId": "TXN_1234567890",
 *     "transactionId": "T2411251200000000000001",
 *     "amount": 50000,
 *     "state": "COMPLETED",
 *     "responseCode": "SUCCESS",
 *     "paymentInstrument": {
 *       "type": "UPI",
 *       "utr": "123456789012"
 *     }
 *   }
 * }
 * 
 * 📱 YOU RETURN TO MOBILE APP:
 * {
 *   "success": true,
 *   "paymentStatus": "SUCCESS", // SUCCESS, FAILED, PENDING
 *   "transactionId": "T2411251200000000000001",
 *   "amount": 500,
 *   "message": "Payment successful",
 *   "registrationStatus": "CONFIRMED" // Update user's event registration
 * }
 * 
 * 🚨 IMPORTANT PAYMENT STATES:
 * - COMPLETED + SUCCESS = Payment successful ✅
 * - FAILED = Payment failed ❌
 * - PENDING = Payment still processing ⏳
 */