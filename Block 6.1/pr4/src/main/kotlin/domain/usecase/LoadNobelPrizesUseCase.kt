package com.example.nobelapi.domain.usecase

import com.example.nobelapi.data.api.NobelPrizeApiClient
import com.example.nobelapi.domain.repository.NobelPrizeRepository

class LoadNobelPrizesUseCase(
    private val prizeRepository: NobelPrizeRepository
) {
    private val apiClient = NobelPrizeApiClient()

    suspend operator fun invoke(): Int {
        val response = apiClient.fetchNobelPrizes()
        var count = 0
        var exists = 0
        for (prizeDto in response.nobelPrizes) {
            val prizeId = "${prizeDto.awardYear}-${prizeDto.category.en}"
            if (prizeRepository.prizeExists(prizeId)) {
                exists++
            } else {
                prizeRepository.savePrizeFromApi(prizeDto)
                count++
            }
        }
        println("Loaded $count new prizes, $exists already exist")
        return count
    }
}