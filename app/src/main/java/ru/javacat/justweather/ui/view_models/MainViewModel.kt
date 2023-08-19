package ru.javacat.justweather.ui.view_models

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.javacat.justweather.ApiError
import ru.javacat.justweather.NetworkError
import ru.javacat.justweather.models.Place
import ru.javacat.justweather.repository.PlacesRepository
import ru.javacat.justweather.repository.PlacesRepositorySharedPrefsImpl
import ru.javacat.justweather.repository.Repository
import ru.javacat.justweather.repository.RepositoryImpl
import ru.javacat.justweather.response_models.Astro
import ru.javacat.justweather.response_models.Condition
import ru.javacat.justweather.response_models.Day
import ru.javacat.justweather.response_models.Forecastday
import ru.javacat.justweather.response_models.Weather
import ru.javacat.justweather.ui.LoadingState
import ru.javacat.justweather.ui.SingleLiveEvent
import java.lang.Exception

val emptyForecastday = Forecastday(
    Astro(0, 0, "", "", "", "", "", ""),
    "error",
    0,
    Day(
        0.0,
        0.0,
        0.0,
        0.0,
        0.0,
        Condition(0, "", ""),
        0,
        0,
        0,
        0,
        0.0,
        0.0,
        0.0,
        0.0,
        0.0,
        0.0,
        0.0,
        0.0,
        0.0,
        0.0
    ),
    emptyList()
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: Repository = RepositoryImpl()
    private val placesRepository: PlacesRepository = PlacesRepositorySharedPrefsImpl(application)

    val loadingState = SingleLiveEvent<LoadingState>()

    private val _data = MutableLiveData<Weather>()
    val data: LiveData<Weather>
        get() = _data

    private val _forecastData = MutableLiveData<Forecastday>()
    val forecastData: LiveData<Forecastday>
        get() = _forecastData


    private val _placeData = MutableLiveData<List<Place>>()

    val placeData: LiveData<List<Place>>
        get() = _placeData


    init {
        Log.i("MyTag", "initing VM")
        loadPlaces()
    }


    fun loadWeatherByName(name: String, daysCount: Int) {
        viewModelScope.launch {
            loadingState.postValue(LoadingState.Load)
            try {
                val weather = repository.loadByName(name, daysCount)
                Log.i("MyTag", "weatherResp: $weather")
                if (weather != null) {
                    _data.value = weather!!
                    savePlace(Place(0, weather.location.name, weather.location.region))
                }
            }catch (e: ApiError) {
                loadingState.postValue(LoadingState.InputError)
                Log.i("MyTag", "ОШИБКА: ${e.code}")
            }catch (e: NetworkError){
                loadingState.postValue(LoadingState.NetworkError)
                Log.i("MyTag", "ОШИБКА: NETWORK")
            }
        }
    }

    fun chooseForecastDay(item: Forecastday) {
        viewModelScope.launch {
            try {
                _forecastData.postValue(item)
            } catch (e: Exception){
                loadingState.postValue(LoadingState.NetworkError)
            }

        }

        //Log.i("MyLog", forecastData.value!!.date)
    }

    private fun loadPlaces() {
        _placeData.value = placesRepository.getPlaces().value
    }


    private fun savePlace(place: Place) {
        val places = placeData.value
        val result = places?.find { it.name == place.name }
        if (result == null) {
            placesRepository.save(place)
            loadPlaces()
        }
    }

    fun removePlace(id: Int) {
        placesRepository.removeById(id)
        loadPlaces()
    }
}