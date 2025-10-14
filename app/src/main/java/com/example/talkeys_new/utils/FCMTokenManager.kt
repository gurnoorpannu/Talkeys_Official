package com.example.talkeys_new.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging

object FCMTokenManager {
    private const val PREFS_NAME = "fcm_prefs"
    private const val TOKEN_KEY = "fcm_token"
    private const val TAG = "FCMTokenManager"

    /**
     * Get the current FCM token from Firebase
     */
    fun getCurrentToken(onTokenReceived: (String?) -> Unit) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                onTokenReceived(null)
                return@addOnCompleteListener
            }

            val token = task.result
            Log.d(TAG, "FCM Token retrieved: $token")
            onTokenReceived(token)
        }
    }

    /**
     * Get the locally stored FCM token
     */
    fun getStoredToken(context: Context): String? {
        val sharedPref = getSharedPreferences(context)
        return sharedPref.getString(TOKEN_KEY, null)
    }

    /**
     * Store FCM token locally
     */
    fun storeToken(context: Context, token: String) {
        val sharedPref = getSharedPreferences(context)
        with(sharedPref.edit()) {
            putString(TOKEN_KEY, token)
            apply()
        }
        Log.d(TAG, "Token stored locally")
    }

    /**
     * Clear stored FCM token
     */
    fun clearToken(context: Context) {
        val sharedPref = getSharedPreferences(context)
        with(sharedPref.edit()) {
            remove(TOKEN_KEY)
            apply()
        }
        Log.d(TAG, "Token cleared")
    }

    /**
     * Check if token exists locally
     */
    fun hasToken(context: Context): Boolean {
        return getStoredToken(context) != null
    }

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
}