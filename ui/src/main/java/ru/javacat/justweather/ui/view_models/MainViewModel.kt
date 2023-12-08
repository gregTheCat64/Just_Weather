package ru.javacat.justweather.ui.view_models

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.javacat.justweather.domain.ApiError
import ru.javacat.justweather.domain.NetworkError
import ru.javacat.justweather.domain.models.Forecastday
import ru.javacat.justweather.domain.models.Hour
import ru.javacat.justweather.domain.models.Weather
import ru.javacat.justweather.ui.LoadingState
import ru.javacat.justweather.ui.SingleLiveEvent
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: ru.javacat.justweather.domain.repos.Repository,
) : ViewModel(){


    //val currentWeatherFlow = repository.currentWeatherFlow

    val loadingState = SingleLiveEvent<LoadingState>()

    private val _forecastData = MutableStateFlow<Forecastday?>(null)
    val forecastData = _forecastData.asStateFlow()

    private var _hoursData = MutableStateFlow<List<Hour>?>(null)
    var hoursData = _hoursData.asStateFlow()


    init {
        Log.i("MyTag", "initing VM")
    }

    suspend fun getCurrentWeatherFlow(): StateFlow<Weather?> =
        repository.currentWeatherFlow.stateIn(
            CoroutineScope(Dispatchers.IO),
            SharingStarted.WhileSubscribed(),
            repository.getCurrentWeather()
        )

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
        Log.i("MainViewModel", "setting _forecastData")
        viewModelScope.launch() {
            try {
                _forecastData.emit(item)
            } catch (e: Exception) {
                loadingState.postValue(LoadingState.NetworkError)
            }
        }
    }

    fun getHours(weatherId: String, date: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                println("getting hours in vm")
                _hoursData.emit(repository.getHours(weatherId, date))
                Log.i("HOURS", "${hoursData.value}")
            } catch (e: Exception) {
                println("ERROR inVM with hours")
                loadingState.postValue(LoadingState.NetworkError)
            }
        }
    }
}