package com.example.prac3.presentation.ui.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.prac3.presentation.viewmodel.TodoViewModel
import com.example.prac3.presentation.ui.component.TodoItem

@Composable
fun TodoListScreen(viewModel: TodoViewModel) {

    val todos by viewModel.todos.collectAsStateWithLifecycle()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.addTodo("Новая задача") }) {
                Text("+")
            }
        }
    ) { padding ->

        LazyColumn(modifier = Modifier.padding(padding)) {
            items(todos) { todo ->
                TodoItem(
                    todo = todo,
                    onClick = { viewModel.toggle(todo) }
                )
            }
        }
    }
}