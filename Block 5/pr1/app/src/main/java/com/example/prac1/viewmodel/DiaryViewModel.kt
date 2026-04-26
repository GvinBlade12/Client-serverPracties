package com.example.prac1.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.prac1.data.DiaryEntry
import com.example.prac1.data.DiaryRepository

class DiaryViewModel(private val repository: DiaryRepository) : ViewModel() {

    // Список без пересканирования
    val entries = mutableStateListOf<DiaryEntry>()

    init {
        entries.addAll(repository.loadEntries())
    }

    fun addEntry(title: String?, content: String) {
        val newEntry = repository.saveEntry(title, content)
        entries.add(0, newEntry) // добавляем в начало
    }

    fun deleteEntry(fileName: String) {
        repository.deleteEntry(fileName)
        entries.removeAll { it.fileName == fileName }
    }
}

// Factory
class DiaryViewModelFactory(private val repository: DiaryRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DiaryViewModel(repository) as T
    }
}