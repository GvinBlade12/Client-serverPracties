package com.example.mod6z3.data.repository

import com.example.mod6z3.data.api.AuthApiService
import com.example.mod6z3.data.dto.LoginRequest
import com.example.mod6z3.data.storage.TokenDataStore
import com.example.mod6z3.domain.model.LoginData
import com.example.mod6z3.domain.model.User
import com.example.mod6z3.domain.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class AuthRepositoryImpl(
    private val apiService: AuthApiService,
    private val tokenDataStore: TokenDataStore
) : AuthRepository {

    override suspend fun login(username: String, password: String): Result<LoginData> {
        return withContext(Dispatchers.IO) {
            try {
                val request = LoginRequest(username, password)
                val response = apiService.login(request)
                tokenDataStore.saveToken("Bearer ${response.accessToken}")
                Result.success(
                    LoginData(
                        token = response.accessToken,
                        user = User(
                            id = response.id,
                            firstName = response.firstName,
                            lastName = response.lastName,
                            username = response.username,
                            email = response.email,
                            image = response.image
                        )
                    )
                )
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun logout() {
        withContext(Dispatchers.IO) {
            tokenDataStore.clearToken()
        }
    }

    override suspend fun isLoggedIn(): Boolean {
        return withContext(Dispatchers.IO) {
            val token = tokenDataStore.getToken.first()
            token.isNotEmpty()
        }
    }
}