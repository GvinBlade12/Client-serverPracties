package com.example.nobelapi.di

import com.example.nobelapi.data.repository.NobelPrizeRepositoryImpl
import com.example.nobelapi.data.repository.UserPrizeRepositoryImpl
import com.example.nobelapi.data.repository.UserRepositoryImpl
import com.example.nobelapi.domain.repository.NobelPrizeRepository
import com.example.nobelapi.domain.repository.UserPrizeRepository
import com.example.nobelapi.domain.repository.UserRepository
import com.example.nobelapi.domain.usecase.*
import com.example.nobelapi.security.JwtService

class AppModule {
    val jwtService by lazy { JwtService() }

    private val userRepository: UserRepository by lazy { UserRepositoryImpl() }
    private val prizeRepository: NobelPrizeRepository by lazy { NobelPrizeRepositoryImpl() }
    private val userPrizeRepository: UserPrizeRepository by lazy { UserPrizeRepositoryImpl() }
    val validateUserUseCase by lazy { ValidateUserUseCase(userRepository) }
    val getPrizesUseCase by lazy { GetPrizesUseCase(prizeRepository) }
    val getPrizeByYearAndCategoryUseCase by lazy { GetPrizeByYearAndCategoryUseCase(prizeRepository) }
    val getLaureatesByPrizeUseCase by lazy { GetLaureatesByPrizeUseCase(prizeRepository) }
    val getUserProfileUseCase by lazy { GetUserProfileUseCase(userRepository) }
    val getUserFavoritesUseCase by lazy { GetUserFavoritesUseCase(userPrizeRepository) }
    val addFavoriteUseCase by lazy { AddFavoriteUseCase(userPrizeRepository) }
    val removeFavoriteUseCase by lazy { RemoveFavoriteUseCase(userPrizeRepository) }
    val loadNobelPrizesUseCase by lazy { LoadNobelPrizesUseCase(prizeRepository) }
}