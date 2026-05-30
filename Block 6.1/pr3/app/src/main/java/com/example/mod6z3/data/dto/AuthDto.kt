package com.example.mod6z3.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val username: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val id: Int,
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val gender: String,
    val image: String,
    val accessToken: String,
    val refreshToken: String
)

@Serializable
data class UsersResponse(
    val users: List<UserDto>,
    val total: Int,
    val skip: Int,
    val limit: Int
)

@Serializable
data class UserDto(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val username: String,
    val email: String,
    val image: String,
    val age: Int? = null,
    val phone: String? = null,
    val birthDate: String? = null,
    val address: AddressDto? = null
)

@Serializable
data class AddressDto(
    val address: String,
    val city: String,
    val state: String,
    val postalCode: String
)