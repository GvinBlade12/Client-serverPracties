package com.example.mod6z2.data.dto
import kotlinx.serialization.Serializable

@Serializable
data class NobelPrizeDto(
    val awardYear: String,
    val category: CategoryDto,
    val laureates: List<LaureateDto>?
)

@Serializable
data class CategoryDto(
    val en: String
)

@Serializable
data class LaureateDto(
    val id: String,
    val fullName: FullNameDto,
    val motivation: MotivationDto?
)

@Serializable
data class FullNameDto(
    val en: String
)

@Serializable
data class MotivationDto(
    val en: String
)