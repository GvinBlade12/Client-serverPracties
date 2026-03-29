package com.example.ex8

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.observe
import androidx.work.*

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PhotoProcessingScreen()
        }
    }
}

@Composable
fun PhotoProcessingScreen() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val workManager = WorkManager.getInstance(context)

    var status by remember { mutableStateOf("Ожидание") }
    var progress by remember { mutableStateOf(0f) }
    var resultText by remember { mutableStateOf("") }
    var isRunning by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text(status, style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(20.dp))

        if (isRunning) {
            LinearProgressIndicator(progress = progress)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {

                isRunning = true
                resultText = ""

                val compress = OneTimeWorkRequestBuilder<CompressWorker>().build()

                val watermark = OneTimeWorkRequestBuilder<WatermarkWorker>()
                    .setInputData(
                        workDataOf("filePath" to "compressed_photo.jpg")
                    )
                    .build()

                val upload = OneTimeWorkRequestBuilder<UploadWorker>()
                    .setInputData(
                        workDataOf("filePath" to "watermarked_photo.jpg")
                    )
                    .build()

                workManager.beginWith(compress)
                    .then(watermark)
                    .then(upload)
                    .enqueue()

                // 👇 наблюдаем за ВСЕЙ цепочкой
                workManager.getWorkInfosForUniqueWorkLiveData(upload.id.toString())

                workManager.getWorkInfoByIdLiveData(compress.id)
                    .observeForever { info ->
                        if (info != null && info.state == WorkInfo.State.RUNNING) {
                            status = "Сжимаем фото..."
                            progress = info.progress.getInt("progress", 0) / 100f
                        }
                    }

                workManager.getWorkInfoByIdLiveData(watermark.id)
                    .observeForever { info ->
                        if (info != null && info.state == WorkInfo.State.RUNNING) {
                            status = "Добавляем водяной знак..."
                            progress = info.progress.getInt("progress", 0) / 100f
                        }
                    }

                workManager.getWorkInfoByIdLiveData(upload.id)
                    .observeForever { info ->
                        if (info != null) {
                            when (info.state) {
                                WorkInfo.State.RUNNING -> {
                                    status = "Загружаем..."
                                    progress = info.progress.getInt("progress", 0) / 100f
                                }

                                WorkInfo.State.SUCCEEDED -> {
                                    isRunning = false
                                    resultText = info.outputData.getString("result") ?: "Готово!"
                                }

                                WorkInfo.State.FAILED -> {
                                    isRunning = false
                                    resultText = "Ошибка обработки!"
                                }

                                else -> {}
                            }
                        }
                    }

            },
            enabled = !isRunning
        ) {
            Text("Начать обработку и загрузку")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(resultText)
    }
}