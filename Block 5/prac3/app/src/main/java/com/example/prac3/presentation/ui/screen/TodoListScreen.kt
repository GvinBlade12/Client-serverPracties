package com.example.prac3.presentation.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.prac3.presentation.ui.component.TodoItem
import com.example.prac3.presentation.viewmodel.TodoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreen(
    viewModel: TodoViewModel,
    onAddClick: () -> Unit,
    onEditClick: (Long) -> Unit
) {
    val todos = viewModel.todos.value
    val completedColor = viewModel.completedColorEnabled.value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TodoList") },
                actions = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Цвет завершенных")
                        Switch(
                            checked = completedColor,
                            onCheckedChange = viewModel::setCompletedColorEnabled
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = "Добавить")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(todos, key = { it.id }) { todo ->
                TodoItem(
                    todo = todo,
                    colorCompleted = completedColor,
                    onToggle = { viewModel.toggleCompleted(todo) },
                    onClick = { onEditClick(todo.id) }
                )
            }
        }
    }
}
