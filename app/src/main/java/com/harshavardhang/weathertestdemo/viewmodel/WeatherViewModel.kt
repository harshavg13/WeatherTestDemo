package com.harshavardhang.weathertestdemo.viewmodel

import android.content.Context
import android.location.Location
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.tasks.Task
import com.harshavardhang.weathertestdemo.model.WeatherResponse
import com.harshavardhang.weathertestdemo.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import com.harshavardhang.weathertestdemo.BuildConfig
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val locationProviderClient: FusedLocationProviderClient
) : ViewModel() {

    // LiveData for the city name
    private val _cityNameLiveData = MutableLiveData<String>()
    val cityNameLiveData: LiveData<String> = _cityNameLiveData

    // LiveData for the weather response
    val weatherLiveData = MutableLiveData<WeatherResponse>()

    // LiveData for any errors that occur
    val errorLiveData = MutableLiveData<String>()

    // LiveData for the location coordinates (latitude, longitude)
    val locationLiveData = MutableLiveData<Pair<Double, Double>>()

    // Initialization block that automatically loads weather for the last searched city
    init {
        loadWeatherForLastSearchedCity(BuildConfig.API_KEY)
    }

    // Fetches weather for a given city name
    fun fetchWeatherForCity(cityName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getWeather(cityName)
                if (response != null) {
                    // Save the city name as the last searched city
                    saveLastSearchedCity(cityName)
                }
                // Post the weather data to LiveData
                weatherLiveData.postValue(response)
            } catch (e: Exception) {
                // If there's an error (e.g., permission issue), post it to error LiveData
                errorLiveData.postValue("Location permission not granted")
            }
        }
    }

    // Fetches the user's last known location
    fun fetchLastKnownLocation() {
        viewModelScope.launch {
            try {
                // Fetch the last known location asynchronously
                val location = withContext(Dispatchers.IO) {
                    locationProviderClient.lastLocation.await()
                }

                if (location != null) {
                    // If location is available, fetch weather using coordinates
                    val latitude = location.latitude
                    val longitude = location.longitude
                    fetchWeatherByCoordinates(latitude, longitude)
                } else {
                    // If no location is available, post an error
                    errorLiveData.postValue("Failed to retrieve location.")
                }
            } catch (e: SecurityException) {
                // Handle the case where location permission is not granted
                errorLiveData.postValue("Location permission not granted.")
            } catch (e: Exception) {
                // Handle any other exceptions
                errorLiveData.postValue("An error occurred: ${e.message}")
            }
        }
    }

    // Fetches weather based on provided latitude and longitude
    private fun fetchWeatherByCoordinates(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            try {
                val weatherResponse = repository.fetchWeatherByCoordinates(latitude, longitude)
                weatherResponse?.name?.let { cityName ->
                    // Update city name in LiveData
                    _cityNameLiveData.postValue(cityName)
                }
                // Post weather data to LiveData
                weatherLiveData.postValue(weatherResponse)
            } catch (e: Exception) {
                // If an error occurs, post it to error LiveData
                errorLiveData.postValue("Failed to fetch weather data: ${e.message}")
            }
        }
    }

    // Saves the last searched city
    fun saveLastSearchedCity(cityName: String) {
        repository.saveLastSearchedCity(cityName)
    }

    // Loads weather for the last searched city
    fun loadWeatherForLastSearchedCity(apiKey: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val lastCity = repository.getLastSearchedCity()
            if (lastCity != null) {
                // If a last searched city exists, fetch its weather
                fetchWeatherForCity(lastCity)
            }
        }
    }

    // Updates the city name in LiveData
    fun updateCityName(cityName: String) {
        _cityNameLiveData.value = cityName
    }
}

// Extension function for Task<T> to await its result in a coroutine
suspend fun <T> Task<T>.await(): T {
    return suspendCancellableCoroutine { continuation ->
        addOnSuccessListener { result ->
            continuation.resume(result)
        }
        addOnFailureListener { exception ->
            continuation.resumeWithException(exception)
        }
        addOnCanceledListener {
            continuation.cancel()
        }
    }
}