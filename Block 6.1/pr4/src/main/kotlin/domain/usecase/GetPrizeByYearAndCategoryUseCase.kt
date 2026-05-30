package com.example.nobelapi.domain.usecase

import com.example.nobelapi.domain.model.NobelPrize
import com.example.nobelapi.domain.repository.NobelPrizeRepository

class GetPrizeByYearAndCategoryUseCase(
    private val repository: NobelPrizeRepository
) {
    suspend operator fun invoke(year: String, category: String): NobelPrize? {
        return repository.getPrizeByYearAndCategory(year, category)
    }
}