package com.example.prac1.data

import android.content.Context
import java.io.File

class DiaryRepository(private val context: Context) {

    private val dir = context.filesDir

    // Загрузка всех записей (один раз при старте)
    fun loadEntries(): List<DiaryEntry> {
        return dir.listFiles()?.mapNotNull { file ->
            val text = file.readText()
            val lines = text.split("\n", limit = 2)

            val title = lines.getOrNull(0)?.takeIf { it.startsWith("TITLE:") }
                ?.removePrefix("TITLE:")

            val content = if (title != null) lines.getOrNull(1) ?: "" else text

            val timestamp = file.name.substringBefore("_").toLongOrNull() ?: 0L

            DiaryEntry(
                fileName = file.name,
                title = title,
                content = content,
                timestamp = timestamp
            )
        }?.sortedByDescending { it.timestamp } ?: emptyList()
    }

    // Сохранение новой записи
    fun saveEntry(title: String?, content: String): DiaryEntry {
        val timestamp = System.currentTimeMillis()
        val safeTitle = title?.replace(" ", "_") ?: ""

        val fileName = if (safeTitle.isNotEmpty()) {
            "${timestamp}_$safeTitle.txt"
        } else {
            "${timestamp}.txt"
        }

        val file = File(dir, fileName)

        val textToSave = if (!title.isNullOrEmpty()) {
            "TITLE:$title\n$content"
        } else content

        file.writeText(textToSave)

        return DiaryEntry(fileName, title, content, timestamp)
    }

    // Удаление
    fun deleteEntry(fileName: String) {
        File(dir, fileName).delete()
    }

    // Чтение
    fun readEntry(fileName: String): DiaryEntry? {
        val file = File(dir, fileName)
        if (!file.exists()) return null

        val text = file.readText()
        val lines = text.split("\n", limit = 2)

        val title = lines.getOrNull(0)?.takeIf { it.startsWith("TITLE:") }
            ?.removePrefix("TITLE:")

        val content = if (title != null) lines.getOrNull(1) ?: "" else text
        val timestamp = fileName.substringBefore("_").toLongOrNull() ?: 0L

        return DiaryEntry(fileName, title, content, timestamp)
    }
}