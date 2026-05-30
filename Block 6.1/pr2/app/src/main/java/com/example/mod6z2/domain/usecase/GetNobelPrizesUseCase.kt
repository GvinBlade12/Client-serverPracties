package com.example.mod6z2.domain.usecase
import com.example.mod6z2.domain.model.NobelPrize
import com.example.mod6z2.domain.repository.NobelRepository

class GetNobelPrizesUseCase(
    private val repository: NobelRepository
) {
    suspend operator fun invoke(year: String?, category: String?): Result<List<NobelPrize>> {
        return repository.getNobelPrizes(year, category)
    }
}