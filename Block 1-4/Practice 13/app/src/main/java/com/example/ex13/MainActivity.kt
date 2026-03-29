package com.example.ex13

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {

    private val viewModel: CurrencyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CurrencyScreen(viewModel)
        }
    }
}

@Composable
fun CurrencyScreen(viewModel: CurrencyViewModel) {
    val rate by viewModel.rate.collectAsState()

    var previousRate by remember { mutableStateOf(rate) }

    val isUp = rate > previousRate
    val arrow = if (isUp) "↑" else "↓"
    val color = if (isUp) Color(0xFF2E7D32) else Color(0xFFC62828)

    LaunchedEffect(rate) {
        previousRate = rate
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text("Курс доллара к рублю", fontSize = 26.sp)

            Spacer(modifier = Modifier.height(32.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = String.format("%.2f ₽", rate),
                    fontSize = 40.sp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = arrow,
                    fontSize = 40.sp,
                    color = color
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = { viewModel.refreshNow() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Обновить сейчас")
            }
        }
    }
}