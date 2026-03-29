package com.example.ex9

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.work.ArrayCreatingInputMerger
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf

@Composable
fun WeatherAppScreen() {
    val context = LocalContext.current
    val workManager = WorkManager.getInstance(context)

    // Основное состояние
    var cityTemperatures by remember { mutableStateOf<List<Pair<String, Int>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf("Нажмите кнопку для получения прогноза") }

    // Наблюдаем за всеми WeatherWorker и ReportWorker
    val allWorkInfo by workManager.getWorkInfosByTagLiveData("weather").observeAsState(emptyList())
    val reportWorkInfo by workManager.getWorkInfosByTagLiveData("report").observeAsState(emptyList())

    // Обновляем список температур по мере завершения WeatherWorker'ов
    LaunchedEffect(allWorkInfo) {
        val completedWorks = allWorkInfo.filter { it.state == WorkInfo.State.SUCCEEDED }

        val results = completedWorks.mapNotNull { workInfo ->
            val city = workInfo.outputData.getString("city")
            val temp = workInfo.outputData.getInt("temperature", -999)
            if (city != null && temp != -999) {
                city to temp
            } else null
        }

        if (results.isNotEmpty()) {
            cityTemperatures = results
        }
    }

    // Следим за состоянием ReportWorker
    LaunchedEffect(reportWorkInfo) {
        reportWorkInfo.firstOrNull()?.let { info ->
            when (info.state) {
                WorkInfo.State.RUNNING -> {
                    isLoading = true
                    statusMessage = "Формируем итоговый отчёт..."
                }
                WorkInfo.State.SUCCEEDED -> {
                    isLoading = false
                    statusMessage = "Все данные успешно получены!"
                }
                WorkInfo.State.FAILED -> {
                    isLoading = false
                    statusMessage = "Ошибка при обработке данных"
                }
                else -> {}
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Прогноз погоды в городах",
            style = MaterialTheme.typography.headlineMedium
        )

        Button(
            onClick = {
                // Сброс состояния
                cityTemperatures = emptyList()
                isLoading = true
                statusMessage = "Загружаем прогноз для 4 городов..."

                val cities = listOf("Москва", "Лондон", "Нью-Йорк", "Токио")

                val weatherRequests = cities.map { city ->
                    OneTimeWorkRequestBuilder<WeatherWorker>()
                        .setInputData(workDataOf("city" to city))
                        .addTag("weather")           // важно!
                        .build()
                }

                val reportRequest = OneTimeWorkRequestBuilder<ReportWorker>()
                    .setInputMerger(ArrayCreatingInputMerger::class.java)
                    .addTag("report")
                    .build()

                workManager
                    .beginWith(weatherRequests)
                    .then(reportRequest)
                    .enqueue()
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isLoading) "Загрузка..." else "Собрать прогноз")
        }

        Text(
            text = statusMessage,
            style = MaterialTheme.typography.bodyLarge
        )

        if (isLoading && cityTemperatures.isEmpty()) {
            CircularProgressIndicator()
        }

        // Список городов с температурами
        if (cityTemperatures.isNotEmpty()) {
            Text(
                text = "Температура в городах:",
                style = MaterialTheme.typography.titleMedium
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(cityTemperatures) { (city, temp) ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = city,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "$temp°C",
                                style = MaterialTheme.typography.headlineSmall
                            )
                        }
                    }
                }
            }
        }

        // Финальное сообщение после завершения
        if (!isLoading && cityTemperatures.isNotEmpty()) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Задание выполнено успешно",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Все 4 города обработаны в фоне")
                }
            }
        }
    }
}