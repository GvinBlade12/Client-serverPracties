package com.example.ex5

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d("MainActivity", "Notification permission granted")
        } else {
            Log.d("MainActivity", "Notification permission denied")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Запрашиваем разрешение на уведомления для Android 13+
        if (Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.d("MainActivity", "Requesting notification permission")
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                Log.d("MainActivity", "Notification permission already granted")
            }
        }

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TimerScreen()
                }
            }
        }
    }
}

@Composable
fun TimerScreen() {
    val context = LocalContext.current

    // Используем mutableStateOf с явным указанием
    val secondsState = remember { mutableStateOf(0) }

    val receiver = remember {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val newSeconds = intent?.getIntExtra(
                    TimerForegroundService.EXTRA_SECONDS,
                    0
                ) ?: 0
                Log.d("TimerScreen", "Received update: $newSeconds seconds")
                secondsState.value = newSeconds
            }
        }
    }

    DisposableEffect(Unit) {
        Log.d("TimerScreen", "Registering receiver...")
        val filter = IntentFilter(TimerForegroundService.ACTION_TIMER_UPDATE)

        // Регистрируем receiver
        try {
            if (Build.VERSION.SDK_INT >= 33) {
                context.registerReceiver(
                    receiver,
                    filter,
                    Context.RECEIVER_NOT_EXPORTED
                )
            } else {
                context.registerReceiver(receiver, filter)
            }
            Log.d("TimerScreen", "Receiver registered successfully")
        } catch (e: Exception) {
            Log.e("TimerScreen", "Error registering receiver", e)
        }

        onDispose {
            try {
                context.unregisterReceiver(receiver)
                Log.d("TimerScreen", "Receiver unregistered")
            } catch (e: IllegalArgumentException) {
                Log.e("TimerScreen", "Error unregistering receiver", e)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = secondsState.value.toString(),
            fontSize = 64.sp,
            style = MaterialTheme.typography.displayLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = {
                    Log.d("TimerScreen", "Start button clicked")
                    val intent = Intent(context, TimerForegroundService::class.java)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(intent)
                        Log.d("TimerScreen", "startForegroundService called")
                    } else {
                        context.startService(intent)
                        Log.d("TimerScreen", "startService called")
                    }
                }
            ) {
                Text("Старт")
            }

            Button(
                onClick = {
                    Log.d("TimerScreen", "Stop button clicked")
                    val intent = Intent(context, TimerForegroundService::class.java)
                    context.stopService(intent)
                    Log.d("TimerScreen", "stopService called")
                    secondsState.value = 0 // Сбрасываем UI при остановке
                }
            ) {
                Text("Стоп")
            }
        }
    }
}