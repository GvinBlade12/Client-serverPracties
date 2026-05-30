package com.example.mod6z2.presentation

import com.example.mod6z2.domain.model.NobelPrize

sealed class NobelListState {
    object Loading : NobelListState()
    data class Success(val prizes: List<NobelPrize>) : NobelListState()
    data class Error(val message: String) : NobelListState()
}