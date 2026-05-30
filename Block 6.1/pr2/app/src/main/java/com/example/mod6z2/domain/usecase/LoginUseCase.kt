package com.example.mod6z2.domain.usecase

import com.example.mod6z2.domain.repository.NobelRepository

class LoginUseCase(
    private val repository: NobelRepository
) {
    suspend operator fun invoke(username: String, password: String): Result<String> {
        return repository.login(username, password)
    }
}