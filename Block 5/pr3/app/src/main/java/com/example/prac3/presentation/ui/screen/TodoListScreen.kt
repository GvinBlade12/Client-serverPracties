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
import com.example.prac3.domain.model.Todo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreen(viewModel: TodoViewModel) {

    val todos by viewModel.todos.collectAsStateWithLifecycle()
    val isColored by viewModel.isColored.collectAsStateWithLifecycle()

    var editingTodo by remember { mutableStateOf<Todo?>(null) }
    var newText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Todo") },
                actions = {
                    Switch(
                        checked = isColored,
                        onCheckedChange = { viewModel.setColored(it) }
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                viewModel.addTodo("Новая задача")
            }) {
                Text("+")
            }
        }
    ) { padding ->

        LazyColumn(modifier = Modifier.padding(padding)) {
            items(todos) { todo ->
                TodoItem(
                    todo = todo,
                    isColored = isColored,
                    onToggle = { viewModel.toggle(todo) },
                    onEdit = {
                        editingTodo = todo
                        newText = todo.title
                    },
                    onDelete = { viewModel.delete(todo) }
                )
            }
        }

        // 🔥 Диалог редактирования
        if (editingTodo != null) {
            AlertDialog(
                onDismissRequest = { editingTodo = null },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.updateTitle(editingTodo!!, newText)
                        editingTodo = null
                    }) {
                        Text("Сохранить")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { editingTodo = null }) {
                        Text("Отмена")
                    }
                },
                text = {
                    TextField(
                        value = newText,
                        onValueChange = { newText = it }
                    )
                }
            )
        }
    }
}