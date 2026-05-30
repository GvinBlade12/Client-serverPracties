package com.example.mod6z2.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mod6z2.domain.model.NobelPrize
import com.example.mod6z2.domain.usecase.GetNobelPrizesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NobelListViewModel(
    private val getNobelPrizesUseCase: GetNobelPrizesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<NobelListState>(NobelListState.Loading)
    val state: StateFlow<NobelListState> = _state.asStateFlow()

    private var allPrizes: List<NobelPrize> = emptyList()

    init {
        loadPrizes()
    }

    fun loadPrizes() {
        viewModelScope.launch {
            _state.value = NobelListState.Loading
            val result = getNobelPrizesUseCase(null, null)
            result.fold(
                onSuccess = { prizes ->
                    allPrizes = prizes
                    _state.value = NobelListState.Success(prizes)
                },
                onFailure = { exception ->
                    _state.value = NobelListState.Error(exception.message ?: "Ошибка загрузки")
                }
            )
        }
    }

    fun applyFilters(year: String?, category: String?) {
        val filtered = allPrizes.filter { prize ->
            var matches = true
            if (!year.isNullOrEmpty()) {
                matches = matches && prize.awardYear.contains(year)
            }
            if (!category.isNullOrEmpty()) {
                matches = matches && prize.category.equals(category, ignoreCase = true)
            }
            matches
        }
        _state.value = NobelListState.Success(filtered)
    }
}