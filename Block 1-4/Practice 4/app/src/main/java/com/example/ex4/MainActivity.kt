package com.example.ex4

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                SocialFeedScreen(this)
            }
        }
    }
}

// Data classes
data class Post(
    val id: Int,
    val userId: Int,
    val title: String,
    val body: String,
    val avatarUrl: String
)

data class Comment(
    val postId: Int,
    val id: Int,
    val name: String,
    val body: String
)

sealed class PostState {
    object Loading : PostState()
    data class Ready(
        val avatar: String,
        val comments: List<Comment>
    ) : PostState()
    data class Error(val message: String = "Ошибка загрузки") : PostState()
}

// Загрузка JSON из assets
suspend fun loadPosts(context: Context): List<Post> = withContext(IO) {
    val json = context.assets.open("social_posts.json")
        .bufferedReader().use { it.readText() }
    val type = object : TypeToken<List<Post>>() {}.type
    Gson().fromJson(json, type)
}

suspend fun loadAllComments(context: Context): List<Comment> = withContext(IO) {
    val json = context.assets.open("comments.json")
        .bufferedReader().use { it.readText() }
    val type = object : TypeToken<List<Comment>>() {}.type
    Gson().fromJson(json, type)
}

// Загрузка данных ОДНОГО поста
suspend fun loadPostData(
    post: Post,
    allComments: List<Comment>
): PostState = supervisorScope {
    try {
        val avatarDeferred = async {
            delay(800) // имитация сети
            post.avatarUrl
        }

        val commentsDeferred = async {
            delay(1200) // имитация сети
            allComments.filter { it.postId == post.id }
        }

        PostState.Ready(
            avatar = avatarDeferred.await(),
            comments = commentsDeferred.await()
        )
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        PostState.Error(e.message ?: "Ошибка загрузки")
    }
}

// Компонент карточки поста
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostCard(post: Post, state: PostState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Заголовок поста
            Text(
                text = post.title,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Тело поста
            Text(
                text = post.body,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Используем Divider (не экспериментальный)
            Divider()

            Spacer(modifier = Modifier.height(8.dp))

            // Состояние загрузки контента
            when (state) {
                is PostState.Loading -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Загрузка аватарки и комментариев...")
                    }
                }

                is PostState.Ready -> {
                    // Аватар
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Аватар",
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Аватар: ${state.avatar}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Комментарии
                    Text(
                        text = "Комментарии (${state.comments.size}):",
                        fontWeight = FontWeight.Medium,
                        style = MaterialTheme.typography.bodySmall
                    )

                    state.comments.forEach { comment ->
                        Spacer(modifier = Modifier.height(4.dp))
                        Row {
                            Text(
                                text = "💬 ",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "${comment.name}: ${comment.body}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    if (state.comments.isEmpty()) {
                        Text(
                            text = "💬 Нет комментариев",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                is PostState.Error -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Ошибка",
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

// Основной экран
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialFeedScreen(context: Context) {
    val scope = rememberCoroutineScope()
    var loadJob by remember { mutableStateOf<Job?>(null) }
    var posts by remember { mutableStateOf<List<Post>>(emptyList()) }
    var postStates by remember { mutableStateOf<Map<Int, PostState>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(false) }

    // Функция загрузки ленты
    fun loadFeed() {
        // Отменяем предыдущую загрузку
        loadJob?.cancel()

        loadJob = scope.launch {
            isLoading = true

            try {
                // Загружаем посты
                val loadedPosts = loadPosts(context)
                posts = loadedPosts

                // Загружаем все комментарии (один раз для всех постов)
                val allComments = loadAllComments(context)

                // Загружаем данные для каждого поста параллельно
                val stateJobs = loadedPosts.map { post ->
                    async {
                        try {
                            post.id to loadPostData(post, allComments)
                        } catch (e: CancellationException) {
                            throw e
                        } catch (e: Exception) {
                            post.id to PostState.Error("Ошибка: ${e.message}")
                        }
                    }
                }

                // Ждем завершения всех загрузок
                stateJobs.forEach { job ->
                    try {
                        val (postId, state) = job.await()
                        postStates = postStates + (postId to state)
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        // Обрабатываем ошибки отдельных постов
                    }
                }
            } catch (e: CancellationException) {
                // Загрузка была отменена - очищаем состояние
                posts = emptyList()
                postStates = emptyMap()
            } catch (e: Exception) {
                // Обрабатываем общие ошибки
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    // Загружаем данные при первом запуске
    LaunchedEffect(Unit) {
        loadFeed()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Социальная лента") },
                actions = {
                    Button(
                        onClick = { loadFeed() },
                        enabled = !isLoading
                    ) {
                        Text("Обновить")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                posts.isEmpty() && !isLoading -> {
                    // Нет постов
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("Нет постов для отображения")
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { loadFeed() }) {
                            Text("Попробовать снова")
                        }
                    }
                }

                posts.isNotEmpty() -> {
                    // Список постов
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(posts) { post ->
                            val state = postStates[post.id] ?: PostState.Loading
                            PostCard(post = post, state = state)
                        }
                    }
                }

                isLoading && posts.isEmpty() -> {
                    // Индикатор загрузки
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}