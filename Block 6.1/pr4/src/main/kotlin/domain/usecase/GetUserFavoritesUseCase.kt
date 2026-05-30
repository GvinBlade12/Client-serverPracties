package com.example.nobelapi.domain.usecase

import com.example.nobelapi.domain.model.NobelPrize
import com.example.nobelapi.domain.repository.UserPrizeRepository

class GetUserFavoritesUseCase(
    private val userPrizeRepository: UserPrizeRepository
) {
    suspend operator fun invoke(userId: Int): List<NobelPrize> {
        return userPrizeRepository.getUserFavorites(userId)
    }
}