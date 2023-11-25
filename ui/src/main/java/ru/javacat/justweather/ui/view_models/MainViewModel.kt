package ru.javacat.justweather.ui.view_models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.javacat.justweather.domain.ApiError
import ru.javacat.justweather.domain.NetworkError
import ru.javacat.justweather.domain.models.Forecastday
import ru.javacat.justweather.domain.models.Hour
import ru.javacat.justweather.ui.LoadingState
import ru.javacat.justweather.ui.SingleLiveEvent
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: ru.javacat.justweather.domain.repos.Repository,
) : ViewModel(){

    val currentWeatherFlow = repository.currentWeatherFlow

    val loadingState = SingleLiveEvent<LoadingState>()

    private val _forecastData: MutableLiveData<Forecastday> = MutableLiveData()
    val forecastData: LiveData<Forecastday>
        get() = _forecastData

    private var _hoursData: MutableLiveData<List<Hour>> = MutableLiveData()
    var hoursData: LiveData<List<Hour>>? = _hoursData


    init {
        Log.i("MyTag", "initing VM")
    }


    fun updateWeatherList(){
        viewModelScope.launch(Dispatchers.IO) {
            loadingState.postValue(LoadingState.Load)
            try {
                val place = repository.getCurrentWeather()
                Log.i("MyTag", "restoring ${place?.location}")
                val id = place?.id.toString()
                repository.updateWeatherById(id, false)
                loadingState.postValue(LoadingState.Success)
                loadingState.postValue(LoadingState.Updated)
            }catch (e: ApiError) {
                loadingState.postValue(LoadingState.InputError)
                Log.i("MyTag", "ОШИБКА: ${e.code}")
            } catch (e: NetworkError) {
                loadingState.postValue(LoadingState.NetworkError)
                Log.i("MyTag", "ОШИБКА: NETWORK")
            }

        }
    }

    fun updateCurrentWeather(){
        viewModelScope.launch(Dispatchers.IO) {
            loadingState.postValue(LoadingState.Load)
            try {
                val place = repository.getCurrentWeather()
                Log.i("MyTag", "restoring ${place?.location}")
                val id = place?.id.toString()
                repository.updateCurrentWeather(id)
                loadingState.postValue(LoadingState.Success)
                loadingState.postValue(LoadingState.Updated)
            }catch (e: ApiError) {
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

    fun getHours(weatherId: String, date: String) {
        viewModelScope.launch {
            try {
                println("getting hours in vm")
                _hoursData.value = repository.getHours(weatherId, date)
                //Log.i("HOURS", "${hoursData.value}")
            } catch (e: Exception) {
                loadingState.postValue(LoadingState.NetworkError)
            }
        }
    }
}