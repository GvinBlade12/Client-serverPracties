package com.example.mod6z3.presentation.login

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.mod6z3.data.storage.TokenDataStore
import com.example.mod6z3.domain.usecase.LoginUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val tokenDataStore: TokenDataStore,
    private val navController: NavController
) : ViewModel() {

    private val _state = MutableStateFlow<LoginState>(LoginState.Idle)
    val state: StateFlow<LoginState> = _state.asStateFlow()

    val username = mutableStateOf("")
    val password = mutableStateOf("")

    fun login() {
        viewModelScope.launch {
            _state.value = LoginState.Loading
            val result = loginUseCase(username.value, password.value)
            result.fold(
                onSuccess = { loginData ->
                    _state.value = LoginState.Success("Добро пожаловать, ${loginData.user.firstName}!")
                    navController.navigate("users") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onFailure = { exception ->
                    val errorMessage = when {
                        exception.message?.contains("401") == true -> "Неверное имя пользователя или пароль"
                        exception.message?.contains("timeout") == true -> "Нет соединения с сервером"
                        else -> "Ошибка: ${exception.message}"
                    }
                    _state.value = LoginState.Error(errorMessage)
                }
            )
        }
    }
}