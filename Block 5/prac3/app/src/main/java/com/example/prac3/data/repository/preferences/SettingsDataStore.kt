package com.example.prac3.data.repository.preferences

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStoreFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsDataStore(context: Context) {
    private val dataStore = PreferenceDataStoreFactory.create(
        produceFile = { context.preferencesDataStoreFile("todo_settings.preferences_pb") }
    )

    val completedColorEnabled: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[KEY_COMPLETED_COLOR_ENABLED] ?: false
    }

    val isJsonImported: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[KEY_JSON_IMPORTED] ?: false
    }

    suspend fun setCompletedColorEnabled(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[KEY_COMPLETED_COLOR_ENABLED] = enabled
        }
    }

    suspend fun setJsonImported(imported: Boolean) {
        dataStore.edit { prefs ->
            prefs[KEY_JSON_IMPORTED] = imported
        }
    }

    private companion object {
        val KEY_COMPLETED_COLOR_ENABLED = booleanPreferencesKey("completed_color_enabled")
        val KEY_JSON_IMPORTED = booleanPreferencesKey("json_imported")
    }
}
