package com.example.talkeys_new.security

import android.content.Context
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.talkeys_new.utils.Result

/**
 * SecureStorage provides encrypted storage for sensitive data using Android Keystore.
 * All data is encrypted at rest using AES-256-GCM encryption.
 * 
 * This class wraps EncryptedSharedPreferences and provides a simple API for
 * storing and retrieving encrypted key-value pairs.
 */
class SecureStorage(context: Context) {

    private val TAG = "SecureStorage"
    
    // Master key is generated or retrieved from Android Keystore
    // Uses AES256_GCM encryption scheme for maximum security
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    // EncryptedSharedPreferences encrypts both keys and values
    private val sharedPreferences = try {
        EncryptedSharedPreferences.create(
            context,
            "secure_prefs", // File name for encrypted preferences
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    } catch (e: Exception) {
        Log.e(TAG, "Failed to create EncryptedSharedPreferences", e)
        throw SecurityException("Failed to initialize secure storage", e)
    }

    /**
     * Saves a string value securely.
     * Both the key and value are encrypted before storage.
     * 
     * @param key The key to store the value under
     * @param value The value to store (will be encrypted)
     * @return Result indicating success or failure
     */
    fun saveString(key: String, value: String): Result<Unit> {
        return try {
            sharedPreferences.edit().putString(key, value).apply()
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save encrypted data for key: $key", e)
            Result.Error(e, "Failed to save encrypted data")
        }
    }

    /**
     * Retrieves a string value securely.
     * The value is decrypted before being returned.
     * 
     * @param key The key to retrieve the value for
     * @param defaultValue The default value if key doesn't exist
     * @return Result containing the decrypted value or error
     */
    fun getString(key: String, defaultValue: String? = null): Result<String?> {
        return try {
            val value = sharedPreferences.getString(key, defaultValue)
            Result.Success(value)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to retrieve encrypted data for key: $key", e)
            Result.Error(e, "Failed to retrieve encrypted data")
        }
    }

    /**
     * Removes a specific key-value pair from secure storage.
     * 
     * @param key The key to remove
     * @return Result indicating success or failure
     */
    fun remove(key: String): Result<Unit> {
        return try {
            sharedPreferences.edit().remove(key).apply()
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to remove encrypted data for key: $key", e)
            Result.Error(e, "Failed to remove encrypted data")
        }
    }

    /**
     * Clears all data from secure storage.
     * Use with caution - this will remove all encrypted data.
     * 
     * @return Result indicating success or failure
     */
    fun clear(): Result<Unit> {
        return try {
            sharedPreferences.edit().clear().apply()
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to clear encrypted storage", e)
            Result.Error(e, "Failed to clear encrypted storage")
        }
    }
}
