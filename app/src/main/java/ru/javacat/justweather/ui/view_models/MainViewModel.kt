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
import ru.javacat.justweather.domain.models.ForecastdayWithHours
import ru.javacat.justweather.domain.repos.CurrentPlaceRepository

import ru.javacat.justweather.domain.repos.Repository

import ru.javacat.justweather.ui.LoadingState
import ru.javacat.justweather.ui.SingleLiveEvent
import java.lang.Exception
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository,
    private val currentPlaceRepository: CurrentPlaceRepository
) : ViewModel(){


    val weatherFlow = repository.weatherFlow?.asLiveData(viewModelScope.coroutineContext)
    //val forecastFlow = repository.forecastFlow.asLiveData(viewModelScope.coroutineContext)

    private val loadingState = SingleLiveEvent<LoadingState>()

    private val _forecastData: MutableLiveData<Forecastday>? = null
    val forecastData: LiveData<Forecastday>?
        get() = _forecastData



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
                _forecastData?.postValue(item)
            } catch (e: Exception) {
                loadingState.postValue(LoadingState.NetworkError)
            }
        }
    }
}