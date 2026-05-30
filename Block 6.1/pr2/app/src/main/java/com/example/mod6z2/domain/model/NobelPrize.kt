package com.example.mod6z2.domain.model

data class NobelPrize(
    val awardYear: String,
    val category: String,
    val laureates: List<Laureate>
)