package com.example.talkeys_new.utils

/**
 * 🔥 API #1: CREATE ORDER (MOST IMPORTANT)
 * 
 * This is what mobile app calls BEFORE starting payment
 * 
 * 📱 MOBILE APP WILL SEND:
 * POST /api/payments/create-order
 * {
 *   "eventId": "64f8a1b2c3d4e5f6789012ab",
 *   "userId": "user123",
 *   "amount": 500,
 *   "currency": "INR",
 *   "eventName": "Tech Conference 2024"
 * }
 * 
 * 🔧 WHAT YOU NEED TO DO:
 * 
 * 1. Validate the request (user exists, event exists, amount correct)
 * 2. Generate unique merchantTransactionId (e.g., "TXN_" + timestamp)
 * 3. Call PhonePe Create Order API
 * 4. Return token and orderId to mobile app
 * 
 * 📤 PHONEPE API CALL YOU MAKE:
 * POST https://api-preprod.phonepe.com/apis/pg-sandbox/pg/v1/pay
 * Headers:
 * - Content-Type: application/json
 * - X-VERIFY: SHA256(base64_payload + "/pg/v1/pay" + client_secret) + "###" + "1"
 * 
 * Body (base64 encoded):
 * {
 *   "merchantId": "SU2504181253408025787154",
 *   "merchantTransactionId": "TXN_1234567890",
 *   "merchantUserId": "user123",
 *   "amount": 50000, // Amount in paise (₹500 = 50000 paise)
 *   "redirectUrl": "https://yourapp.com/payment/callback",
 *   "redirectMode": "POST",
 *   "callbackUrl": "https://yourapi.com/webhook/phonepe",
 *   "paymentInstrument": {
 *     "type": "PAY_PAGE"
 *   }
 * }
 * 
 * 📥 PHONEPE WILL RETURN:
 * {
 *   "success": true,
 *   "code": "PAYMENT_INITIATED",
 *   "message": "Payment initiated",
 *   "data": {
 *     "merchantId": "SU2504181253408025787154",
 *     "merchantTransactionId": "TXN_1234567890",
 *     "instrumentResponse": {
 *       "type": "PAY_PAGE",
 *       "redirectInfo": {
 *         "url": "https://mercury-t2.phonepe.com/transact/...",
 *         "method": "GET"
 *       }
 *     }
 *   }
 * }
 * 
 * 📱 YOU RETURN TO MOBILE APP:
 * {
 *   "success": true,
 *   "token": "extracted_from_phonepe_url",
 *   "orderId": "TXN_1234567890",
 *   "message": "Order created successfully"
 * }
 */