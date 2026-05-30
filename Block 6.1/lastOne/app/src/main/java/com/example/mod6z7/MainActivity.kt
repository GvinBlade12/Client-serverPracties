package com.example.mod6z7
import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.mod6z7.ui.theme.Mod6z7Theme
import java.util.UUID

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Mod6z7Theme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    BLEScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BLEScreen() {
    val context = LocalContext.current
    var isScanning by remember { mutableStateOf(false) }
    var devices by remember { mutableStateOf<List<BluetoothDevice>>(emptyList()) }
    var heartRate by remember { mutableStateOf<Int?>(null) }
    var isConnected by remember { mutableStateOf(false) }
    var connectedDeviceName by remember { mutableStateOf<String?>(null) }
    var currentGatt by remember { mutableStateOf<BluetoothGatt?>(null) }
    val bluetoothManager = remember { context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager }
    val bluetoothAdapter = remember { bluetoothManager.adapter }
    val scanner = remember { bluetoothAdapter?.bluetoothLeScanner }
    val scanCallback = remember {
        object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                val device = result.device
                if (!devices.any { it.address == device.address }) {
                    devices = devices + device
                }
            }
        }
    }

    fun startScan() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) return
        isScanning = true
        devices = emptyList()
        scanner?.startScan(scanCallback)
        Handler(Looper.getMainLooper()).postDelayed({
            if (isScanning) {
                scanner?.stopScan(scanCallback)
                isScanning = false
            }
        }, 10000)
    }

    fun stopScan() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {
            scanner?.stopScan(scanCallback)
        }
        isScanning = false
    }

    fun connectToDevice(device: BluetoothDevice) {
        stopScan()
        connectedDeviceName = device.name

        val gattCallback = object : BluetoothGattCallback() {
            override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    gatt.discoverServices()
                }
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                val hrService = gatt.getService(UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb"))
                val hrChar = hrService?.getCharacteristic(UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb"))
                if (hrChar != null && ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                    gatt.setCharacteristicNotification(hrChar, true)
                    val desc = hrChar.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))
                    desc?.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
                    gatt.writeDescriptor(desc)
                }
            }

            override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
                val data = characteristic.value
                if (data != null && data.size >= 2) {
                    val flags = data[0].toInt() and 0xFF
                    val is16Bit = (flags and 0x01) != 0
                    val bpm = if (is16Bit && data.size >= 3) {
                        ((data[2].toInt() and 0xFF) shl 8) or (data[1].toInt() and 0xFF)
                    } else {
                        data[1].toInt() and 0xFF
                    }
                    heartRate = bpm
                    isConnected = true
                }
            }
        }

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            currentGatt = device.connectGatt(context, false, gattCallback)
        }
    }
    fun disconnect() {
        currentGatt?.disconnect()
        currentGatt?.close()
        currentGatt = null
        isConnected = false
        heartRate = null
        connectedDeviceName = null
    }
    LaunchedEffect(Unit) {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            listOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            listOf(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        permissions.forEach { perm ->
            if (ContextCompat.checkSelfPermission(context, perm) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(context as MainActivity, permissions.toTypedArray(), 1)
            }
        }
    }
    Scaffold(
        topBar = { TopAppBar(title = { Text("Heart Rate Monitor") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Button(
                onClick = { if (isScanning) stopScan() else startScan() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isScanning) "Остановить" else "Начать сканирование")
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isConnected) "Connected" else "Disconnected",
                        color = if (isConnected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = heartRate?.toString() ?: "—",
                        fontSize = 56.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text("Heart Rate: ${heartRate?.toString() ?: "—"} bpm")
                    if (connectedDeviceName != null) {
                        Text(connectedDeviceName!!, fontSize = 12.sp)
                    }
                }
            }

            Text("Устройства с Heart Rate Service", fontWeight = FontWeight.Bold)

            LazyColumn {
                items(devices) { device ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { connectToDevice(device) }
                    ) {
                        Text(
                            text = "${device.name ?: "Unknown"} - ${device.address}",
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }

            if (isConnected) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { disconnect() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Отключиться")
                }
            }
        }
    }
}