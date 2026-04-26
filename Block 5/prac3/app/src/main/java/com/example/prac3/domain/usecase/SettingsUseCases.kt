package com.example.prac3.domain.usecase

import com.example.prac3.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

class ObserveCompletedColorUseCase(private val repository: SettingsRepository) {
    operator fun invoke(): Flow<Boolean> = repository.observeCompletedColorEnabled()
}

class SetCompletedColorUseCase(private val repository: SettingsRepository) {
    suspend operator fun invoke(enabled: Boolean) = repository.setCompletedColorEnabled(enabled)
}
