package com.example.mod6z3.domain.usecase

import com.example.mod6z3.domain.model.User
import com.example.mod6z3.domain.repository.UserRepository

class GetUserDetailUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(userId: String): Result<User> {
        return repository.getUserById(userId)
    }
}