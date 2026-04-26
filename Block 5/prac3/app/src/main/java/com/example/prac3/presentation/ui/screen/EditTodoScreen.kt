package com.example.prac3.presentation.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.prac3.domain.model.Todo
import com.example.prac3.presentation.viewmodel.TodoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTodoScreen(
    taskId: Long?,
    viewModel: TodoViewModel,
    onBack: () -> Unit
) {
    var currentTask by remember { mutableStateOf<Todo?>(null) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    LaunchedEffect(taskId) {
        if (taskId != null) {
            currentTask = viewModel.getTask(taskId)
            title = currentTask?.title.orEmpty()
            description = currentTask?.description.orEmpty()
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(if (taskId == null) "Новая задача" else "Редактирование") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Заголовок") }
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Описание") }
            )

            Button(
                onClick = {
                    if (taskId == null) {
                        viewModel.addTask(title, description)
                    } else {
                        currentTask?.let {
                            viewModel.updateTask(it.copy(title = title, description = description))
                        }
                    }
                    onBack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Сохранить")
            }

            if (taskId != null && currentTask != null) {
                TextButton(
                    onClick = {
                        viewModel.deleteTask(currentTask!!)
                        onBack()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Удалить")
                }
            }
        }
    }
}
