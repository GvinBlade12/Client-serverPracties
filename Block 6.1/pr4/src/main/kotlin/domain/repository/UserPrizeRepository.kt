package com.example.nobelapi.domain.repository

import com.example.nobelapi.domain.model.NobelPrize
import com.example.nobelapi.domain.model.UserPrize

interface UserPrizeRepository {
    suspend fun getUserFavorites(userId: Int): List<NobelPrize>
    suspend fun addFavorite(userId: Int, prizeId: Int): Boolean
    suspend fun removeFavorite(userId: Int, prizeId: Int): Boolean
    suspend fun isFavorite(userId: Int, prizeId: Int): Boolean
}