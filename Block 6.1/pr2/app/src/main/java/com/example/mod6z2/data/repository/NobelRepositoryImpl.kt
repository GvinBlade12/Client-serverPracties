package com.example.mod6z2.data.repository

import com.example.mod6z2.data.api.NobelApiService
import com.example.mod6z2.data.storage.TokenDataStore
import com.example.mod6z2.domain.model.Laureate
import com.example.mod6z2.domain.model.NobelPrize
import com.example.mod6z2.domain.repository.NobelRepository
import kotlinx.coroutines.flow.first

class NobelRepositoryImpl(
    private val apiService: NobelApiService,
    private val tokenDataStore: TokenDataStore
) : NobelRepository {

    override suspend fun login(username: String, password: String): Result<String> {
        return try {
            val response = apiService.login(username, password)
            tokenDataStore.saveToken("Bearer ${response.token}")
            Result.success(response.token)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getNobelPrizes(year: String?, category: String?): Result<List<NobelPrize>> {
        return try {
            val token = tokenDataStore.getToken.first()
            if (token.isEmpty()) {
                return Result.failure(Exception("Not authenticated"))
            }
            val prizes = apiService.getPrizes(token)
            val result = prizes.map { dto ->
                NobelPrize(
                    awardYear = dto.awardYear,
                    category = dto.category.en,
                    laureates = dto.laureates?.map { laureateDto ->
                        Laureate(
                            id = laureateDto.id,
                            fullName = laureateDto.fullName.en,
                            motivation = laureateDto.motivation?.en ?: "",
                            portion = "1"
                        )
                    } ?: emptyList()
                )
            }
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        tokenDataStore.clearToken()
    }
}