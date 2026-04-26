package com.example.prac3.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.prac3.presentation.ui.screen.EditTodoScreen
import com.example.prac3.presentation.ui.screen.TodoListScreen
import com.example.prac3.presentation.viewmodel.TodoViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val vm: TodoViewModel = viewModel()

    NavHost(navController = navController, startDestination = "list") {
        composable("list") {
            TodoListScreen(
                viewModel = vm,
                onAddClick = { navController.navigate("edit") },
                onEditClick = { id -> navController.navigate("edit/$id") }
            )
        }

        composable("edit") {
            EditTodoScreen(taskId = null, viewModel = vm) { navController.popBackStack() }
        }

        composable(
            route = "edit/{id}",
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id")
            EditTodoScreen(taskId = id, viewModel = vm) { navController.popBackStack() }
        }
    }
}
