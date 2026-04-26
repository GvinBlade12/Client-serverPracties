package com.example.prac3.presentation.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.prac3.domain.model.Todo

@Composable
fun TodoItem(
    todo: Todo,
    colorCompleted: Boolean,
    onToggle: () -> Unit,
    onClick: () -> Unit
) {
    val background = if (todo.isCompleted && colorCompleted) {
        MaterialTheme.colorScheme.secondaryContainer
    } else {
        MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .background(background)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Checkbox(checked = todo.isCompleted, onCheckedChange = { onToggle() })
            Column {
                Text(text = todo.title, style = MaterialTheme.typography.titleMedium)
                Text(text = todo.description, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
