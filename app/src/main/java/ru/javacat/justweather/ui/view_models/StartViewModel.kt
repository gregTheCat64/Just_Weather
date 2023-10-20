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
import ru.javacat.justweather.models.Place
import ru.javacat.justweather.domain.repos.CurrentPlaceRepository
import ru.javacat.justweather.domain.repos.PlacesRepository
import ru.javacat.justweather.domain.repos.Repository
import ru.javacat.justweather.ui.LoadingState
import ru.javacat.justweather.ui.SingleLiveEvent
import javax.inject.Inject

@HiltViewModel
class StartViewModel @Inject constructor(
    private val repository: Repository,
    private val placesRepository: PlacesRepository,
    private val currentPlaceRepository: CurrentPlaceRepository
): ViewModel() {

    val weatherFlow = repository.weatherFlow.asLiveData(viewModelScope.coroutineContext)
    val loadingState = SingleLiveEvent<LoadingState>()

    private val _placeData = MutableLiveData<List<Place>>()
    val placeData: LiveData<List<Place>>
        get() = _placeData

    init {
        loadPlaces()
    }

    fun findPlaceByLocation(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            loadingState.postValue(LoadingState.Load)

            try {
                val foundWeather = repository.fetchLocationDetails(name)?:throw NetworkError
                loadingState.postValue(LoadingState.Success)
                //savePlace(Place(0, weatherFlow.location.name, foundWeather.location.region))
                //TODO: fix saving

            } catch (e: ApiError) {
                loadingState.postValue(LoadingState.InputError)
                Log.i("MyTag", "ОШИБКА: ${e.code}")
            } catch (e: NetworkError) {
                loadingState.postValue(LoadingState.NetworkError)
                Log.i("MyTag", "ОШИБКА: NETWORK")
            }
        }
    }

    private fun loadPlaces() {
        viewModelScope.launch(Dispatchers.IO) {
            _placeData.postValue(placesRepository.getPlaces().value)
        }
    }

    private suspend fun savePlace(place: Place) {
        viewModelScope.launch(Dispatchers.IO) {
            val places = placeData.value
            val result = places?.find { it.name == place.name }
            println("RESULT_PLACES= $result")
            if (result == null) {
                placesRepository.save(place)
                loadPlaces()
            }
            addToPlacesList()
        }
    }

    private fun addToPlacesList(){
        viewModelScope.launch{
            weatherFlow.value?.location?.let { currentPlaceRepository.saveToPlacesList(it) }

        }
    }


}