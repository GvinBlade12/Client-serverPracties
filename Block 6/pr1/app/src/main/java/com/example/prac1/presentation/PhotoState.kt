package com.example.prac1.presentation

import com.example.prac1.domain.model.Photo

sealed interface PhotoState {
    object Loading : PhotoState
    data class Success(val photos: List<Photo>) : PhotoState
    data class Error(val message: String) : PhotoState
}