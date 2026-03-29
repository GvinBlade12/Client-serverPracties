package com.example.ex13

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class CurrencyViewModel : ViewModel() {

    private val _rate = MutableStateFlow(90.5)
    val rate: StateFlow<Double> = _rate

    init {
        startAutoUpdates()
    }

    private fun startAutoUpdates() {
        viewModelScope.launch {
            while (true) {
                delay(5000)
                generateNewRate()
            }
        }
    }

    fun refreshNow() {
        generateNewRate()
    }

    private fun generateNewRate() {
        val change = Random.nextDouble(-2.0, 2.0)
        _rate.value = (90.5 + change)
    }
}