package com.example.prac1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.prac1.data.DiaryRepository
import com.example.prac1.ui.screens.EditScreen
import com.example.prac1.ui.screens.ListScreen
import com.example.prac1.ui.theme.Prac1Theme
import com.example.prac1.viewmodel.DiaryViewModel
import com.example.prac1.viewmodel.DiaryViewModelFactory
import androidx.navigation.compose.composable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = DiaryRepository(applicationContext)

        setContent {
            val navController = rememberNavController()

            val viewModel: DiaryViewModel = viewModel(
                factory = DiaryViewModelFactory(repository)
            )

            NavHost(navController, startDestination = "list") {

                composable("list") {
                    ListScreen(navController, viewModel)
                }

                composable("edit") {
                    EditScreen(navController, viewModel)
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Prac1Theme {
        Greeting("Android")
    }
}