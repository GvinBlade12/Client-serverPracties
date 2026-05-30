package com.example.mod6z3.presentation.userdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mod6z3.domain.usecase.GetUserDetailUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserDetailViewModel(
    private val getUserDetailUseCase: GetUserDetailUseCase,
    private val userId: String
) : ViewModel() {

    private val _state = MutableStateFlow<UserDetailState>(UserDetailState.Loading)
    val state: StateFlow<UserDetailState> = _state.asStateFlow()

    init {
        loadUserDetail()
    }

    fun loadUserDetail() {
        viewModelScope.launch {
            _state.value = UserDetailState.Loading
            val result = getUserDetailUseCase(userId)
            result.fold(
                onSuccess = { user ->
                    _state.value = UserDetailState.Success(user)
                },
                onFailure = { exception ->
                    _state.value = UserDetailState.Error(exception.message ?: "Ошибка загрузки")
                }
            )
        }
    }
}