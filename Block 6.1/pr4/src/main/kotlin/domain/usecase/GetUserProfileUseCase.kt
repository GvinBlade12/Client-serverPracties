package com.example.nobelapi.domain.usecase

import com.example.nobelapi.domain.model.User
import com.example.nobelapi.domain.repository.UserRepository

class GetUserProfileUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: Int): User? {
        return userRepository.findById(userId)
    }
}