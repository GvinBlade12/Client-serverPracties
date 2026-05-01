package com.example.prac3

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.prac3.data.SettingsDataStore
import com.example.prac3.data.local.TodoDao
import com.example.prac3.data.local.TodoDatabase
import com.example.prac3.data.local.TodoEntity
import com.example.prac3.data.repository.TodoRepositoryImpl
import com.example.prac3.presentation.ui.screen.TodoListScreen
import com.example.prac3.presentation.viewmodel.TodoViewModel
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = Room.databaseBuilder(
            applicationContext,
            TodoDatabase::class.java,
            "todo_db"
        ).build()

        lifecycleScope.launch(Dispatchers.IO) {
            importFromJson(applicationContext, db.todoDao())
        }

        val repo = TodoRepositoryImpl(db.todoDao())
        val settings = SettingsDataStore(applicationContext)
        val viewModel = TodoViewModel(repo, settings)

        setContent {
            TodoListScreen(viewModel)
        }
    }
}

data class TodoJson(
    val title: String,
    val isDone: Boolean
)

suspend fun importFromJson(context: Context, dao: TodoDao) {
    val prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
    val isImported = prefs.getBoolean("imported", false)

    if (isImported) return

    val json = context.assets.open("todos.json")
        .bufferedReader()
        .use { it.readText() }

    val list = Gson().fromJson(json, Array<TodoJson>::class.java)

    list.forEach {
        dao.insert(TodoEntity(title = it.title, isDone = it.isDone))
    }

    prefs.edit().putBoolean("imported", true).apply()
}