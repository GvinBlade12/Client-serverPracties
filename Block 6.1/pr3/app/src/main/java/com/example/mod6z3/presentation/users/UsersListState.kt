package com.example.mod6z3.presentation.users

import com.example.mod6z3.domain.model.User

sealed class UsersListState {
    object Loading : UsersListState()
    data class Success(val users: List<User>) : UsersListState()
    data class Error(val message: String) : UsersListState()
}