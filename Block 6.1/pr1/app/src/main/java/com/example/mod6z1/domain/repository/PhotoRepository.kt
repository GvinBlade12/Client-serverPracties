package com.example.mod6z1.domain.repository
import com.example.mod6z1.domain.model.Photo

interface PhotoRepository {
    suspend fun getPhotos(page: Int, limit: Int): Result<List<Photo>>
}