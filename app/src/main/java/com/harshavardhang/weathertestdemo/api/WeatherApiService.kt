package com.harshavardhang.weathertestdemo.api

import com.harshavardhang.weathertestdemo.model.WeatherResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("weather")
    suspend fun getWeather(
        @Query("q") cityName: String?,
        @Query("appid") apiKey: String?
    ): Response<WeatherResponse>

    @GET("weather")
    suspend fun fetchWeatherByCoordinates(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String
    ): Response<WeatherResponse>
}