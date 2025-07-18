# Backend API Specification for PhonePe Integration

## Base URL
```
https://api.talkeys.xyz/
```

## Required API Endpoints

### 1. Create Order API
**Endpoint:** `POST /api/payment/create-order`

**Request Body:**
```json
{
  "eventId": "678b9075f6deb135145b5636",
  "eventName": "Kick-OFF Day-1 2025",
  "amount": 100.0,
  "userId": "user_1752768772758",
  "userEmail": "user@example.com",
  "userName": "Demo User"
}
```

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Order created successfully",
  "data": {
    "orderId": "ORDER_123456789",
    "token": "BASE64_ENCODED_PHONEPE_TOKEN",
    "amount": 100.0,
    "merchantId": "TEST-M22ZDT307F584_25062"
  }
}
```

**Error Response (400/500):**
```json
{
  "success": false,
  "message": "Error message",
  "data": null
}
```

### 2. Order Status API
**Endpoint:** `GET /api/payment/order-status/{orderId}`

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Order status retrieved",
  "data": {
    "orderId": "ORDER_123456789",
    "status": "SUCCESS",
    "amount": 100.0,
    "transactionId": "TXN_987654321",
    "paymentMethod": "PhonePe",
    "paidAt": "2025-07-17T16:13:05Z"
  }
}
```

**Status Values:**
- `PENDING` - Payment initiated but not completed
- `SUCCESS` - Payment completed successfully
- `FAILED` - Payment failed
- `CANCELLED` - Payment cancelled by user

## PhonePe Token Generation

The `token` field in the Create Order API response should be a Base64 encoded JSON payload as per PhonePe's specification:

```javascript
// Example token generation (Node.js/Express)
const crypto = require('crypto');

function generatePhonePeToken(orderData) {
  const payload = {
    merchantId: "TEST-M22ZDT307F584_25062", // Your merchant ID
    merchantTransactionId: orderData.orderId,
    merchantUserId: orderData.userId,
    amount: orderData.amount * 100, // Amount in paise
    redirectUrl: "https://api.talkeys.xyz/payment/callback",
    redirectMode: "POST",
    callbackUrl: "https://api.talkeys.xyz/payment/webhook",
    mobileNumber: "9999999999", // Optional
    paymentInstrument: {
      type: "PAY_PAGE"
    }
  };
  
  const base64Payload = Buffer.from(JSON.stringify(payload)).toString('base64');
  
  // Generate checksum (required by PhonePe)
  const string = base64Payload + '/pg/v1/pay' + 'YOUR_SALT_KEY';
  const sha256 = crypto.createHash('sha256').update(string).digest('hex');
  const checksum = sha256 + '###' + 'YOUR_SALT_INDEX';
  
  return base64Payload;
}
```

## Implementation Notes

1. **Security**: Store PhonePe credentials securely (merchant ID, salt key, salt index)
2. **Validation**: Validate all incoming request data
3. **Database**: Store order details in your database for tracking
4. **Webhooks**: Implement webhook endpoint to receive payment status updates from PhonePe
5. **Logging**: Log all payment transactions for debugging and audit

## Testing

Your Android app will now call these APIs automatically when users click "Register Now" on events. The app will:

1. Call `/api/payment/create-order` with event details
2. Use the returned token to initiate PhonePe payment
3. After payment, call `/api/payment/order-status/{orderId}` to verify payment status

## Current Status

✅ **Android App**: Fully integrated and ready
⏳ **Backend APIs**: Need to be implemented on your server
🔧 **PhonePe Setup**: Configure with your actual PhonePe merchant credentials

Once you implement these APIs on your backend, the payment integration will work seamlessly!