package com.example.mod6z7.domain.model

import android.bluetooth.BluetoothDevice

data class BLEDevice(
    val bluetoothDevice: BluetoothDevice? = null,
    val name: String,
    val address: String
)

enum class ConnectionState {
    Disconnected,
    Connecting,
    Connected
}