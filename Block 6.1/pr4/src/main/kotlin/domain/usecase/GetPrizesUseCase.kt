package com.example.nobelapi.domain.usecase

import com.example.nobelapi.domain.model.NobelPrize
import com.example.nobelapi.domain.repository.NobelPrizeRepository

class GetPrizesUseCase(
    private val repository: NobelPrizeRepository
) {
    suspend operator fun invoke(): List<NobelPrize> {
        return repository.getAllPrizes()
    }
}