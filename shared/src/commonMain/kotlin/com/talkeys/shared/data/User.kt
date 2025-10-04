package com.talkeys.shared.data

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val name: String,
    val email: String,
    val profilePicture: String? = null,
    val createdAt: String
)
