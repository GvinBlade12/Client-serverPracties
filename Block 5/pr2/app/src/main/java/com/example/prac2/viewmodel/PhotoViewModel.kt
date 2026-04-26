package com.example.prac2.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.prac2.data.PhotoItem
import com.example.prac2.data.PhotoRepository
import java.io.File

class PhotoViewModel(private val repository: PhotoRepository) : ViewModel() {

    val photos = mutableStateListOf<PhotoItem>()

    init {
        photos.addAll(repository.loadPhotos())
    }

    fun createFile(): File {
        return repository.createImageFile()
    }

    fun addPhoto(file: File) {
        photos.add(0, PhotoItem(file))
    }

    fun reload() {
        photos.clear()
        photos.addAll(repository.loadPhotos())
    }
}

class PhotoViewModelFactory(private val repository: PhotoRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PhotoViewModel(repository) as T
    }
}