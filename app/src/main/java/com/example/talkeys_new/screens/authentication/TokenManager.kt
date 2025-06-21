package com.example.talkeys_new.screens.authentication

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

// Creates a DataStore instance tied to the application Context, named "user_prefs".
// This is where we will store lightweight key-value data (like SharedPreferences but modern and safe).
val Context.dataStore by preferencesDataStore(name = "user_prefs")

// TokenManager handles saving, retrieving, and deleting the JWT token using DataStore.
//A JWT (JSON Web Token) is a compact, secure token used to share user authentication data between a client and server.
class TokenManager(private val context: Context) {

    // Companion object holds constants that are shared across all instances of TokenManager.
    companion object {
        // This is the key used to store/retrieve the auth token in DataStore.
        private val TOKEN_KEY = stringPreferencesKey("auth_token")
    }

    // This 'token' property is a Flow, which means it emits data updates over time.
    // It reads the token from DataStore whenever it changes.
    val token: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[TOKEN_KEY] // Try to get the token from preferences; may be null if not saved.
    }

    // This suspend function saves a token into the DataStore.
    // 'suspend' means this must be called from a coroutine.
    suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token // Store the token under the TOKEN_KEY.
        }
    }

    // This suspend function clears the saved token from DataStore.
    suspend fun clearToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(TOKEN_KEY) // Deletes the token from preferences.
        }
    }

    // Use this if you want to directly get the current token (not as a Flow)
    suspend fun getToken(): String? {
        return token.first()
    }

}
