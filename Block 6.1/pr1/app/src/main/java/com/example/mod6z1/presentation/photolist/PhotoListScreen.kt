package com.example.mod6z1.presentation.photolist
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mod6z1.domain.model.Photo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoListScreen(
    navController: NavController,
    viewModel: PhotoListViewModel
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Фотокаталог") }) }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when (state) {
                is PhotoListState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is PhotoListState.Success -> {
                    PhotoGrid(
                        photos = (state as PhotoListState.Success).photos,
                        onPhotoClick = { photo ->
                            navController.currentBackStackEntry?.savedStateHandle?.set("photo", photo)
                            navController.navigate("photo_detail/${photo.id}")
                        }
                    )
                }
                is PhotoListState.Error -> {
                    ErrorView(
                        message = (state as PhotoListState.Error).message,
                        onRetry = { viewModel.loadPhotos() }
                    )
                }
            }
        }
    }
}

@Composable
fun PhotoGrid(photos: List<Photo>, onPhotoClick: (Photo) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize().padding(8.dp)
    ) {
        items(photos) { photo ->
            PhotoCard(photo = photo, onClick = { onPhotoClick(photo) })
        }
    }
}

@Composable
fun PhotoCard(photo: Photo, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.height(250.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = photo.url,
                contentDescription = photo.author,
                modifier = Modifier.fillMaxWidth().weight(1f).clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                Text(text = "Автор: ${photo.author}", maxLines = 1)
                Text(text = "Размер: ${photo.width} × ${photo.height}")
            }
        }
    }
}

@Composable
fun ErrorView(message: String, onRetry: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text(text = message, color = MaterialTheme.colorScheme.error)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) { Text("Повторить") }
    }
}