package com.example.prac1.data.remote

import com.example.prac1.domain.model.Photo
import kotlinx.serialization.Serializable

@Serializable
data class PhotoDto(
    val id: String,
    val author: String,
    val width: Int,
    val height: Int,
    val url: String,
    val download_url: String
)

// Функция-маппер, которая превращает DTO от сервера в нашу красивую Domain-модель
fun PhotoDto.toDomain(): Photo {
    return Photo(id, author, width, height, url, download_url)
}