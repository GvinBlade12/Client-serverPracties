package com.example.nobelapi.domain.repository

import com.example.nobelapi.domain.model.Laureate
import com.example.nobelapi.domain.model.NobelPrize

interface NobelPrizeRepository {
    suspend fun getAllPrizes(): List<NobelPrize>
    suspend fun getPrizeByYearAndCategory(year: String, category: String): NobelPrize?
    suspend fun getLaureatesByPrize(year: String, category: String): List<Laureate>?
    suspend fun savePrize(prize: NobelPrize): Int
    suspend fun getPrizeById(id: Int): NobelPrize?
    suspend fun prizeExists(prizeId: String): Boolean
    suspend fun savePrizeFromApi(prizeDto: com.example.nobelapi.data.api.NobelPrizeApiDto): Int
}