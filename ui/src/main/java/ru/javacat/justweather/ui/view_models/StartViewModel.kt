package ru.javacat.justweather.ui.view_models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.javacat.justweather.common.util.LOC_LIMIT
import ru.javacat.justweather.domain.ApiError
import ru.javacat.justweather.domain.NetworkError
import ru.javacat.justweather.domain.models.Weather
import ru.javacat.justweather.ui.LoadingState
import ru.javacat.justweather.ui.SingleLiveEvent
import javax.inject.Inject

@HiltViewModel
class StartViewModel @Inject constructor(
    private val repository: ru.javacat.justweather.domain.repos.Repository,
): ViewModel() {

    private val locationsLimit = LOC_LIMIT

    private val _weatherData: MutableLiveData<Weather> = MutableLiveData()
    val weatherData: LiveData<Weather>
        get() = _weatherData

    //val currentWeatherFlow = repository.currentWeatherFlow.asLiveData()

    val loadingState = SingleLiveEvent<LoadingState>()

    suspend fun getCurrentWeatherFlow(): StateFlow<Weather?> =
        repository.currentWeatherFlow.stateIn(
            CoroutineScope(Dispatchers.IO),
            SharingStarted.WhileSubscribed(),
            repository.getCurrentWeather()?:null
        )


    fun getLocationByCoords(coords: String){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val listOfCoords = coords.split(",")
                val reversedCoords = listOfCoords[1]+","+listOfCoords[0]
                val result = repository.getLocationByCoords(reversedCoords)
                Log.i("MyTag", "getLocationByCoords: $result")

                //val localTitle = result.featureMember[0].GeoObject.name
                val localAddress = result.featureMember[0].GeoObject.metaDataProperty.GeocoderMetaData.Address

                val country = localAddress.Components.firstOrNull{it.kind == "country"}?.name
                val province = localAddress.Components.firstOrNull{it.kind == "province"}?.name
                val localTitle = localAddress.Components.lastOrNull { it.kind == "locality" }?.name.toString()
                val localSubtitle = "$country, $province"

                repository.unCheckLocated()

                setPlace(coords, true, localTitle, localSubtitle)
            } catch (e: ApiError) {
                loadingState.postValue(LoadingState.InputError)
                Log.i("MyTag", "ОШИБКА: ${e.code}")
            } catch (e: NetworkError) {
                loadingState.postValue(LoadingState.NetworkError)
                Log.i("MyTag", "ОШИБКА: NETWORK")
            }
        }
    }


    private fun setPlace(request: String, isLocated: Boolean, localTitle: String, localSubtitle: String) {
        Log.i("StartVM", "settingLocation")
        viewModelScope.launch(Dispatchers.IO) {
            loadingState.postValue(LoadingState.Load)
            try {
                repository.getNewPlaceDetails(request,  isLocated, localTitle, localSubtitle, locationsLimit)
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
}