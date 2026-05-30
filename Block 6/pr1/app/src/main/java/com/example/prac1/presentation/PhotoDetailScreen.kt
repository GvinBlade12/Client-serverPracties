package com.example.prac1.presentation

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.prac1.domain.model.Photo


@Composable
fun PhotoDetailScreen(
    photo: Photo,
    onDownloadClick: (Uri) -> Unit
) {
    // Лаунчер для SAF (создание файла в папке Downloads)
    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("image/jpeg")
    ) { uri ->
        uri?.let { onDownloadClick(it) }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        AsyncImage(
            model = photo.downloadUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxWidth().weight(1f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Автор: ${photo.author}", style = MaterialTheme.typography.titleLarge)
        Text(text = "Разрешение: ${photo.width} x ${photo.height} px")
        Text(text = "Ссылка: ${photo.url}", style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { createDocumentLauncher.launch("photo_${photo.id}.jpg") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Скачать фото")
        }
    }
}