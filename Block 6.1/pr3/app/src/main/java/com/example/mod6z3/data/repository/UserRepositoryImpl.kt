package com.example.mod6z3.data.repository

import com.example.mod6z3.data.api.AuthApiService
import com.example.mod6z3.data.storage.TokenDataStore
import com.example.mod6z3.domain.model.User
import com.example.mod6z3.domain.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class UserRepositoryImpl(
    private val apiService: AuthApiService,
    private val tokenDataStore: TokenDataStore
) : UserRepository {

    override suspend fun getUsers(): Result<List<User>> {
        return withContext(Dispatchers.IO) {
            try {
                val token = tokenDataStore.getToken.first()
                if (token.isEmpty()) {
                    return@withContext Result.failure(Exception("Токен не найден"))
                }
                val response = apiService.getUsers(token)
                val users = response.users.map { dto ->
                    User(
                        id = dto.id,
                        firstName = dto.firstName,
                        lastName = dto.lastName,
                        username = dto.username,
                        email = dto.email,
                        image = dto.image,
                        age = dto.age,
                        phone = dto.phone,
                        birthDate = dto.birthDate,
                        address = dto.address?.let { "${it.address}, ${it.city}" }
                    )
                }
                Result.success(users)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun getUserById(userId: String): Result<User> {
        return withContext(Dispatchers.IO) {
            try {
                val token = tokenDataStore.getToken.first()
                if (token.isEmpty()) {
                    return@withContext Result.failure(Exception("Токен не найден"))
                }
                val dto = apiService.getUserById(token, userId.toInt())
                Result.success(
                    User(
                        id = dto.id,
                        firstName = dto.firstName,
                        lastName = dto.lastName,
                        username = dto.username,
                        email = dto.email,
                        image = dto.image,
                        age = dto.age,
                        phone = dto.phone,
                        birthDate = dto.birthDate,
                        address = dto.address?.let { "${it.address}, ${it.city}" }
                    )
                )
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}