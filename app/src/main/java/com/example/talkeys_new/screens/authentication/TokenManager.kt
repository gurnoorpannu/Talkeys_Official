package com.example.talkeys_new.screens.authentication

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.preferencesDataStore
import com.example.talkeys_new.security.SecureStorage
import com.example.talkeys_new.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

// DataStore for non-sensitive user preferences (used by GoogleSignInManager)
val Context.dataStore by preferencesDataStore(name = "user_prefs")

/**
 * TokenManager handles saving, retrieving, validating, and deleting the JWT token using SecureStorage.
 * A JWT (JSON Web Token) is a compact, secure token used to share user authentication data between a client and server.
 * 
 * Security features:
 * - Tokens are encrypted at rest using AES-256-GCM via Android Keystore
 * - Token expiry validation (24 hours)
 * - No plaintext storage
 * - No token values logged
 */
class TokenManager(private val context: Context) {

    private val TAG = "TokenManager"
    
    // SecureStorage instance for encrypted token persistence
    private val secureStorage = SecureStorage(context)

    // Companion object holds constants that are shared across all instances of TokenManager.
    companion object {
        // Keys for encrypted storage
        private const val TOKEN_KEY = "auth_token"
        private const val TOKEN_EXPIRY_KEY = "auth_token_expiry"
        
        // Token validity period: 24 hours in milliseconds
        private const val TOKEN_VALIDITY_PERIOD = 24 * 60 * 60 * 1000L
    }

    // MutableStateFlow to emit token updates
    private val _tokenFlow = MutableStateFlow<String?>(null)
    
    /**
     * This 'token' property is a Flow, which means it emits data updates over time.
     * It provides the current token value to observers.
     */
    val token: Flow<String?> = _tokenFlow.asStateFlow()

    init {
        // Load token on initialization
        loadTokenFromStorage()
    }

    /**
     * Loads the token from secure storage into the flow.
     * Called during initialization and after token updates.
     */
    private fun loadTokenFromStorage() {
        try {
            when (val result = secureStorage.getString(TOKEN_KEY)) {
                is Result.Success -> {
                    _tokenFlow.value = result.data
                }
                is Result.Error -> {
                    Log.e(TAG, "Failed to load token from storage", result.exception)
                    _tokenFlow.value = null
                }
                else -> {
                    _tokenFlow.value = null
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading token", e)
            _tokenFlow.value = null
        }
    }

    /**
     * Saves a token into the SecureStorage with proper error handling.
     * Token is encrypted before storage. Expiry time is set to 24 hours from now.
     * 
     * @param token The JWT token to save (NEVER logged)
     * @return Result indicating success or failure
     */
    suspend fun saveToken(token: String): Result<Unit> {
        return try {
            // Save the encrypted token
            when (val tokenResult = secureStorage.saveString(TOKEN_KEY, token)) {
                is Result.Success -> {
                    // Calculate and save expiry time (24 hours from now)
                    val expiryTime = System.currentTimeMillis() + TOKEN_VALIDITY_PERIOD
                    
                    when (val expiryResult = secureStorage.saveString(TOKEN_EXPIRY_KEY, expiryTime.toString())) {
                        is Result.Success -> {
                            // Update the flow with new token
                            _tokenFlow.value = token
                            Log.d(TAG, "Token saved successfully with expiry")
                            Result.Success(Unit)
                        }
                        is Result.Error -> {
                            Log.e(TAG, "Failed to save token expiry", expiryResult.exception)
                            Result.Error(expiryResult.exception, "Failed to save token expiry")
                        }
                        else -> {
                            Log.e(TAG, "Unexpected result saving token expiry")
                            Result.Error(Exception("Unexpected result"), "Failed to save token expiry")
                        }
                    }
                }
                is Result.Error -> {
                    Log.e(TAG, "Failed to save token", tokenResult.exception)
                    Result.Error(tokenResult.exception, "Failed to save authentication token")
                }
                else -> {
                    Log.e(TAG, "Unexpected result saving token")
                    Result.Error(Exception("Unexpected result"), "Failed to save authentication token")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error saving token", e)
            Result.Error(e, "Failed to save authentication token")
        }
    }

    /**
     * Clears the saved token from SecureStorage with proper error handling.
     * @return Result indicating success or failure
     */
    suspend fun clearToken(): Result<Unit> {
        return try {
            // Remove token
            val tokenResult = secureStorage.remove(TOKEN_KEY)
            // Remove expiry
            val expiryResult = secureStorage.remove(TOKEN_EXPIRY_KEY)
            
            // Update flow
            _tokenFlow.value = null
            
            when {
                tokenResult is Result.Error -> {
                    Log.e(TAG, "Failed to clear token", tokenResult.exception)
                    Result.Error(tokenResult.exception, "Failed to clear authentication token")
                }
                expiryResult is Result.Error -> {
                    Log.e(TAG, "Failed to clear token expiry", expiryResult.exception)
                    Result.Error(expiryResult.exception, "Failed to clear token expiry")
                }
                else -> {
                    Log.d(TAG, "Token cleared successfully")
                    Result.Success(Unit)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error clearing token", e)
            Result.Error(e, "Failed to clear authentication token")
        }
    }

    /**
     * Gets the current token directly (not as a Flow) with proper error handling.
     * @return Result containing the token or an error
     */
    suspend fun getToken(): Result<String?> {
        return try {
            when (val result = secureStorage.getString(TOKEN_KEY)) {
                is Result.Success -> {
                    Result.Success(result.data)
                }
                is Result.Error -> {
                    Log.e(TAG, "Failed to retrieve token", result.exception)
                    Result.Error(result.exception, "Failed to retrieve authentication token")
                }
                else -> {
                    Result.Error(Exception("Unexpected result"), "Failed to retrieve authentication token")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error retrieving token", e)
            Result.Error(e, "Failed to retrieve authentication token")
        }
    }
    
    /**
     * Checks if the token exists and is not expired.
     * Validates both token presence and expiry timestamp.
     * 
     * @return true if the token is valid and not expired, false otherwise
     */
    suspend fun isTokenValid(): Boolean {
        return try {
            // Check if token exists
            val tokenResult = secureStorage.getString(TOKEN_KEY)
            if (tokenResult !is Result.Success || tokenResult.data.isNullOrEmpty()) {
                Log.d(TAG, "Token validation failed: no token found")
                return false
            }
            
            // Check token expiry
            val expiryResult = secureStorage.getString(TOKEN_EXPIRY_KEY)
            if (expiryResult !is Result.Success || expiryResult.data.isNullOrEmpty()) {
                Log.d(TAG, "Token validation failed: no expiry found")
                return false
            }
            
            // Parse and validate expiry time
            val expiryTime = expiryResult.data.toLongOrNull()
            if (expiryTime == null) {
                Log.e(TAG, "Token validation failed: invalid expiry format")
                return false
            }
            
            val isValid = System.currentTimeMillis() < expiryTime
            if (!isValid) {
                Log.d(TAG, "Token validation failed: token expired")
            }
            
            isValid
        } catch (e: Exception) {
            Log.e(TAG, "Error checking token validity", e)
            false
        }
    }
}
