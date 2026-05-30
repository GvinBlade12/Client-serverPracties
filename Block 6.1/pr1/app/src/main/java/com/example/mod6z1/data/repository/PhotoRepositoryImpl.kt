package com.example.mod6z1.data.repository
import com.example.mod6z1.data.api.PhotoApiService
import com.example.mod6z1.data.model.PhotoDto
import com.example.mod6z1.domain.model.Photo
import com.example.mod6z1.domain.repository.PhotoRepository
import java.io.IOException

class PhotoRepositoryImpl(
    private val apiService: PhotoApiService
) : PhotoRepository {
    override suspend fun getPhotos(page: Int, limit: Int): Result<List<Photo>> {
        return try {
            val photosDto = apiService.getPhotos(page, limit)
            val photos = photosDto.map { dto ->
                Photo(
                    id = dto.id,
                    author = dto.author,
                    width = dto.width,
                    height = dto.height,
                    url = dto.url,
                    downloadUrl = dto.downloadUrl
                )
            }
            Result.success(photos)
        } catch (e: IOException) {
            Result.failure(Exception("Ошибка сети: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(Exception("Ошибка загрузки: ${e.message}"))
        }
    }
}