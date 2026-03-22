package com.example.ex5

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*

class TimerForegroundService : Service() {
    private var seconds = 0
    private var job: Job? = null

    companion object {
        const val ACTION_TIMER_UPDATE = "TIMER_UPDATE"
        const val EXTRA_SECONDS = "seconds"
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "timer_channel"
        private const val TAG = "TimerService"
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")

        // Сбрасываем секунды при новом запуске
        if (job == null) {
            seconds = 0
            Log.d(TAG, "Starting fresh timer")
        } else {
            Log.d(TAG, "Timer already running, continuing from $seconds")
        }

        // Создаем уведомление и запускаем foreground
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)
        Log.d(TAG, "startForeground called")

        // Запускаем таймер, если ещё не запущен
        if (job == null) {
            job = CoroutineScope(Dispatchers.Default).launch {
                Log.d(TAG, "Timer coroutine started")
                while (isActive) {
                    delay(1000)
                    seconds++
                    Log.d(TAG, "Timer: $seconds seconds")

                    // Обновляем уведомление
                    val updatedNotification = createNotification()
                    val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                    manager.notify(NOTIFICATION_ID, updatedNotification)

                    // Отправляем широковещательное сообщение для обновления UI
                    val broadcastIntent = Intent(ACTION_TIMER_UPDATE).apply {
                        putExtra(EXTRA_SECONDS, seconds)
                        setPackage(packageName) // Важно! Ограничиваем наше приложение
                    }
                    sendBroadcast(broadcastIntent)
                    Log.d(TAG, "Broadcast sent: $seconds")
                }
            }
        }

        return START_STICKY
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        job?.cancel()
        job = null
        seconds = 0
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotification(): Notification {
        Log.d(TAG, "Creating notification for seconds: $seconds")

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Таймер запущен")
            .setContentText("Прошло $seconds секунд")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d(TAG, "Creating notification channel")
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Timer Channel",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Канал для отображения времени работы таймера"
                setShowBadge(false)
            }

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
            Log.d(TAG, "Notification channel created")
        }
    }
}