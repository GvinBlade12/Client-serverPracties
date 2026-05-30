package com.example.prac1.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.prac1.domain.model.Photo

@Composable
fun PhotoListScreen(
    state: PhotoState,
    onRetry: () -> Unit,
    onPhotoClick: (Photo) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        when (state) {
            is PhotoState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            is PhotoState.Success -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.photos) { photo ->
                        PhotoItem(photo = photo, onClick = { onPhotoClick(photo) })
                    }
                }
            }
            is PhotoState.Error -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Ошибка: ${state.message}", color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { onRetry() }) { Text("Повторить") }
                }
            }
        }
    }
}

@Composable
fun PhotoItem(photo: Photo, onClick: () -> Unit) { // ИСПРАВЛЕНО: заменили Void на Unit
    Card(modifier = Modifier.clickable { onClick() }) { // ИСПРАВЛЕНО: обернули в лямбду { onClick() }
        Column {
            AsyncImage(
                model = photo.downloadUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().height(150.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(8.dp)) {
                Text(text = photo.author, style = MaterialTheme.typography.titleSmall)
                Text(text = "${photo.width} × ${photo.height}", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}