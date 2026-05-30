package com.example.mod6z3.presentation.userdetail

import com.example.mod6z3.domain.model.User

sealed class UserDetailState {
    object Loading : UserDetailState()
    data class Success(val user: User) : UserDetailState()
    data class Error(val message: String) : UserDetailState()
}