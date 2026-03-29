package com.example.ex7

import android.content.*
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {

    private var randomService: RandomService? = null
    private var isBound = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as RandomService.LocalBinder
            randomService = binder.getService()
            isBound = true

            randomService?.startGenerating()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
            randomService = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var number by remember { mutableStateOf(0) }

            MainScreen(
                number = number,
                onConnect = {
                    val intent = Intent(this, RandomService::class.java)
                    bindService(intent, connection, Context.BIND_AUTO_CREATE)

                    randomService?.setListener {
                        number = it
                    }
                },
                onDisconnect = {
                    if (isBound) {
                        randomService?.stopGenerating()
                        unbindService(connection)
                        isBound = false
                    }
                }
            )
        }
    }
}


@Composable
fun MainScreen(
    number: Int,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Случайное число: $number")

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onConnect) {
            Text("Подключиться")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = onDisconnect) {
            Text("Отключиться")
        }
    }
}