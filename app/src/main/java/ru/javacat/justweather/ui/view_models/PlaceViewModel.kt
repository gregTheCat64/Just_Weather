package ru.javacat.justweather.ui.view_models

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import ru.javacat.justweather.ApiError
import ru.javacat.justweather.NetworkError
import ru.javacat.justweather.models.Place
import ru.javacat.justweather.repository.PlacesRepository
import ru.javacat.justweather.repository.PlacesRepositorySharedPrefsImpl
import ru.javacat.justweather.repository.Repository
import ru.javacat.justweather.repository.RepositoryImpl
import ru.javacat.justweather.response_models.Weather
import ru.javacat.justweather.ui.LoadingState
import ru.javacat.justweather.ui.SingleLiveEvent

class PlaceViewModel(application: Application): AndroidViewModel(application) {
    private val repository: Repository = RepositoryImpl()
    private val placesRepository: PlacesRepository = PlacesRepositorySharedPrefsImpl(application)

    //val weatherFlow: SharedFlow<Weather?> = repository.weatherFlow

    private val _placeData = MutableLiveData<List<Place>>()
    val placeData: LiveData<List<Place>>
        get() = _placeData

    private val _currentPlace = MutableLiveData<String>()
    val currentPlace: LiveData<String>
        get() = _currentPlace

    val loadingState = SingleLiveEvent<LoadingState>()

    init {
        loadPlaces()
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

    fun findPlace(name: String, daysCount: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            loadingState.postValue(LoadingState.Load)
            try {
                val foundWeather = repository.loadByName(name, daysCount) ?: throw NetworkError

                savePlace(Place(0, foundWeather.location.name, foundWeather.location.region))
                Log.i("MyTag", "found: ${foundWeather.location.name}")

            } catch (e: ApiError) {
                loadingState.postValue(LoadingState.InputError)
                Log.i("MyTag", "ОШИБКА: ${e.code}")
            } catch (e: NetworkError) {
                loadingState.postValue(LoadingState.NetworkError)
                Log.i("MyTag", "ОШИБКА: NETWORK")
            }
        }
    }

    fun setPlace(name: String, daysCount: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            loadingState.postValue(LoadingState.Load)

            try {
                val weather = repository.loadByName(name, daysCount) ?: throw NetworkError
                Log.i("MyTag", "weatherResp: $weather")


                //_data.value = weather
                //_currentPlace.postValue(weather.location.name)

            } catch (e: ApiError) {
                loadingState.postValue(LoadingState.InputError)
                Log.i("MyTag", "ОШИБКА: ${e.code}")
            } catch (e: NetworkError) {
                loadingState.postValue(LoadingState.NetworkError)
                Log.i("MyTag", "ОШИБКА: NETWORK")
            }
        }
    }


    fun removePlace(id: Int) {
        placesRepository.removeById(id)
        loadPlaces()
    }
}