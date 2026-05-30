package com.example.mod6z7.di

import android.content.Context
import com.example.mod6z7.data.BLEManager
import com.example.mod6z7.data.BLERepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideBLERepository(@ApplicationContext context: Context): BLERepository {
        return BLEManager(context)
    }
}