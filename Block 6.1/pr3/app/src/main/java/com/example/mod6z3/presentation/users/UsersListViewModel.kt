package com.example.mod6z3.presentation.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mod6z3.domain.model.User
import com.example.mod6z3.domain.usecase.GetUsersUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UsersListViewModel(
    private val getUsersUseCase: GetUsersUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<UsersListState>(UsersListState.Loading)
    val state: StateFlow<UsersListState> = _state.asStateFlow()

    init {
        loadUsers()
    }

    fun loadUsers() {
        viewModelScope.launch {
            _state.value = UsersListState.Loading
            val result = getUsersUseCase()
            result.fold(
                onSuccess = { users ->
                    _state.value = UsersListState.Success(users)
                },
                onFailure = { exception ->
                    _state.value = UsersListState.Error(exception.message ?: "Ошибка загрузки пользователей")
                }
            )
        }
    }
}