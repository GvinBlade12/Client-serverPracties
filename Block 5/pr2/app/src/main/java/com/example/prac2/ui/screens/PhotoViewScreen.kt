package com.example.prac2.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberAsyncImagePainter
import com.example.prac2.data.MediaStoreExporter
import com.example.prac2.data.PhotoItem
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoViewScreen(photo: PhotoItem, onBack: () -> Unit) {

    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Просмотр фото") },
                actions = {
                    IconButton(onClick = { expanded = true }) {
                        Text("⋮")
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Экспорт в галерею") },
                            onClick = {
                                expanded = false
                                MediaStoreExporter.exportToGallery(
                                    context,
                                    photo.file
                                )
                                Toast.makeText(
                                    context,
                                    "Фото экспортировано",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    }
                }
            )
        }
    ) { padding ->

        Image(
            painter = rememberAsyncImagePainter(photo.file),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        )
    }
}