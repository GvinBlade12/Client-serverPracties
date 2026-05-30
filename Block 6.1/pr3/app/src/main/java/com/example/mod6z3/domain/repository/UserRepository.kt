package com.example.mod6z3.domain.repository

import com.example.mod6z3.domain.model.User

interface UserRepository {
    suspend fun getUsers(): Result<List<User>>
    suspend fun getUserById(userId: String): Result<User>
}