package com.example.talkeys_new.utils

/**
 * 💻 CODE EXAMPLES FOR BACKEND DEVELOPER
 * 
 * Here are code snippets in different languages:
 * 
 * 🟢 NODE.JS EXAMPLE:
 * 
 * const crypto = require('crypto');
 * const axios = require('axios');
 * 
 * // Create Order API
 * app.post('/api/payments/create-order', async (req, res) => {
 *   const { eventId, userId, amount, eventName } = req.body;
 *   
 *   const merchantTransactionId = `TXN_${Date.now()}`;
 *   const payload = {
 *     merchantId: 'SU2504181253408025787154',
 *     merchantTransactionId,
 *     merchantUserId: userId,
 *     amount: amount * 100, // Convert to paise
 *     redirectUrl: 'https://yourapp.com/payment/callback',
 *     redirectMode: 'POST',
 *     callbackUrl: 'https://yourapi.com/webhook/phonepe',
 *     paymentInstrument: { type: 'PAY_PAGE' }
 *   };
 *   
 *   const base64Payload = Buffer.from(JSON.stringify(payload)).toString('base64');
 *   const checksum = crypto.createHash('sha256')
 *     .update(base64Payload + '/pg/v1/pay' + 'YOUR_CLIENT_SECRET')
 *     .digest('hex') + '###1';
 *   
 *   try {
 *     const response = await axios.post(
 *       'https://api-preprod.phonepe.com/apis/pg-sandbox/pg/v1/pay',
 *       { request: base64Payload },
 *       { headers: { 'Content-Type': 'application/json', 'X-VERIFY': checksum } }
 *     );
 *     
 *     // Extract token from PhonePe response URL
 *     const token = extractTokenFromUrl(response.data.data.instrumentResponse.redirectInfo.url);
 *     
 *     res.json({
 *       success: true,
 *       token: token,
 *       orderId: merchantTransactionId
 *     });
 *   } catch (error) {
 *     res.status(500).json({ success: false, message: error.message });
 *   }
 * });
 * 
 * 🔵 PYTHON EXAMPLE:
 * 
 * import hashlib
 * import base64
 * import requests
 * import json
 * 
 * @app.route('/api/payments/create-order', methods=['POST'])
 * def create_order():
 *     data = request.json
 *     merchant_transaction_id = f"TXN_{int(time.time())}"
 *     
 *     payload = {
 *         "merchantId": "SU2504181253408025787154",
 *         "merchantTransactionId": merchant_transaction_id,
 *         "merchantUserId": data['userId'],
 *         "amount": data['amount'] * 100,
 *         "redirectUrl": "https://yourapp.com/payment/callback",
 *         "redirectMode": "POST",
 *         "callbackUrl": "https://yourapi.com/webhook/phonepe",
 *         "paymentInstrument": {"type": "PAY_PAGE"}
 *     }
 *     
 *     base64_payload = base64.b64encode(json.dumps(payload).encode()).decode()
 *     checksum_string = base64_payload + "/pg/v1/pay" + "YOUR_CLIENT_SECRET"
 *     checksum = hashlib.sha256(checksum_string.encode()).hexdigest() + "###1"
 *     
 *     headers = {
 *         "Content-Type": "application/json",
 *         "X-VERIFY": checksum
 *     }
 *     
 *     response = requests.post(
 *         "https://api-preprod.phonepe.com/apis/pg-sandbox/pg/v1/pay",
 *         json={"request": base64_payload},
 *         headers=headers
 *     )
 *     
 *     # Process response and return token + orderId
 */