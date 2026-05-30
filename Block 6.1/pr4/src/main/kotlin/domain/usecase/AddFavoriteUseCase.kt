package com.example.nobelapi.domain.usecase

import com.example.nobelapi.domain.repository.UserPrizeRepository

class AddFavoriteUseCase(
    private val userPrizeRepository: UserPrizeRepository
) {
    suspend operator fun invoke(userId: Int, prizeId: Int): Boolean {
        if (userPrizeRepository.isFavorite(userId, prizeId)) {
            return false
        }
        return userPrizeRepository.addFavorite(userId, prizeId)
    }
}