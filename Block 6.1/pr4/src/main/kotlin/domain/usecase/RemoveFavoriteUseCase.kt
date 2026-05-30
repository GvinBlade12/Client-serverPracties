package com.example.nobelapi.domain.usecase

import com.example.nobelapi.domain.repository.UserPrizeRepository

class RemoveFavoriteUseCase(
    private val userPrizeRepository: UserPrizeRepository
) {
    suspend operator fun invoke(userId: Int, prizeId: Int) {
        userPrizeRepository.removeFavorite(userId, prizeId)
    }
}