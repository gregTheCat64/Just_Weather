package ru.javacat.justweather.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.javacat.justweather.NetworkError
import ru.javacat.justweather.repository.Repository
import ru.javacat.justweather.repository.RepositoryImpl
import ru.javacat.justweather.response_models.Astro
import ru.javacat.justweather.response_models.Condition
import ru.javacat.justweather.response_models.Day
import ru.javacat.justweather.response_models.Forecast
import ru.javacat.justweather.response_models.Forecastday
import ru.javacat.justweather.response_models.Weather

val emptyForecastday = Forecastday(
    Astro(0,0,"","","","","",""),
    "error",
    0,
    Day(0.0,0.0,0.0,0.0, 0.0, Condition(0,"",""),0,0,0,0,0.0,0.0,0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
    emptyList()
)

class MainViewModel(application: Application): AndroidViewModel(application) {

    private val repository: Repository = RepositoryImpl()

    private val _data = MutableLiveData<Weather>()
    val data: LiveData<Weather>
        get() = _data

    private val _forecastData = MutableLiveData<Forecastday>()
    val forecastData: LiveData<Forecastday>
        get() = _forecastData


    fun loadWeatherByName(name: String, daysCount: Int){
        viewModelScope.launch {
            try {
                val response = repository.getByName(name, daysCount)
                if (response != null) {
                    _data.value = response!!
                    println(response.current.temp_c)
                } else return@launch
            } catch (e: NetworkError){
                e.printStackTrace()
            }
        }
    }

    fun chooseForecastData(item: Forecastday){
        viewModelScope.launch {
            _forecastData.postValue(item)
        }

        //Log.i("MyLog", forecastData.value!!.date)
    }
}