package com.example.talkeys_new.utils

/**
 * 🔒 SECURITY REMINDER
 * 
 * Your PhonePe credentials are now configured:
 * - Client Id: SU2504181253408025787154 ✅
 * - Client Secret: 4d9bafd5-1172-4695-a212-beb19efd70ed ⚠️
 * 
 * CRITICAL SECURITY NOTES:
 * 
 * 1. CLIENT SECRET SECURITY:
 *    ❌ NEVER use client secret in mobile app
 *    ❌ NEVER commit to public repositories  
 *    ❌ NEVER expose in client-side code
 *    ✅ ONLY use on secure backend server
 * 
 * 2. CURRENT STATUS:
 *    ✅ Client Id in app (safe)
 *    ⚠️ Client Secret in app (for reference only)
 *    ⚠️ Currently using SANDBOX environment
 * 
 * 3. BEFORE PRODUCTION:
 *    - Move client secret to backend only
 *    - Set PhonePeConfig.IS_PRODUCTION = true
 *    - Remove secret from mobile app code
 *    - Use environment variables on server
 * 
 * 4. TESTING STATUS:
 *    ✅ SDK will now initialize with real credentials
 *    ✅ Can test payment flow in SANDBOX
 *    ❌ Still need backend for real payments
 */

object SecurityReminder {
    const val REMINDER = "Move client secret to backend before production!"
}