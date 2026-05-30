package com.example.mod6z2.presentation.login

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.mod6z2.data.storage.TokenDataStore
import com.example.mod6z2.domain.usecase.LoginUseCase
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
                onSuccess = { token ->
                    _state.value = LoginState.Success("Добро пожаловать!")
                    navController.navigate("list") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onFailure = { exception ->
                    _state.value = LoginState.Error("Неверные данные или сервер недоступен")
                }
            )
        }
    }
}