package com.example.mod6z3.domain.model

data class User(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val username: String,
    val email: String,
    val image: String,
    val age: Int? = null,
    val phone: String? = null,
    val birthDate: String? = null,
    val address: String? = null
)

data class LoginData(
    val token: String,
    val user: User
)