package com.example.ex3

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MaterialTheme {

                var repos by remember {
                    mutableStateOf<List<Repo>>(emptyList())
                }

                LaunchedEffect(Unit) {
                    repos = loadRepos(this@MainActivity)
                }

                RepoSearchScreen(repos)
            }
        }
    }
}

// ревьюшка
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MaterialTheme { // ✅ исправлено
        Greeting("Android")
    }
}

// модель данных
data class Repo(
    val id: Int,
    val full_name: String,
    val description: String?,
    val stargazers_count: Int,
    val language: String?
)

// Подгружаем джсон
suspend fun loadRepos(context: Context): List<Repo> =
    withContext(Dispatchers.IO) {

        val json = context.assets
            .open("github_repos.json")
            .bufferedReader()
            .use { it.readText() }

        val type = object : TypeToken<List<Repo>>() {}.type
        Gson().fromJson(json, type)
    }

//Тут интерфейс
@Composable
fun RepoSearchScreen(repos: List<Repo>) {

    var query by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<List<Repo>>(emptyList()) }
    var loading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    var searchJob by remember { mutableStateOf<Job?>(null) }

    Column(Modifier.padding(16.dp)) {

        TextField(
            value = query,
            onValueChange = {
                query = it

                searchJob?.cancel()

                searchJob = scope.launch {
                    delay(500) // debounce

                    loading = true

                    val filtered = withContext(Dispatchers.Default) {
                        repos.filter {
                            it.full_name.contains(query, ignoreCase = true)
                        }
                    }

                    result = filtered
                    loading = false
                }
            },
            label = { Text("Поиск репозиториев") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        if (loading) {
            CircularProgressIndicator()
        }

        LazyColumn {
            items(result) { repo ->
                Column(Modifier.padding(8.dp)) {
                    Text(repo.full_name, fontWeight = FontWeight.Bold)
                    Text(repo.description ?: "")
                }
            }
        }
    }
}