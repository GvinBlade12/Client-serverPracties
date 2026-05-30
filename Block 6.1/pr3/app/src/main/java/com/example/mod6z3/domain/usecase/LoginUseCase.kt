package com.example.mod6z3.domain.usecase

import com.example.mod6z3.domain.model.LoginData
import com.example.mod6z3.domain.repository.AuthRepository

class LoginUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(username: String, password: String): Result<LoginData> {
        return repository.login(username, password)
    }
}