package com.example.mod6z1.domain.usecase
import com.example.mod6z1.domain.model.Photo
import com.example.mod6z1.domain.repository.PhotoRepository

class GetPhotosUseCase(
    private val repository: PhotoRepository
) {
    suspend operator fun invoke(page: Int = 1, limit: Int = 30): Result<List<Photo>> {
        return repository.getPhotos(page, limit)
    }
}