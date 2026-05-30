package com.example.mod6z1.presentation.photolist
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mod6z1.domain.model.Photo
import com.example.mod6z1.domain.usecase.GetPhotosUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PhotoListViewModel(
    private val getPhotosUseCase: GetPhotosUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<PhotoListState>(PhotoListState.Loading)
    val state: StateFlow<PhotoListState> = _state.asStateFlow()

    private var allPhotos: List<Photo> = emptyList()

    init {
        loadPhotos()
    }

    fun loadPhotos() {
        viewModelScope.launch {
            _state.value = PhotoListState.Loading
            val result = getPhotosUseCase(page = 1, limit = 50)
            result.fold(
                onSuccess = { photos ->
                    allPhotos = photos
                    _state.value = PhotoListState.Success(photos)
                },
                onFailure = { exception ->
                    _state.value = PhotoListState.Error(exception.message ?: "Ошибка")
                }
            )
        }
    }
    fun getPhotoById(id: String): Photo? {
        return allPhotos.find { it.id == id }
    }
}