package com.talkeys.shared.auth

// Platform-agnostic token storage interface
interface TokenStorage {
    suspend fun saveToken(token: String)
    suspend fun getToken(): String?
    suspend fun clearToken()
    suspend fun hasToken(): Boolean
}

// Expect function to get platform-specific implementation
expect fun createTokenStorage(): TokenStorage
