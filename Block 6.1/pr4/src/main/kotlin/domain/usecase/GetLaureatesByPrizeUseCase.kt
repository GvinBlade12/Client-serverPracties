package com.example.nobelapi.domain.usecase

import com.example.nobelapi.domain.model.Laureate
import com.example.nobelapi.domain.repository.NobelPrizeRepository

class GetLaureatesByPrizeUseCase(
    private val repository: NobelPrizeRepository
) {
    suspend operator fun invoke(year: String, category: String): List<Laureate>? {
        return repository.getLaureatesByPrize(year, category)
    }
}