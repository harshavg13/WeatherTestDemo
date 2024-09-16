@file:OptIn(ExperimentalMaterial3Api::class)

package com.harshavardhang.weathertestdemo

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.harshavardhang.weathertestdemo.model.WeatherResponse
import com.harshavardhang.weathertestdemo.ui.theme.WeatherTestDemoTheme
import com.harshavardhang.weathertestdemo.viewmodel.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    // Activity initialization
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherAppScreen()  // Set the main UI content
        }
    }
}

@Composable
fun WeatherAppScreen(weatherViewModel: WeatherViewModel = viewModel()) {
    // Observing LiveData from the ViewModel
    val currentWeather by weatherViewModel.weatherLiveData.observeAsState()
    val isLocationPermissionGranted = remember { mutableStateOf(false) }
    val appContext = LocalContext.current
    val cityName by weatherViewModel.cityNameLiveData.observeAsState("")
    var isError by remember { mutableStateOf(false) }
    var isSearchAttempted by remember { mutableStateOf(false) }  // Track if a search has been attempted

    // Handle location permission request
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted -> isLocationPermissionGranted.value = isGranted }
    )

    // Main layout for the weather app
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top bar for search and location actions
        WeatherAppBar(
            onRequestLocation = {
                if (ContextCompat.checkSelfPermission(appContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    weatherViewModel.fetchLastKnownLocation()  // Get location if permission granted
                } else {
                    locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)  // Request permission
                }
            },
            onSearchCity = {
                isError = cityName.isBlank()  // Check if city name is empty
                if (!isError) {
                    weatherViewModel.fetchWeatherForCity(cityName)  // Fetch weather if city is valid
                }
                isSearchAttempted = true
            },
            cityName = cityName,  // Current city name
            onCityNameChange = {
                isSearchAttempted = false
                weatherViewModel.updateCityName(it)  // Update city name in ViewModel
            },
            isError = isError
        )

        // Display weather data or an error message if the search was attempted
        when {
            currentWeather != null -> WeatherDetailsView(currentWeather!!)  // Show weather details
            isSearchAttempted -> Text("No weather data available for $cityName", color = Color.Red, fontSize = 20.sp, modifier = Modifier.padding(top = 100.dp))  // Error message
        }
    }
}

@Composable
fun WeatherAppBar(
    onRequestLocation: () -> Unit,
    onSearchCity: () -> Unit,
    cityName: String,
    onCityNameChange: (String) -> Unit,
    isError: Boolean
) {
    TopAppBar(
        title = {
            // TextField for city name input
            Column {
                TextField(
                    value = cityName,
                    onValueChange = onCityNameChange,  // Update city name on text change
                    placeholder = { Text("Enter city name") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = isError,  // Show error if needed
                    singleLine = true
                )

                // Show error message if input is invalid
                if (isError) {
                    Text(
                        text = "Please enter a city",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                }
            }
        },
        actions = {
            // Search button
            IconButton(onClick = { onSearchCity() }) {
                Icon(Icons.Default.Search, contentDescription = "Search for City")
            }
            // Location button
            IconButton(onClick = { onRequestLocation() }) {
                Icon(Icons.Default.LocationOn, contentDescription = "Use Current Location")
            }
        }
    )
}

@Composable
fun WeatherDetailsView(weatherResponse: WeatherResponse) {
    // Display weather details
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Show weather icon using image URL
        AsyncImage(
            model = "https://openweathermap.org/img/wn/${weatherResponse.weather[0].icon}@2x.png",
            contentDescription = "Weather Icon",
            modifier = Modifier.size(150.dp)
        )

        // Display weather information in rows
        WeatherInfoRow(
            label1 = "City", value1 = weatherResponse.name,
            label2 = "Temperature", value2 = "${weatherResponse.main.temp}°C"
        )

        WeatherInfoRow(
            label1 = "Feels Like", value1 = "${weatherResponse.main.feels_like}°C",
            label2 = "Humidity", value2 = "${weatherResponse.main.humidity}%"
        )

        WeatherInfoRow(
            label1 = "Wind Speed", value1 = "${weatherResponse.wind.speed} m/s",
            label2 = "Sunrise", value2 = weatherResponse.sys.getFormattedSunrise()
        )

        WeatherInfoRow(
            label1 = "Sunset", value1 = weatherResponse.sys.getFormattedSunset(),
            label2 = "", value2 = ""  // Optional, more details can be added here
        )
    }
}

@Composable
fun WeatherInfoRow(label1: String, value1: String, label2: String, value2: String) {
    // Display a row with weather information
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        WeatherInfoTile(label = label1, value = value1)
        if (label2.isNotEmpty()) {
            WeatherInfoTile(label = label2, value = value2)
        }
    }
}

@Composable
fun WeatherInfoTile(label: String, value: String, modifier: Modifier = Modifier) {
    // Display a single weather info tile
    Column(modifier = modifier.padding(16.dp)) {
        Text(text = label, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = value, fontSize = 16.sp)
    }
}