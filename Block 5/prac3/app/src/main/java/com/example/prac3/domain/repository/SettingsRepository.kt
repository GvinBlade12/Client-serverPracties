package com.example.prac3.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun observeCompletedColorEnabled(): Flow<Boolean>
    suspend fun setCompletedColorEnabled(enabled: Boolean)
}
