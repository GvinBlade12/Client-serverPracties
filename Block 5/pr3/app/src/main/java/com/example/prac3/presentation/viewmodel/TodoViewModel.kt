package com.example.prac3.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prac3.data.SettingsDataStore
import com.example.prac3.domain.model.Todo
import com.example.prac3.domain.repository.TodoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TodoViewModel(
    private val repository: TodoRepository,
    private val settings: SettingsDataStore
) : ViewModel() {

    val isColored = settings.isColored
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), true)

    val todos = repository.getTodos()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    fun setColored(value: Boolean) {
        viewModelScope.launch {
            settings.setColored(value)
        }
    }

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

    // 🔥 НОВОЕ: редактирование
    fun updateTitle(todo: Todo, newTitle: String) {
        viewModelScope.launch {
            repository.update(todo.copy(title = newTitle))
        }
    }
}