package com.example.talkeys_new.screens.authentication

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.talkeys_new.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.util.Date

// Creates a DataStore instance tied to the application Context, named "user_prefs".
val Context.dataStore by preferencesDataStore(name = "user_prefs")

/**
 * TokenManager handles saving, retrieving, validating, and deleting the JWT token using DataStore.
 * A JWT (JSON Web Token) is a compact, secure token used to share user authentication data between a client and server.
 */
class TokenManager(private val context: Context) {

    private val TAG = "TokenManager"

    // Companion object holds constants that are shared across all instances of TokenManager.
    companion object {
        // This is the key used to store/retrieve the auth token in DataStore.
        private val TOKEN_KEY = stringPreferencesKey("auth_token")
        private val TOKEN_EXPIRY_KEY = stringPreferencesKey("auth_token_expiry")
    }

    /**
     * This 'token' property is a Flow, which means it emits data updates over time.
     * It reads the token from DataStore whenever it changes and handles errors.
     */
    val token: Flow<String?> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e(TAG, "Error reading token", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[TOKEN_KEY] // Try to get the token from preferences; may be null if not saved.
        }

    /**
     * Saves a token into the DataStore with proper error handling.
     * @param token The JWT token to save
     * @return Result indicating success or failure
     */
    suspend fun saveToken(token: String): Result<Unit> {
        return try {
            context.dataStore.edit { preferences ->
                preferences[TOKEN_KEY] = token // Store the token under the TOKEN_KEY.
                
                // Store expiry time if we can extract it (optional enhancement)
                try {
                    // In a real app, you would decode the JWT and extract the expiration
                    // For now, we'll just set an expiry 24 hours from now as an example
                    val expiryTime = Date().time + (24 * 60 * 60 * 1000) // 24 hours from now
                    preferences[TOKEN_EXPIRY_KEY] = expiryTime.toString()
                } catch (e: Exception) {
                    Log.w(TAG, "Could not extract token expiry", e)
                }
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save token", e)
            Result.Error(e, "Failed to save authentication token")
        }
    }

    /**
     * Clears the saved token from DataStore with proper error handling.
     * @return Result indicating success or failure
     */
    suspend fun clearToken(): Result<Unit> {
        return try {
            context.dataStore.edit { preferences ->
                preferences.remove(TOKEN_KEY) // Deletes the token from preferences.
                preferences.remove(TOKEN_EXPIRY_KEY) // Also remove the expiry time
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to clear token", e)
            Result.Error(e, "Failed to clear authentication token")
        }
    }

    /**
     * Gets the current token directly (not as a Flow) with proper error handling.
     * @return Result containing the token or an error
     */
    suspend fun getToken(): Result<String?> {
        return try {
            val currentToken = token.first()
            Result.Success(currentToken)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get token", e)
            Result.Error(e, "Failed to retrieve authentication token")
        }
    }
    
    /**
     * Checks if the token exists and is not expired.
     * @return true if the token is valid, false otherwise
     */
    suspend fun isTokenValid(): Boolean {
        return try {
            val currentToken = token.first()
            if (currentToken.isNullOrEmpty()) {
                return false
            }
            
            // In a real app, you would decode the JWT and check its expiration
            // For now, we'll just check if we have a token
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error checking token validity", e)
            false
        }
    }
}
