package com.example.prac3.data.repository

import android.content.Context
import com.example.prac3.data.local.TodoDao
import com.example.prac3.data.model.TodoJsonModel
import com.example.prac3.data.model.toDomain
import com.example.prac3.data.model.toEntity
import com.example.prac3.domain.model.Todo
import com.example.prac3.domain.repository.TodoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import com.example.prac3.data.model.toEntity as jsonToEntity
import com.example.prac3.data.repository.preferences.SettingsDataStore

class TodoRepositoryImpl(
    private val context: Context,
    private val todoDao: TodoDao,
    private val settingsDataStore: SettingsDataStore
) : TodoRepository {

    override fun observeTodos(): Flow<List<Todo>> =
        todoDao.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun getTodo(id: Long): Todo? = todoDao.getById(id)?.toDomain()

    override suspend fun addTodo(title: String, description: String) {
        todoDao.insert(Todo(title = title, description = description).toEntity())
    }

    override suspend fun updateTodo(todo: Todo) {
        todoDao.update(todo.toEntity())
    }

    override suspend fun deleteTodo(todo: Todo) {
        todoDao.delete(todo.toEntity())
    }

    override suspend fun ensureImportedFromJson() {
        val imported = settingsDataStore.isJsonImported.first()
        if (imported || todoDao.getCount() > 0) return

        val json = context.assets.open("tasks.json").bufferedReader().use { it.readText() }
        val parsed = parseJson(json)
        todoDao.insertAll(parsed.map { it.jsonToEntity() })
        settingsDataStore.setJsonImported(true)
    }

    private fun parseJson(raw: String): List<TodoJsonModel> {
        val array = JSONArray(raw)
        return buildList {
            for (i in 0 until array.length()) {
                val item = array.getJSONObject(i)
                add(
                    TodoJsonModel(
                        title = item.optString("title"),
                        description = item.optString("description"),
                        isCompleted = item.optBoolean("isCompleted", false)
                    )
                )
            }
        }
    }
}
