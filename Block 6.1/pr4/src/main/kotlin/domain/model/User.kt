package com.example.nobelapi.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    val username: String,
    val passwordHash: String,
    val role: String
)