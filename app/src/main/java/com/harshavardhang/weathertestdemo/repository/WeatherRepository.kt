package com.harshavardhang.weathertestdemo.repository

import android.app.Application
import android.content.Context
import android.util.Log
import com.harshavardhang.weathertestdemo.BuildConfig
import com.harshavardhang.weathertestdemo.api.WeatherApiService
import com.harshavardhang.weathertestdemo.model.WeatherResponse
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val apiService: WeatherApiService,
    private val application: Application // Inject the Application context
) {
    val api_key = BuildConfig.API_KEY;
    suspend fun getWeather(cityName: String): WeatherResponse? {
        return try {
            val response = apiService.getWeather(cityName, api_key)
            if (response.isSuccessful) {
                response.body() // Return the body if successful
            } else {
                // Handle non-2xx response codes here
                println("Error: ${response.code()} - ${response.message()}")
                null
            }
        } catch (e: Exception) {
            // Handle network or conversion errors here
            println("Exception occurred: ${e.message}")
            null
        }
    }

    suspend fun fetchWeatherByCoordinates(latitude: Double, longitude: Double): WeatherResponse? {
        return try {
            // Retrofit's suspend function makes the network request asynchronously
            val response = apiService.fetchWeatherByCoordinates(latitude, longitude, api_key)
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("WeatherViewModel", "Error: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("WeatherViewModel", "Error fetching weather data: ${e.message}")
            null
        }
    }

    // Save the last searched city to SharedPreferences using the application context
    fun saveLastSearchedCity(cityName: String) {
        val sharedPref = application.getSharedPreferences("WeatherAppPreferences", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString("LAST_SEARCHED_CITY", cityName)
        editor.apply()
    }

    // Load the last searched city from SharedPreferences
    fun getLastSearchedCity(): String? {
        val sharedPref = application.getSharedPreferences("WeatherAppPreferences", Context.MODE_PRIVATE)
        return sharedPref.getString("LAST_SEARCHED_CITY", null)
    }
}