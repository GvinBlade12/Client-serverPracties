package com.example.prac1.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.prac1.viewmodel.DiaryViewModel

@Composable
fun EditScreen(navController: NavController, viewModel: DiaryViewModel) {

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Заголовок (необязательно)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("Текст") },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Button(onClick = {
                navController.popBackStack()
            }) {
                Text("Назад")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(onClick = {
                viewModel.addEntry(
                    title.ifBlank { null },
                    content
                )
                navController.popBackStack()
            }) {
                Text("Сохранить")
            }
        }
    }
}