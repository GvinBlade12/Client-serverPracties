package com.example.ex14

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {

    private val viewModel: CompassViewModel by viewModels()
    private lateinit var sensorManager: CompassSensorManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorManager = CompassSensorManager(
            this,
            onAzimuthChanged = { viewModel.updateAzimuth(it) },
            onSensorError = { viewModel.setSensorError() }
        )

        setContent {
            CompassScreen(viewModel)
        }
    }

    override fun onResume() {
        super.onResume()
        sensorManager.start()
    }

    override fun onPause() {
        super.onPause()
        sensorManager.stop()
    }
}

@Composable
fun CompassScreen(viewModel: CompassViewModel) {
    val azimuth by viewModel.azimuth.collectAsState()
    val error by viewModel.sensorError.collectAsState()

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF101418))
    ) {
        if (error) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    "Устройство не поддерживает датчик ориентации",
                    color = Color.Red,
                    fontSize = 22.sp
                )
            }
            return@Surface
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Компас", fontSize = 28.sp, color = Color.White)

            CompassView(azimuth)

            Text(
                text = "Азимут: $azimuth°",
                fontSize = 26.sp,
                color = Color.White
            )
        }
    }
}

@Composable
fun CompassView(azimuth: Int) {
    val animatedRotation by animateFloatAsState(
        targetValue = -azimuth.toFloat(),
        animationSpec = tween(500),
        label = "rotation"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.DarkGray, CircleShape)
        ) {
            rotate(animatedRotation) {
                val center = Offset(size.width / 2, size.height / 2)
                val length = size.minDimension * 0.4f

                // Север (красный)
                drawLine(
                    color = Color.Red,
                    start = center,
                    end = Offset(center.x, center.y - length),
                    strokeWidth = 12f
                )

                // Юг (серый)
                drawLine(
                    color = Color.LightGray,
                    start = center,
                    end = Offset(center.x, center.y + length),
                    strokeWidth = 12f
                )
            }
        }

        Text("N", color = Color.White, fontSize = 32.sp, modifier = Modifier.align(Alignment.TopCenter))
    }
}