package com.example.prac3.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.prac3.data.local.AppDatabase
import com.example.prac3.data.repository.SettingsRepositoryImpl
import com.example.prac3.data.repository.TodoRepositoryImpl
import com.example.prac3.data.repository.preferences.SettingsDataStore
import com.example.prac3.domain.model.Todo
import com.example.prac3.domain.usecase.AddTodoUseCase
import com.example.prac3.domain.usecase.DeleteTodoUseCase
import com.example.prac3.domain.usecase.EnsureImportedUseCase
import com.example.prac3.domain.usecase.GetTodoUseCase
import com.example.prac3.domain.usecase.ObserveCompletedColorUseCase
import com.example.prac3.domain.usecase.ObserveTodosUseCase
import com.example.prac3.domain.usecase.SetCompletedColorUseCase
import com.example.prac3.domain.usecase.UpdateTodoUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TodoViewModel(application: Application) : AndroidViewModel(application) {
    private val db = Room.databaseBuilder(
        application,
        AppDatabase::class.java,
        "todo.db"
    ).build()

    private val settingsDataStore = SettingsDataStore(application)
    private val todoRepository = TodoRepositoryImpl(application, db.todoDao(), settingsDataStore)
    private val settingsRepository = SettingsRepositoryImpl(settingsDataStore)

    private val observeTodos = ObserveTodosUseCase(todoRepository)
    private val getTodo = GetTodoUseCase(todoRepository)
    private val addTodo = AddTodoUseCase(todoRepository)
    private val updateTodo = UpdateTodoUseCase(todoRepository)
    private val deleteTodo = DeleteTodoUseCase(todoRepository)
    private val ensureImported = EnsureImportedUseCase(todoRepository)
    private val observeCompletedColor = ObserveCompletedColorUseCase(settingsRepository)
    private val setCompletedColor = SetCompletedColorUseCase(settingsRepository)

    val todos: StateFlow<List<Todo>> = observeTodos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val completedColorEnabled: StateFlow<Boolean> = observeCompletedColor()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    init {
        viewModelScope.launch { ensureImported() }
    }

    fun addTask(title: String, description: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            addTodo(title.trim(), description.trim())
        }
    }

    fun updateTask(todo: Todo) {
        viewModelScope.launch { updateTodo(todo) }
    }

    fun deleteTask(todo: Todo) {
        viewModelScope.launch { deleteTodo(todo) }
    }

    fun toggleCompleted(todo: Todo) {
        viewModelScope.launch {
            updateTodo(todo.copy(isCompleted = !todo.isCompleted))
        }
    }

    fun setCompletedColorEnabled(enabled: Boolean) {
        viewModelScope.launch { setCompletedColor(enabled) }
    }

    suspend fun getTask(id: Long): Todo? = getTodo(id)
}
