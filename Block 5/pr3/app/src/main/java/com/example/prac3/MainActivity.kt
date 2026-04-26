package com.example.prac3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.room.Room
import com.example.prac3.data.local.TodoDatabase
import com.example.prac3.data.repository.TodoRepositoryImpl
import com.example.prac3.presentation.ui.screen.TodoListScreen
import com.example.prac3.presentation.viewmodel.TodoViewModel
import com.example.prac3.ui.theme.Prac3Theme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 🔥 создаём базу и всё остальное ВНЕ Compose
        val db = Room.databaseBuilder(
            applicationContext,
            TodoDatabase::class.java,
            "todo_db"
        ).build()

        val repo = TodoRepositoryImpl(db.todoDao())
        val viewModel = TodoViewModel(repo)

        setContent {
            TodoListScreen(viewModel)
        }
    }
}