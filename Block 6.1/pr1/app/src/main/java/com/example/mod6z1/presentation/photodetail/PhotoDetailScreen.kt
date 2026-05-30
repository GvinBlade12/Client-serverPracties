package com.example.mod6z1.presentation.photodetail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mod6z1.domain.model.Photo
import com.example.mod6z1.utils.DownloadUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoDetailScreen(
    navController: NavController,
    photo: Photo?
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isDownloading by remember { mutableStateOf(false) }
    var downloadError by remember { mutableStateOf<String?>(null) }
    var downloadSuccess by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Детали фото") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (photo == null) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("Фото не найдено")
            }
        } else {
            Column(modifier = Modifier.fillMaxSize().padding(paddingValues).verticalScroll(rememberScrollState())) {
                AsyncImage(
                    model = photo.downloadUrl,
                    contentDescription = photo.author,
                    modifier = Modifier.fillMaxWidth().height(400.dp).clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Информация: автор, размеры, ссылка
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Text(text = "Автор: ${photo.author}", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Размер: ${photo.width} × ${photo.height} пикселей")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Ссылка: ${photo.downloadUrl}", style = MaterialTheme.typography.bodySmall)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        scope.launch {
                            isDownloading = true
                            val result = DownloadUtils.downloadImage(context, photo.downloadUrl, photo.author)
                            result.onSuccess { downloadSuccess = true; kotlinx.coroutines.delay(3000); downloadSuccess = false }
                            result.onFailure { downloadError = it.message }
                            isDownloading = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    enabled = !isDownloading
                ) {
                    when {
                        isDownloading -> {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Скачивание...")
                        }
                        downloadSuccess -> {
                            Icon(Icons.Default.Download, null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Скачано!")
                        }
                        else -> {
                            Icon(Icons.Default.Download, null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Скачать фото")
                        }
                    }
                }
                if (downloadError != null) {
                    Text("Ошибка: $downloadError", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}