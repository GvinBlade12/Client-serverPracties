package com.example.prac1.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.prac1.viewmodel.DiaryViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ListScreen(navController: NavController, viewModel: DiaryViewModel) {

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate("edit")
            }) {
                Text("+")
            }
        }
    ) { padding ->

        if (viewModel.entries.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                verticalArrangement = Arrangement.Center
            ) {
                Text("У вас пока нет записей")
                Text("Нажмите +, чтобы создать первую")
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding)) {
                items(viewModel.entries) { entry ->
                    EntryItem(entry, viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EntryItem(entry: com.example.prac1.data.DiaryEntry, viewModel: DiaryViewModel) {

    var expanded by remember { mutableStateOf(false) }

    val date = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        .format(Date(entry.timestamp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .combinedClickable(
                onClick = { /* можно открыть экран редактирования */ },
                onLongClick = { expanded = true }
            )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = entry.title ?: "Без заголовка")
            Text(text = date)
            Text(text = entry.content.take(40))
        }
    }

    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
        DropdownMenuItem(
            text = { Text("Удалить") },
            onClick = {
                viewModel.deleteEntry(entry.fileName)
                expanded = false
            }
        )
    }
}