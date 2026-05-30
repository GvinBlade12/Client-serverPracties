package com.example.mod6z2.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NobelListScreen(
    navController: NavController,
    viewModel: NobelListViewModel
) {
    val state by viewModel.state.collectAsState()
    var selectedYear by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Нобелевские премии") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Фильтры
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = selectedYear,
                    onValueChange = { selectedYear = it },
                    placeholder = { Text("Год") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                Box(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = when(selectedCategory) {
                            "" -> ""
                            "Chemistry" -> "Химия"
                            "Physics" -> "Физика"
                            "Literature" -> "Литература"
                            "Peace" -> "Мир"
                            "Physiology or Medicine" -> "Медицина"
                            "Economic Sciences" -> "Экономика"
                            else -> selectedCategory
                        },
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { Text("Категория") },
                        trailingIcon = {
                            IconButton(onClick = { expanded = !expanded }) {
                                Text("▼")
                            }
                        }
                    )

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Все категории") },
                            onClick = {
                                selectedCategory = ""
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Химия") },
                            onClick = {
                                selectedCategory = "Chemistry"
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Физика") },
                            onClick = {
                                selectedCategory = "Physics"
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Литература") },
                            onClick = {
                                selectedCategory = "Literature"
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Мир") },
                            onClick = {
                                selectedCategory = "Peace"
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Медицина") },
                            onClick = {
                                selectedCategory = "Physiology or Medicine"
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Экономика") },
                            onClick = {
                                selectedCategory = "Economic Sciences"
                                expanded = false
                            }
                        )
                    }
                }

                Button(
                    onClick = {
                        viewModel.applyFilters(
                            year = selectedYear.ifEmpty { null },
                            category = selectedCategory.ifEmpty { null }
                        )
                    }
                ) {
                    Text("Фильтр")
                }
            }

            // Список
            when (state) {
                is NobelListState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is NobelListState.Success -> {
                    val prizes = (state as NobelListState.Success).prizes
                    if (prizes.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Нет результатов по вашему запросу")
                        }
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(prizes) { prize ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 4.dp)
                                        .clickable {
                                            if (prize.laureates.isNotEmpty()) {
                                                navController.navigate("detail/${prize.laureates[0].id}/${prize.awardYear}/${prize.category}")
                                            }
                                        },
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(
                                            "${prize.awardYear} — ${prize.category.uppercase()}",
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        prize.laureates.forEach { laureate ->
                                            Text(
                                                "• ${laureate.fullName}",
                                                style = MaterialTheme.typography.bodyLarge
                                            )
                                            Text(
                                                laureate.motivation.take(100),
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                is NobelListState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text((state as NobelListState.Error).message, color = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { viewModel.loadPrizes() }) {
                                Text("Повторить")
                            }
                        }
                    }
                }
            }
        }
    }
}