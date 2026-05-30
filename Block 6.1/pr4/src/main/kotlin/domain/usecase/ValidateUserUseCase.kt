package com.example.nobelapi.domain.usecase

import com.example.nobelapi.domain.repository.UserRepository

class ValidateUserUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(username: String, password: String): Boolean {
        val user = userRepository.findByUsername(username)
        return user != null && user.passwordHash == password
    }
}