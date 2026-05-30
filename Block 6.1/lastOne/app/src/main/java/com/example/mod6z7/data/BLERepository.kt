package com.example.mod6z7.data
import android.bluetooth.BluetoothDevice
import kotlinx.coroutines.flow.Flow

interface BLERepository {
    fun scanDevices(): Flow<BluetoothDevice>
    fun stopScan()
    fun connectToDevice(device: BluetoothDevice): Flow<Int>
    fun disconnect()
}