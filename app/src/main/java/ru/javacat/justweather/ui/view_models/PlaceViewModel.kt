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
import ru.javacat.justweather.domain.models.SearchLocation
import ru.javacat.justweather.domain.models.Weather
import ru.javacat.justweather.models.Place
import ru.javacat.justweather.domain.repos.CurrentPlaceRepository
import ru.javacat.justweather.domain.repos.PlacesRepository
import ru.javacat.justweather.domain.repos.Repository
import ru.javacat.justweather.ui.LoadingState
import ru.javacat.justweather.ui.SingleLiveEvent
import javax.inject.Inject

@HiltViewModel
class PlaceViewModel @Inject constructor(
    private val repository: Repository,
) : ViewModel() {


    private var _foundLocations = MutableLiveData<List<SearchLocation>>()
    val foundLocations: LiveData<List<SearchLocation>>
        get() = _foundLocations

    private var _allWeathers = MutableLiveData<List<Weather>?>()
    val allWeathers: LiveData<List<Weather>?>
        get() = _allWeathers

    val loadingState = SingleLiveEvent<LoadingState>()

    init {

        getAllWeathers()
    }

    private fun getAllWeathers() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = repository.getAllWeathers()
                _allWeathers.postValue(result)
            } catch (e: ApiError) {
                loadingState.postValue(LoadingState.InputError)
            }
        }
    }

    suspend fun updateDb(){
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateDb()
        }
    }


//    fun savePlace(place: Place) {
//        viewModelScope.launch(Dispatchers.IO) {
//            val places = placeData.value
//            val result = places?.find { it.name == place.name }
//            if (result == null) {
////                placesRepository.save(place)
////                loadPlaces()
//
//            }
//        }
//
//    }


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



    fun setPlace(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            loadingState.postValue(LoadingState.Load)

            try {
                repository.fetchLocationDetails(name, "newCurrent") ?: throw NetworkError

                //Log.i("MyTag", "weatherResp: $weather")
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


    fun removePlace(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            //placesRepository.removeById(id)
            //loadPlaces()
            repository.removeById(id)
        }

    }

    fun clearPlace(){
        _foundLocations.value = emptyList()
    }
}