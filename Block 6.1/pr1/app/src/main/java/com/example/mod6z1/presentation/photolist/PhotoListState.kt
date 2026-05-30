package com.example.mod6z1.presentation.photolist
import com.example.mod6z1.domain.model.Photo

sealed class PhotoListState {
    object Loading : PhotoListState()
    data class Success(val photos: List<Photo>) : PhotoListState()
    data class Error(val message: String) : PhotoListState()
}