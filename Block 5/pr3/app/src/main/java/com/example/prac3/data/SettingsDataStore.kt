package com.example.prac3.data

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsDataStore(private val context: Context) {

    private val KEY = booleanPreferencesKey("colored")

    val isColored = context.dataStore.data.map { prefs ->
        prefs[KEY] ?: true
    }

    suspend fun setColored(value: Boolean) {
        context.dataStore.edit {
            it[KEY] = value
        }
    }
}