package com.example.mod6z2.presentation

sealed class NobelDetailState {
    object Loading : NobelDetailState()
    data class Success(
        val fullName: String,
        val year: String,
        val category: String,
        val motivation: String,
        val country: String
    ) : NobelDetailState()
    data class Error(val message: String) : NobelDetailState()
}