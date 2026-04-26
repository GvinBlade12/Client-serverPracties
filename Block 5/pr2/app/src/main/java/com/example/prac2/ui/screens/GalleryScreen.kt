package com.example.prac2.ui.screens

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import com.example.prac2.data.MediaStoreExporter
import com.example.prac2.data.PhotoItem
import com.example.prac2.viewmodel.PhotoViewModel
import java.io.File

@Composable
fun GalleryScreen(viewModel: PhotoViewModel) {

    var selectedPhoto by remember { mutableStateOf<PhotoItem?>(null) }
    val context = LocalContext.current
    var currentFile by remember { mutableStateOf<File?>(null) }

    // 📸 камера
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && currentFile != null) {
            viewModel.addPhoto(currentFile!!)
        }
    }

    // 🔐 permission
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val file = viewModel.createFile()
            currentFile = file

            val uri = FileProvider.getUriForFile(
                context,
                "com.example.prac2.fileprovider",
                file
            )

            cameraLauncher.launch(uri)
        }
    }

    fun openCameraFlow() {
        permissionLauncher.launch(Manifest.permission.CAMERA)
    }
    if (selectedPhoto != null) {
        PhotoViewScreen(
            photo = selectedPhoto!!,
            onBack = { selectedPhoto = null }
        )
        return
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                openCameraFlow()
            }) {
                Text("📷")
            }
        }
    ) { padding ->

        if (viewModel.photos.isEmpty()) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text("У вас пока нет фото")

                Spacer(modifier = Modifier.height(12.dp))

                Button(onClick = {
                    openCameraFlow()
                }) {
                    Text("Сделать первое фото")
                }
            }

        } else {

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.padding(padding)
            ) {
                items(viewModel.photos) { photo ->

                    var expanded by remember { mutableStateOf(false) }
                    val context = LocalContext.current

                    Box {
                        Image(
                            painter = rememberAsyncImagePainter(photo.file),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(4.dp)
                                .size(120.dp)
                                .clickable { selectedPhoto = photo }
                        )

                        IconButton(
                            onClick = { expanded = true },
                            modifier = Modifier.align(Alignment.TopEnd)
                        ) {
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
                                    MediaStoreExporter.exportToGallery(context, photo.file)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}