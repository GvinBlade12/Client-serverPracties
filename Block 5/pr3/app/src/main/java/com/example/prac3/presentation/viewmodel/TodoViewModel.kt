package com.example.prac3.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prac3.data.local.TodoDao
import com.example.prac3.data.local.TodoEntity
import com.example.prac3.domain.model.Todo
import com.example.prac3.domain.repository.TodoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TodoViewModel(
    private val repository: TodoRepository
) : ViewModel() {

    val todos = repository.getTodos()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    fun addTodo(title: String) {
        viewModelScope.launch {
            repository.insert(
                Todo(id = 0, title = title, isDone = false)
            )
        }
    }

    fun delete(todo: Todo) {
        viewModelScope.launch {
            repository.delete(todo)
        }
    }

    fun toggle(todo: Todo) {
        viewModelScope.launch {
            repository.update(todo.copy(isDone = !todo.isDone))
        }
    }
}