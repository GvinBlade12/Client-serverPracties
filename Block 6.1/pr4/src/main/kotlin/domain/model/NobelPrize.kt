package com.example.nobelapi.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class NobelPrize(
    val awardYear: String,
    val category: String,
    val prizeAmount: Int,
    val laureates: List<Laureate>
)

@Serializable
data class Laureate(
    val id: String,
    val fullName: String,
    val motivation: String
)