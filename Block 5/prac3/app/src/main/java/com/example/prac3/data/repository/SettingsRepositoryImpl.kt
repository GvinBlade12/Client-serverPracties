package com.example.prac3.data.repository

import com.example.prac3.data.repository.preferences.SettingsDataStore
import com.example.prac3.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

class SettingsRepositoryImpl(
    private val settingsDataStore: SettingsDataStore
) : SettingsRepository {

    override fun observeCompletedColorEnabled(): Flow<Boolean> =
        settingsDataStore.completedColorEnabled

    override suspend fun setCompletedColorEnabled(enabled: Boolean) {
        settingsDataStore.setCompletedColorEnabled(enabled)
    }
}
