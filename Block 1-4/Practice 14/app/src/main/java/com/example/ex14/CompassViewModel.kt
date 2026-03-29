package com.example.ex14

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CompassViewModel : ViewModel() {

    private val _azimuth = MutableStateFlow(0)
    val azimuth: StateFlow<Int> = _azimuth

    private val _sensorError = MutableStateFlow(false)
    val sensorError: StateFlow<Boolean> = _sensorError

    fun updateAzimuth(value: Int) {
        _azimuth.value = value
    }

    fun setSensorError() {
        _sensorError.value = true
    }
}