package com.talkeys.shared.auth

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val name: String,
    val email: String,
    val displayName: String? = null,
    val profilePicture: String? = null,
    val about: String? = null,
    val pronouns: String? = null
)

@Serializable
data class GoogleSignInRequest(
    val idToken: String
)

@Serializable
data class AuthResponse(
    val success: Boolean,
    val message: String,
    val user: User? = null,
    val accessToken: String? = null,
    val name: String? = null
)

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: User, val token: String) : AuthState()
    data class Error(val message: String) : AuthState()
}
