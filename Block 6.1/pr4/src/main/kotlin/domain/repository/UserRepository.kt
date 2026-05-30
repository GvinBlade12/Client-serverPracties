package com.example.nobelapi.domain.repository

import com.example.nobelapi.domain.model.User

interface UserRepository {
    suspend fun findByUsername(username: String): User?
    suspend fun findById(id: Int): User?
    suspend fun createUser(username: String, passwordHash: String, role: String): User
    suspend fun updateUserRole(userId: Int, role: String): Boolean
}