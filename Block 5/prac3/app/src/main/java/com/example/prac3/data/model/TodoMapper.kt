package com.example.prac3.data.model

import com.example.prac3.data.local.TodoEntity
import com.example.prac3.domain.model.Todo

fun TodoEntity.toDomain(): Todo = Todo(
    id = id,
    title = title,
    description = description,
    isCompleted = isCompleted
)

fun Todo.toEntity(): TodoEntity = TodoEntity(
    id = id,
    title = title,
    description = description,
    isCompleted = isCompleted
)

fun TodoJsonModel.toEntity(): TodoEntity = TodoEntity(
    title = title,
    description = description,
    isCompleted = isCompleted
)
