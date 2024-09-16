package com.harshavardhang.weathertestdemo.viewmodel
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.harshavardhang.weathertestdemo.model.WeatherResponse
import com.harshavardhang.weathertestdemo.repository.WeatherRepository
import com.harshavardhang.weathertestdemo.viewmodel.WeatherViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*

class WeatherViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: WeatherViewModel
    private lateinit var repository: WeatherRepository
    private lateinit var locationProviderClient: FusedLocationProviderClient
    private val weatherObserver: Observer<WeatherResponse> = mock(Observer::class.java) as Observer<WeatherResponse>
    private val errorObserver: Observer<String> = mock(Observer::class.java) as Observer<String>

    @Before
    fun setUp() {
        repository = mock(WeatherRepository::class.java)
        locationProviderClient = mock(FusedLocationProviderClient::class.java)
        viewModel = WeatherViewModel(repository, locationProviderClient)
        Dispatchers.setMain(Dispatchers.Unconfined)

        viewModel.weatherLiveData.observeForever(weatherObserver)
        viewModel.errorLiveData.observeForever(errorObserver)
    }

    @Test
    fun testViewModelInitialization_CallsLoadWeatherForLastSearchedCity() = runTest {
        verify(repository).getLastSearchedCity() // Verify that the repository tries to fetch the last searched city
    }

    @Test
    fun testFetchWeatherForCity_Success() = runTest {
        // Mocking repository response for a successful weather fetch
        val weatherResponse = WeatherResponse(/* provide mock data */)
        `when`(repository.getWeather("New York")).thenReturn(weatherResponse)

        viewModel.fetchWeatherForCity("New York")

        verify(repository).getLastSearchedCity()  }

}