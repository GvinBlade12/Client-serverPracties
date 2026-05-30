package com.example.mod6z3
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
import com.example.mod6z3.data.api.AuthApiService
import com.example.mod6z3.data.repository.AuthRepositoryImpl
import com.example.mod6z3.data.repository.UserRepositoryImpl
import com.example.mod6z3.data.storage.TokenDataStore
import com.example.mod6z3.domain.usecase.LoginUseCase
import com.example.mod6z3.domain.usecase.GetUsersUseCase
import com.example.mod6z3.domain.usecase.GetUserDetailUseCase
import com.example.mod6z3.presentation.login.LoginScreen
import com.example.mod6z3.presentation.login.LoginViewModel
import com.example.mod6z3.presentation.users.UsersListScreen
import com.example.mod6z3.presentation.users.UsersListViewModel
import com.example.mod6z3.presentation.userdetail.UserDetailScreen
import com.example.mod6z3.presentation.userdetail.UserDetailViewModel
import com.example.mod6z3.ui.theme.Mod6z3Theme
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class MainActivity : ComponentActivity() {

    private val tokenDataStore by lazy { TokenDataStore(applicationContext) }

    private val httpClient by lazy {
        HttpClient(Android) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
            engine {
                connectTimeout = 15000
                socketTimeout = 15000
            }
        }
    }

    private val apiService by lazy { AuthApiService(httpClient) }
    private val authRepository by lazy { AuthRepositoryImpl(apiService, tokenDataStore) }
    private val userRepository by lazy { UserRepositoryImpl(apiService, tokenDataStore) }
    private val loginUseCase by lazy { LoginUseCase(authRepository) }
    private val getUsersUseCase by lazy { GetUsersUseCase(userRepository) }
    private val getUserDetailUseCase by lazy { GetUserDetailUseCase(userRepository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Mod6z3Theme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "login") {
                        composable("login") {
                            val viewModel: LoginViewModel = viewModel {
                                LoginViewModel(loginUseCase, tokenDataStore, navController)
                            }
                            LoginScreen(navController = navController, viewModel = viewModel)
                        }
                        composable("users") {
                            val viewModel: UsersListViewModel = viewModel {
                                UsersListViewModel(getUsersUseCase)
                            }
                            UsersListScreen(navController = navController, viewModel = viewModel)
                        }
                        composable("user_detail/{userId}") { backStackEntry ->
                            val userId = backStackEntry.arguments?.getString("userId") ?: ""
                            val viewModel: UserDetailViewModel = viewModel {
                                UserDetailViewModel(getUserDetailUseCase, userId)
                            }
                            UserDetailScreen(navController = navController, viewModel = viewModel, tokenDataStore = tokenDataStore)
                        }
                    }
                }
            }
        }
    }
}