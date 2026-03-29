package com.example.ex12

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: FactsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FactsScreen(viewModel)
        }
    }
}

@Composable
fun FactsScreen(viewModel: FactsViewModel) {
    val scope = rememberCoroutineScope()

    var fact by rememberSaveable { mutableStateOf<String?>(null) }
    var loading by rememberSaveable { mutableStateOf(false) }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Button(
                onClick = {
                    scope.launch {
                        loading = true
                        fact = null

                        viewModel.getRandomFact().collect {
                            fact = it
                            loading = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Новый факт!", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (loading) {
                CircularProgressIndicator()
            }

            AnimatedVisibility(
                visible = fact != null,
                enter = fadeIn()
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Text(
                        text = fact ?: "",
                        modifier = Modifier.padding(24.dp),
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}