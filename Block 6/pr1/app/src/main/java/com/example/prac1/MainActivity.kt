package com.example.prac1

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.*
import androidx.lifecycle.lifecycleScope
import com.example.prac1.domain.model.Photo
import com.example.prac1.presentation.PhotoDetailScreen
import com.example.prac1.presentation.PhotoListScreen
import com.example.prac1.presentation.PhotoViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class MainActivity : ComponentActivity() {
    private val viewModel: PhotoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val state by viewModel.state
            var selectedPhoto by remember { mutableStateOf<Photo?>(null) }

            if (selectedPhoto == null) {
                PhotoListScreen(
                    state = state,
                    onRetry = { viewModel.loadPhotos() },
                    onPhotoClick = { selectedPhoto = it }
                )
            } else {
                PhotoDetailScreen(
                    photo = selectedPhoto!!,
                    onDownloadClick = { uri ->
                        downloadImage(selectedPhoto!!.downloadUrl, uri)
                    }
                )
            }
        }
    }

    // Функция скачивания через SAF
    private fun downloadImage(urlStr: String, uri: Uri) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val url = URL(urlStr)
                contentResolver.openOutputStream(uri)?.use { outputStream ->
                    url.openStream().use { inputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Скачано успешно!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Ошибка скачивания", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}