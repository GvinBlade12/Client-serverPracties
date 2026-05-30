package com.example.nobelapi.data.repository

import com.example.nobelapi.domain.model.User
import com.example.nobelapi.domain.repository.UserRepository

class InMemoryUserRepository : UserRepository {
    private val users = mutableListOf(
        User(1, "admin", "password", "admin")
    )

    override suspend fun findByUsername(username: String): User? {
        return users.find { it.username == username }
    }

    override suspend fun findById(id: Int): User? {
        return users.find { it.id == id }
    }

    override suspend fun createUser(username: String, passwordHash: String, role: String): User {
        val newId = (users.maxOfOrNull { it.id } ?: 0) + 1
        val user = User(newId, username, passwordHash, role)
        users.add(user)
        return user
    }

    override suspend fun updateUserRole(userId: Int, role: String): Boolean {
        val user = users.find { it.id == userId } ?: return false
        users.remove(user)
        users.add(user.copy(role = role))
        return true
    }
}