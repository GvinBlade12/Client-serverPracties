package com.example.prac3.presentation.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.prac3.domain.model.Todo

@Composable
fun TodoItem(
    todo: Todo,
    isColored: Boolean,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val background = if (todo.isDone && isColored)
        Color.Green.copy(alpha = 0.3f)
    else Color.Transparent

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(background)
            .padding(16.dp)
    ) {

        // ✅ Toggle выполненности
        Checkbox(
            checked = todo.isDone,
            onCheckedChange = { onToggle() }
        )

        Spacer(modifier = Modifier.width(8.dp))

        // ✅ Клик = редактирование
        Text(
            text = todo.title,
            modifier = Modifier
                .weight(1f)
                .clickable { onEdit() },
            color = if (todo.isDone) Color.Gray else Color.Black,
            textDecoration = if (todo.isDone)
                TextDecoration.LineThrough
            else null
        )

        // ❌ Удаление
        Text(
            "❌",
            modifier = Modifier.clickable { onDelete() }
        )
    }
}