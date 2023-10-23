package ru.javacat.justweather.ui.view_models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.javacat.justweather.ApiError
import ru.javacat.justweather.NetworkError
import ru.javacat.justweather.domain.models.Forecastday
import ru.javacat.justweather.domain.models.Hour
import ru.javacat.justweather.domain.models.Weather
import ru.javacat.justweather.domain.repos.CurrentPlaceRepository
import ru.javacat.justweather.domain.repos.Repository
import ru.javacat.justweather.ui.LoadingState
import ru.javacat.justweather.ui.SingleLiveEvent
import javax.inject.Inject
import kotlin.Exception


@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository,
    private val currentPlaceRepository: CurrentPlaceRepository
) : ViewModel(){


    val currentWeatherFlow = repository.currentWeatherFlow.asLiveData(viewModelScope.coroutineContext)
    //val forecastFlow = repository.forecastFlow.asLiveData(viewModelScope.coroutineContext)

    private val loadingState = SingleLiveEvent<LoadingState>()

    private val _weatherData: MutableLiveData<Weather> = MutableLiveData()
    val weatherData: LiveData<Weather>
        get() = _weatherData

    private val _forecastData: MutableLiveData<Forecastday> = MutableLiveData()
    val forecastData: LiveData<Forecastday>
        get() = _forecastData

    private var _hoursData: MutableLiveData<List<Hour>> = MutableLiveData()
    var hoursData: LiveData<List<Hour>>? = _hoursData


    init {
        Log.i("MyTag", "initing VM")
    }

    fun updateWeather(){
        val place = currentPlaceRepository.getFromPlacesList()
        Log.i("MyTag", "restoring ${place?.name}")
        val coords = place?.lat.toString()+","+place?.lon.toString()
        findPlaceByLocation(coords)
    }


    private fun findPlaceByLocation(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            loadingState.postValue(LoadingState.Load)

            try {
                repository.fetchLocationDetails(name)?: throw NetworkError
                //_weatherData.postValue(repository.getCurrentWeather(name))
                loadingState.postValue(LoadingState.Success)

            } catch (e: ApiError) {
                loadingState.postValue(LoadingState.InputError)
                Log.i("MyTag", "ОШИБКА: ${e.code}")
            } catch (e: NetworkError) {
                loadingState.postValue(LoadingState.NetworkError)
                Log.i("MyTag", "ОШИБКА: NETWORK")
            }
        }
    }


    fun chooseForecastDay(item: Forecastday) {
        viewModelScope.launch {
            try {
                _forecastData.postValue(item)
                Log.i("MyTag", "forecast: ${_forecastData.value}")
            } catch (e: Exception) {
                loadingState.postValue(LoadingState.NetworkError)
            }
        }
    }

    fun getHours(date: String) {
        viewModelScope.launch {
            try {
                println("getting hours in vm")
                _hoursData.value = repository.getHours(date)
                //Log.i("HOURS", "${hoursData.value}")
            } catch (e: Exception) {
                loadingState.postValue(LoadingState.NetworkError)
            }
        }



    }
}