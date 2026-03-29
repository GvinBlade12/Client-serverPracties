package com.example.ex6

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

class TimerService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val seconds = intent?.getIntExtra("seconds", 0) ?: 0

        Thread {
            try {
                Thread.sleep(seconds * 1000L)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    showNotification()
                }
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

            stopSelf()
        }.start()

        return START_NOT_STICKY
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showNotification() {
        val channelId = "timer_channel"

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Создание канала (Android 8+)
        val channel = NotificationChannel(
            channelId,
            "Timer Channel",
            NotificationManager.IMPORTANCE_HIGH
        )
        manager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Таймер завершён!")
            .setContentText("Время вышло")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()

        manager.notify(1, notification)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}