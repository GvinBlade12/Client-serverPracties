package com.example.ex10

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Build
import android.os.Looper
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

@Composable
fun LocationAppScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val fusedClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    var address by remember { mutableStateOf("") }
    var coordinates by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }



    suspend fun getAddressFromCoordinates(lat: Double, lng: Double) {
        try {
            val geocoder = Geocoder(context, Locale("ru", "RU"))
            val addresses = withContext(Dispatchers.IO) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    geocoder.getFromLocation(lat, lng, 1)
                } else {
                    @Suppress("DEPRECATION")
                    geocoder.getFromLocation(lat, lng, 1)
                }
            }

            address = if (!addresses.isNullOrEmpty()) {
                val addr = addresses[0]
                buildString {
                    append(addr.getAddressLine(0) ?: "Адрес не определён")
                    addr.locality?.let { append("\n$it") }
                    addr.adminArea?.let { append(", $it") }
                    addr.countryName?.let { append(", $it") }
                }
            } else {
                "Адрес не найден"
            }
        } catch (e: Exception) {
            address = "Не удалось получить адрес"
        } finally {
            isLoading = false
        }
    }

    suspend fun getCurrentLocation() {
        isLoading = true
        errorMessage = null
        address = ""
        coordinates = ""

        try {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                errorMessage = "Нет разрешения на геолокацию"
                isLoading = false
                return
            }

            val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000L)
                .setMaxUpdates(1)
                .build()

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    fusedClient.removeLocationUpdates(this)

                    val location = locationResult.lastLocation
                    if (location != null) {
                        val lat = location.latitude
                        val lng = location.longitude

                        coordinates = "lat: %.6f\nlng: %.6f".format(lat, lng)

                        // Запускаем геокодирование
                        scope.launch {
                            getAddressFromCoordinates(lat, lng)
                        }
                    } else {
                        errorMessage = "Не удалось получить местоположение"
                        isLoading = false
                    }
                }
            }

            fusedClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())

        } catch (e: Exception) {
            errorMessage = "Ошибка: ${e.localizedMessage ?: "Неизвестная ошибка"}"
            isLoading = false
        }
    }


    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (fineGranted || coarseGranted) {
            scope.launch { getCurrentLocation() }
        } else {
            errorMessage = "Разрешение на геолокацию не предоставлено"
            Toast.makeText(context, "Нужно разрешение на определение местоположения", Toast.LENGTH_LONG).show()
        }
    }
    

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Определение местоположения",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

                if (hasFine || hasCoarse) {
                    scope.launch { getCurrentLocation() }
                } else {
                    permissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isLoading) "Определяем местоположение..." else "Получить мой адрес")
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text("Загрузка...", fontSize = 16.sp)
        }

        if (address.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Ваш адрес:", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = address,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 28.sp
                    )
                }
            }
        }

        if (coordinates.isNotEmpty()) {
            Text(
                text = coordinates,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        errorMessage?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = it, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
        }
    }
}