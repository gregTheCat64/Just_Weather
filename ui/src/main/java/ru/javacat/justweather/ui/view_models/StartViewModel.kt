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
import ru.javacat.justweather.domain.ApiError
import ru.javacat.justweather.domain.NetworkError
import ru.javacat.justweather.ui.LoadingState
import ru.javacat.justweather.ui.SingleLiveEvent
import javax.inject.Inject

@HiltViewModel
class StartViewModel @Inject constructor(
    private val repository: ru.javacat.justweather.domain.repos.Repository,
    private val currentPlaceRepository: ru.javacat.justweather.domain.repos.CurrentPlaceRepository
): ViewModel() {
    private val _weatherData: MutableLiveData<ru.javacat.justweather.domain.models.Weather> = MutableLiveData()
    val weatherData: LiveData<ru.javacat.justweather.domain.models.Weather>
        get() = _weatherData

    val currentWeatherFlow = repository.currentWeatherFlow.asLiveData(viewModelScope.coroutineContext)

    val loadingState = SingleLiveEvent<LoadingState>()

//    private val _placeData = MutableLiveData<List<Place>>()
//    val placeData: LiveData<List<Place>>
//        get() = _placeData

    init {
        //loadPlaces()
        //viewModelScope.launch(Dispatchers.IO) {  repository.updateDb() }

    }

//    suspend fun updateDb(){
//        viewModelScope.launch(Dispatchers.IO) {
//            repository.updateDb()
//        }
//    }

    fun findPlaceByLocation(name: String) {
        Log.i("StartFrag", "loadingData")
        viewModelScope.launch(Dispatchers.IO) {
            loadingState.postValue(LoadingState.Load)

            try {
                repository.fetchLocationDetails(name, "newCurrent", true)?:throw NetworkError
                loadingState.postValue(LoadingState.Success)

                //delay(5000)
                //val foundWeatherName = repository.fetchLocationDetails(name)
                //_weatherData.postValue(repository.getCurrentWeather(foundWeatherName!!))
                //savePlace(Place(0, weatherFlow.location.name, foundWeather.location.region))
                //TODO: fix saving

            } catch (e: ApiError) {
                loadingState.postValue(LoadingState.InputError)
                Log.i("MyTag", "ОШИБКА: ${e.code}")
            } catch (e: NetworkError) {
                loadingState.postValue(LoadingState.NetworkError)
                Log.i("MyTag", "ОШИБКА: NETWORK ${e.message}")
            }
        }
    }

//    private fun loadPlaces() {
//        viewModelScope.launch(Dispatchers.IO) {
//            _placeData.postValue(placesRepository.getPlaces().value)
//        }
//    }
//
//    private suspend fun savePlace(place: Place) {
//        viewModelScope.launch(Dispatchers.IO) {
//            val places = placeData.value
//            val result = places?.find { it.name == place.name }
//            println("RESULT_PLACES= $result")
//            if (result == null) {
//                placesRepository.save(place)
//                //loadPlaces()
//            }
//            //addToPlacesList()
//        }
//    }

    private fun addToPlacesList(){
        viewModelScope.launch{
            weatherData?.value?.location?.let { currentPlaceRepository.saveToPlacesList(it) }

        }
    }


}