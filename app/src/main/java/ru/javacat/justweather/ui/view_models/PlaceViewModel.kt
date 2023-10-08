package ru.javacat.justweather.ui.view_models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.javacat.justweather.ApiError
import ru.javacat.justweather.NetworkError
import ru.javacat.justweather.models.Place
import ru.javacat.justweather.repository.CurrentPlaceRepository
import ru.javacat.justweather.repository.PlacesRepository
import ru.javacat.justweather.repository.Repository
import ru.javacat.justweather.response_models.SearchLocation
import ru.javacat.justweather.ui.LoadingState
import ru.javacat.justweather.ui.SingleLiveEvent
import javax.inject.Inject

@HiltViewModel
class PlaceViewModel @Inject constructor(
    private val repository: Repository,
    private val placesRepository: PlacesRepository,
    private val currentPlaceRepository: CurrentPlaceRepository
) : ViewModel() {



    private val _placeData = MutableLiveData<List<Place>>()
    val placeData: LiveData<List<Place>>
        get() = _placeData

    private var _foundLocations = MutableLiveData<List<SearchLocation>>()
    val foundLocations: LiveData<List<SearchLocation>>
        get() = _foundLocations

    val loadingState = SingleLiveEvent<LoadingState>()

    init {
        loadPlaces()
    }

    private fun loadPlaces() {
        viewModelScope.launch(Dispatchers.IO) {
            _placeData.postValue(placesRepository.getPlaces().value)
        }
    }


    fun savePlace(place: Place) {
        viewModelScope.launch(Dispatchers.IO) {
            val places = placeData.value
            val result = places?.find { it.name == place.name }
            if (result == null) {
                placesRepository.save(place)
                loadPlaces()
            }
        }

    }

    fun getLocations(query: String){
        //var searchLocations = emptyList<SearchLocation>()
        viewModelScope.launch(Dispatchers.IO) {
            loadingState.postValue(LoadingState.Load)

            try {
                val found = repository.findLocation(query) ?: throw NetworkError
                _foundLocations.postValue(found)
                loadingState.postValue(LoadingState.Found)
                //savePlace(Place(0, found.name, foundWeather.location.region))
                //Log.i("MyTag", "found: $foundWeather")


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
                val weather = repository.fetchLocationDetails(name, daysCount) ?: throw NetworkError
                weather.location.let {
                    currentPlaceRepository.saveToPlacesList(it)
                }
                Log.i("MyTag", "weatherResp: $weather")
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


    fun removePlace(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            placesRepository.removeById(id)
            loadPlaces()
        }

    }

    fun clearPlace(){
        _foundLocations.value = emptyList()
    }
}