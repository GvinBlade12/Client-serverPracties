package com.example.mod6z2.domain.repository

import com.example.mod6z2.domain.model.NobelPrize

interface NobelRepository {
    suspend fun getNobelPrizes(year: String?, category: String?): Result<List<NobelPrize>>
    suspend fun login(username: String, password: String): Result<String>
    suspend fun logout()
}