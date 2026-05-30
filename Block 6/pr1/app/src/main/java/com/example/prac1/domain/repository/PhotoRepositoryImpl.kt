package com.example.prac1.domain.repository

import com.example.prac1.data.remote.PicsumApi
import com.example.prac1.data.remote.toDomain
import com.example.prac1.domain.model.Photo
import com.example.prac1.domain.repository.PhotoRepository


class PhotoRepositoryImpl(private val api: PicsumApi) : PhotoRepository {
    override suspend fun getPhotos(): List<Photo> {
        return api.getPhotos().map { it.toDomain() }
    }
}