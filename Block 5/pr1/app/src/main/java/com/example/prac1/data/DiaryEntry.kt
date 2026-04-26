package com.example.prac1.data

data class DiaryEntry(
    val fileName: String,
    val title: String?,
    val content: String,
    val timestamp: Long
)
// Модель одной записи дневника