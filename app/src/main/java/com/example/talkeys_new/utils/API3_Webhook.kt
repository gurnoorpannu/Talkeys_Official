package com.example.talkeys_new.utils

/**
 * 🔔 API #3: WEBHOOK HANDLER (RECOMMENDED)
 * 
 * PhonePe will call this when payment status changes
 * 
 * 📥 PHONEPE WILL SEND:
 * POST https://yourapi.com/webhook/phonepe
 * Headers:
 * - Content-Type: application/json
 * - X-VERIFY: checksum_to_verify
 * 
 * Body:
 * {
 *   "response": "base64_encoded_response"
 * }
 * 
 * 🔧 WHAT YOU NEED TO DO:
 * 
 * 1. Verify the checksum (security)
 * 2. Decode the base64 response
 * 3. Update payment status in database
 * 4. Send confirmation email/notification to user
 * 5. Return success response to PhonePe
 * 
 * 📤 DECODED RESPONSE WILL BE:
 * {
 *   "merchantId": "SU2504181253408025787154",
 *   "merchantTransactionId": "TXN_1234567890",
 *   "transactionId": "T2411251200000000000001",
 *   "amount": 50000,
 *   "state": "COMPLETED",
 *   "responseCode": "SUCCESS",
 *   "paymentInstrument": {
 *     "type": "UPI"
 *   }
 * }
 * 
 * 📱 YOU RETURN TO PHONEPE:
 * {
 *   "success": true,
 *   "message": "Webhook processed successfully"
 * }
 * 
 * 🔒 CHECKSUM VERIFICATION:
 * calculated_checksum = SHA256(base64_response + client_secret)
 * received_checksum = X-VERIFY header (remove ###1 suffix)
 * if (calculated_checksum === received_checksum) { process_webhook() }
 */