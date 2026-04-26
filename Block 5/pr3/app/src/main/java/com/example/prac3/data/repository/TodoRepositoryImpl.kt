package com.example.prac3.data.repository

import com.example.prac3.data.local.TodoDao
import com.example.prac3.data.local.TodoEntity
import com.example.prac3.domain.model.Todo
import com.example.prac3.domain.repository.TodoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TodoRepositoryImpl(
    private val dao: TodoDao
) : TodoRepository {

    override fun getTodos(): Flow<List<Todo>> =
        dao.getTodos().map { list ->
            list.map { Todo(it.id, it.title, it.isDone) }
        }

    override suspend fun insert(todo: Todo) {
        dao.insert(TodoEntity(todo.id, todo.title, todo.isDone))
    }

    override suspend fun update(todo: Todo) {
        dao.update(TodoEntity(todo.id, todo.title, todo.isDone))
    }

    override suspend fun delete(todo: Todo) {
        dao.delete(TodoEntity(todo.id, todo.title, todo.isDone))
    }
}