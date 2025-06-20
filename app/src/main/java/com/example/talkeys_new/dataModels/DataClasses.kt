package com.example.talkeys_new.dataModels

data class UserResponse(
    val accessToken: String,
    val name: String
)
data class VerifyRequest(
    val token: String
)

data class VerifyResponse(
    val accessToken: String,
    val name: String
)
