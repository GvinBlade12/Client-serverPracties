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

class ReportWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return createForegroundInfo("Формируем отчёт...")
    }

    override suspend fun doWork(): Result {
        Log.d("ReportWorker", "ReportWorker запущен")

        setForeground(createForegroundInfo("Все данные получены, формируем отчёт..."))

        val temps = inputData.getIntArray("temperature")?.toList() ?: emptyList()

        Log.d("ReportWorker", "Получено температур: ${temps.size} шт.")

        if (temps.isEmpty()) {
            return Result.failure()
        }

        delay(2000)

        val average = temps.average().toInt()
        val text = "Отчёт готов! Средняя температура +${average}°C"

        Log.d("ReportWorker", text)

        setForeground(createFinalNotification(text))

        return Result.success(
            workDataOf("average_temperature" to average)
        )
    }

    private fun createForegroundInfo(text: String): ForegroundInfo {
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle("Прогноз погоды")
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .build()

        return ForegroundInfo(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
    }

    private fun createFinalNotification(text: String): ForegroundInfo {
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle("Прогноз погоды")
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(false)
            .setAutoCancel(true)
            .build()

        return ForegroundInfo(NOTIFICATION_ID + 1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
    }

}