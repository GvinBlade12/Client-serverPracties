package com.example.nobelapi.data.repository

import com.example.nobelapi.domain.model.Laureate
import com.example.nobelapi.domain.model.NobelPrize
import com.example.nobelapi.domain.repository.NobelPrizeRepository

class InMemoryNobelPrizeRepository : NobelPrizeRepository {
    private val prizes = listOf(
        NobelPrize(
            awardYear = "2023",
            category = "physics",
            prizeAmount = 10000000,
            laureates = listOf(
                Laureate("1", "Pierre Agostini", "for experimental methods"),
                Laureate("2", "Ferenc Krausz", "for experimental methods"),
                Laureate("3", "Anne L'Huillier", "for experimental methods")
            )
        ),
        NobelPrize(
            awardYear = "2023",
            category = "chemistry",
            prizeAmount = 10000000,
            laureates = listOf(
                Laureate("4", "Moungi G. Bawendi", "for the discovery and synthesis of quantum dots"),
                Laureate("5", "Louis E. Brus", "for the discovery and synthesis of quantum dots"),
                Laureate("6", "Alexei I. Ekimov", "for the discovery and synthesis of quantum dots")
            )
        ),
        NobelPrize(
            awardYear = "2023",
            category = "medicine",
            prizeAmount = 10000000,
            laureates = listOf(
                Laureate("7", "Katalin Karikó", "for discoveries concerning nucleoside base modifications"),
                Laureate("8", "Drew Weissman", "for discoveries concerning nucleoside base modifications")
            )
        )
    )

    override suspend fun getAllPrizes(): List<NobelPrize> = prizes

    override suspend fun getPrizeByYearAndCategory(year: String, category: String): NobelPrize? {
        return prizes.find { it.awardYear == year && it.category.equals(category, ignoreCase = true) }
    }

    override suspend fun getLaureatesByPrize(year: String, category: String): List<Laureate>? {
        return getPrizeByYearAndCategory(year, category)?.laureates
    }

    override suspend fun prizeExists(prizeId: String): Boolean {
        return prizes.any { "${it.awardYear}-${it.category.lowercase()}" == prizeId.lowercase() }
    }

    override suspend fun savePrize(prize: NobelPrize): Int {
        return 0
    }

    override suspend fun getPrizeById(id: Int): NobelPrize? {
        return prizes.getOrNull(id - 1)
    }

    override suspend fun savePrizeFromApi(prizeDto: com.example.nobelapi.data.api.NobelPrizeApiDto): Int {
        return 0
    }
}