package com.example.prac3.domain.repository

import com.example.prac3.domain.model.Todo
import kotlinx.coroutines.flow.Flow

interface TodoRepository {
    fun observeTodos(): Flow<List<Todo>>
    suspend fun getTodo(id: Long): Todo?
    suspend fun addTodo(title: String, description: String)
    suspend fun updateTodo(todo: Todo)
    suspend fun deleteTodo(todo: Todo)
    suspend fun ensureImportedFromJson()
}
