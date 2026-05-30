package com.example.mod6z7.presentation

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mod6z7.data.BLERepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BLEViewModel @Inject constructor(
    private val repository: BLERepository
) : ViewModel() {

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val _devices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val devices: StateFlow<List<BluetoothDevice>> = _devices.asStateFlow()

    private val _heartRate = MutableStateFlow<Int?>(null)
    val heartRate: StateFlow<Int?> = _heartRate.asStateFlow()

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val _connectedDeviceName = MutableStateFlow<String?>(null)
    val connectedDeviceName: StateFlow<String?> = _connectedDeviceName.asStateFlow()

    fun startScan() {
        _isScanning.value = true
        _devices.value = emptyList()
        viewModelScope.launch {
            repository.scanDevices().collect { device ->
                val current = _devices.value.toMutableList()
                if (current.none { it.address == device.address }) {
                    current.add(device)
                    _devices.value = current
                }
            }
        }
    }

    fun stopScan() {
        repository.stopScan()
        _isScanning.value = false
    }

    fun connectToDevice(device: BluetoothDevice) {
        stopScan()
        _connectedDeviceName.value = device.name
        viewModelScope.launch {
            repository.connectToDevice(device).collect { hr ->
                _heartRate.value = hr
                _isConnected.value = true
            }
        }
    }

    fun disconnect() {
        repository.disconnect()
        _heartRate.value = null
        _isConnected.value = false
        _connectedDeviceName.value = null
    }
}