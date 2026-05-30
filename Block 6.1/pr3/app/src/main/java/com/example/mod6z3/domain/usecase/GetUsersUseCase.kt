package com.example.mod6z3.domain.usecase

import com.example.mod6z3.domain.model.User
import com.example.mod6z3.domain.repository.UserRepository

class GetUsersUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(): Result<List<User>> {
        return repository.getUsers()
    }
}