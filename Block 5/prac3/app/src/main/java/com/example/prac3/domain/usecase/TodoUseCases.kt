package com.example.prac3.domain.usecase

import com.example.prac3.domain.model.Todo
import com.example.prac3.domain.repository.TodoRepository
import kotlinx.coroutines.flow.Flow

class ObserveTodosUseCase(private val repository: TodoRepository) {
    operator fun invoke(): Flow<List<Todo>> = repository.observeTodos()
}

class GetTodoUseCase(private val repository: TodoRepository) {
    suspend operator fun invoke(id: Long): Todo? = repository.getTodo(id)
}

class AddTodoUseCase(private val repository: TodoRepository) {
    suspend operator fun invoke(title: String, description: String) = repository.addTodo(title, description)
}

class UpdateTodoUseCase(private val repository: TodoRepository) {
    suspend operator fun invoke(todo: Todo) = repository.updateTodo(todo)
}

class DeleteTodoUseCase(private val repository: TodoRepository) {
    suspend operator fun invoke(todo: Todo) = repository.deleteTodo(todo)
}

class EnsureImportedUseCase(private val repository: TodoRepository) {
    suspend operator fun invoke() = repository.ensureImportedFromJson()
}
