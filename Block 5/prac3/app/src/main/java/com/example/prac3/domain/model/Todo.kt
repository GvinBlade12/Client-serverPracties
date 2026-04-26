package com.example.prac3.domain.model

data class Todo(
    val id: Long = 0,
    val title: String,
    val description: String,
    val isCompleted: Boolean = false
)
