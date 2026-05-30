package com.example.prac1.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prac1.data.remote.RetrofitInstance
import com.example.prac1.domain.repository.PhotoRepository
import com.example.prac1.domain.repository.PhotoRepositoryImpl

import kotlinx.coroutines.launch

class PhotoViewModel : ViewModel() {
    // В реальном проекте тут используют DI (Hilt), но для лабы сделаем напрямую, чтобы не усложнять
    private val repository: PhotoRepository = PhotoRepositoryImpl(RetrofitInstance.api)

    private val _state = mutableStateOf<PhotoState>(PhotoState.Loading)
    val state: State<PhotoState> = _state

    init {
        loadPhotos()
    }

    fun loadPhotos() {
        viewModelScope.launch {
            _state.value = PhotoState.Loading
            try {
                val photos = repository.getPhotos()
                _state.value = PhotoState.Success(photos)
            } catch (e: Exception) {
                _state.value = PhotoState.Error(e.localizedMessage ?: "Неизвестная ошибка")
            }
        }
    }
}