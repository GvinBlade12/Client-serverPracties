package com.example.mod6z2.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mod6z2.domain.usecase.GetNobelPrizesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NobelDetailViewModel(
    private val getNobelPrizesUseCase: GetNobelPrizesUseCase,
    private val laureateId: String,
    private val prizeYear: String,
    private val category: String
) : ViewModel() {

    private val _state = MutableStateFlow<NobelDetailState>(NobelDetailState.Loading)
    val state: StateFlow<NobelDetailState> = _state.asStateFlow()

    init {
        loadLaureate()
    }

    fun loadLaureate() {
        viewModelScope.launch {
            _state.value = NobelDetailState.Loading
            val result = getNobelPrizesUseCase(prizeYear, category)
            result.fold(
                onSuccess = { prizes ->
                    val prize = prizes.firstOrNull()
                    val laureate = prize?.laureates?.firstOrNull { it.id == laureateId }
                    if (laureate != null) {
                        _state.value = NobelDetailState.Success(
                            fullName = laureate.fullName,
                            year = prize.awardYear,
                            category = prize.category,
                            motivation = laureate.motivation,
                            country = "Информация о стране не указана"
                        )
                    } else {
                        _state.value = NobelDetailState.Error("Лауреат не найден")
                    }
                },
                onFailure = { exception ->
                    _state.value = NobelDetailState.Error(exception.message ?: "Ошибка загрузки")
                }
            )
        }
    }
}