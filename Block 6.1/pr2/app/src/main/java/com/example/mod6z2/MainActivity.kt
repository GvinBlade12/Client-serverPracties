package com.example.mod6z2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mod6z2.data.api.NobelApiService
import com.example.mod6z2.data.repository.NobelRepositoryImpl
import com.example.mod6z2.data.storage.TokenDataStore
import com.example.mod6z2.domain.usecase.GetNobelPrizesUseCase
import com.example.mod6z2.domain.usecase.LoginUseCase
import com.example.mod6z2.presentation.NobelListScreen
import com.example.mod6z2.presentation.NobelListViewModel
import com.example.mod6z2.presentation.NobelDetailScreen
import com.example.mod6z2.presentation.NobelDetailViewModel
import com.example.mod6z2.presentation.login.LoginScreen
import com.example.mod6z2.presentation.login.LoginViewModel
import com.example.mod6z2.ui.theme.Mod6z2Theme
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
class MainActivity : ComponentActivity() {

    private lateinit var tokenDataStore: TokenDataStore
    private lateinit var repository: NobelRepositoryImpl
    private lateinit var getNobelPrizesUseCase: GetNobelPrizesUseCase
    private lateinit var loginUseCase: LoginUseCase

    private val httpClient = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
        install(Logging) {
            level = LogLevel.ALL
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 30000
            connectTimeoutMillis = 15000
            socketTimeoutMillis = 15000
        }
    }
    private val apiService by lazy { NobelApiService(httpClient) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tokenDataStore = TokenDataStore(applicationContext)
        repository = NobelRepositoryImpl(apiService, tokenDataStore)
        getNobelPrizesUseCase = GetNobelPrizesUseCase(repository)
        loginUseCase = LoginUseCase(repository)

        setContent {
            Mod6z2Theme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "login") {
                        composable("login") {
                            val viewModel: LoginViewModel = viewModel {
                                LoginViewModel(loginUseCase, tokenDataStore, navController)
                            }
                            LoginScreen(navController = navController, viewModel = viewModel)
                        }
                        composable("list") {
                            val viewModel: NobelListViewModel = viewModel {
                                NobelListViewModel(getNobelPrizesUseCase)
                            }
                            NobelListScreen(navController = navController, viewModel = viewModel)
                        }
                        composable("detail/{laureateId}/{prizeYear}/{category}") { backStackEntry ->
                            val laureateId = backStackEntry.arguments?.getString("laureateId") ?: ""
                            val prizeYear = backStackEntry.arguments?.getString("prizeYear") ?: ""
                            val category = backStackEntry.arguments?.getString("category") ?: ""
                            val detailViewModel: NobelDetailViewModel = viewModel {
                                NobelDetailViewModel(getNobelPrizesUseCase, laureateId, prizeYear, category)
                            }
                            NobelDetailScreen(navController = navController, viewModel = detailViewModel)
                        }
                    }
                }
            }
        }
    }
}