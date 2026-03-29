package com.example.ex11

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

class AlarmReceiver : BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent?) {
        showNotification(context)

        // Ставим следующий будильник
        AlarmScheduler(context).scheduleNextAlarm()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showNotification(context: Context) {
        val channelId = "pill_channel"

        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            channelId,
            "Pill Reminder",
            NotificationManager.IMPORTANCE_HIGH
        )
        manager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Напоминание")
            .setContentText("Время принять таблетку!")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()

        manager.notify(1, notification)
    }
}