package com.example.prac1.domain.repository

import com.example.prac1.domain.model.Photo

interface PhotoRepository {
    suspend fun getPhotos(): List<Photo>
}