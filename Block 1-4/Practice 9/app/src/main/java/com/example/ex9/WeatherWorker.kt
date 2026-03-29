package com.example.ex9

import android.content.Context
import android.content.pm.ServiceInfo
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.delay

class WeatherWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return createForegroundInfo("Загружаем погоду...")
    }

    override suspend fun doWork(): Result {
        val city = inputData.getString("city") ?: "Неизвестный город"
        Log.d("WeatherWorker", "Начало работы для: $city")

        setForeground(createForegroundInfo("Загружаем погоду для $city..."))

        delay(3000) // имитация загрузки

        val temperature = (5..25).random()
        Log.d("WeatherWorker", "Для $city получена температура: $temperature°C")

        return Result.success(
            workDataOf(
                "city" to city,
                "temperature" to temperature
            )
        )
    }

    private fun createForegroundInfo(text: String): ForegroundInfo {
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle("Прогноз погоды")
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        return ForegroundInfo(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
    }
}