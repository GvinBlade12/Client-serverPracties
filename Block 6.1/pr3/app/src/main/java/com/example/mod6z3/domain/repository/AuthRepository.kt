package com.example.mod6z3.domain.repository

import com.example.mod6z3.domain.model.LoginData

interface AuthRepository {
    suspend fun login(username: String, password: String): Result<LoginData>
    suspend fun logout()
    suspend fun isLoggedIn(): Boolean
}