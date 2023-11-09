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
class MainViewModel @Inject constructor(
    private val repository: ru.javacat.justweather.domain.repos.Repository,
    private val currentPlaceRepository: ru.javacat.justweather.domain.repos.CurrentPlaceRepository
) : ViewModel(){


    val currentWeatherFlow = repository.currentWeatherFlow.asLiveData(viewModelScope.coroutineContext)
    //val forecastFlow = repository.forecastFlow.asLiveData(viewModelScope.coroutineContext)

    private val loadingState = SingleLiveEvent<LoadingState>()

    private val _weatherData: MutableLiveData<ru.javacat.justweather.domain.models.Weather> = MutableLiveData()
    val weatherData: LiveData<ru.javacat.justweather.domain.models.Weather>
        get() = _weatherData

    private val _forecastData: MutableLiveData<ru.javacat.justweather.domain.models.Forecastday> = MutableLiveData()
    val forecastData: LiveData<ru.javacat.justweather.domain.models.Forecastday>
        get() = _forecastData

    private var _hoursData: MutableLiveData<List<ru.javacat.justweather.domain.models.Hour>> = MutableLiveData()
    var hoursData: LiveData<List<ru.javacat.justweather.domain.models.Hour>>? = _hoursData


    init {
        Log.i("MyTag", "initing VM")
    }

    suspend fun updateDb(){
        viewModelScope.launch(Dispatchers.IO) {
            //получаем координаты всех городов в БД
            val lats:List<Double>? = repository.getAllWeathers()?.map { it.location.lat }
            val longs: List<Double>? = repository.getAllWeathers()?.map { it.location.lon }

            Log.i("lats:", "${lats?.size}")
            Log.i("longs:", "${longs?.size}")

            //получаем Id текущего города
            val previousCurrentId = repository.getCurrentWeather()?.id
            val previousCurrentName = repository.getCurrentWeather()?.location?.name
            Log.i("MyTag", "ГОРОД: $previousCurrentName")

            if (!lats.isNullOrEmpty() && !longs.isNullOrEmpty()){
                val pairList = lats.zip(longs)
                repository.clearDbs()
                for (pair in pairList){
                    //добавляем в параметр айди текущего города, чтобы в обновлении городов снова его вставить
                    repository.fetchLocationDetails("${pair.first},${pair.second}", previousCurrentId.toString(), false, "", "")
                }
            }
        }
    }

    fun updateWeather(){
        viewModelScope.launch(Dispatchers.IO) {
            val place = repository.getCurrentWeather()
            val localTitle = place?.location?.localTitle.toString()
            val localSubTitle = place?.location?.localSubtitle.toString()
            Log.i("MyTag", "restoring ${place?.location}")
            val coords = place?.location?.lat.toString() +","+place?.location?.lon.toString()
            findPlaceByLocation(coords, localTitle, localSubTitle)
        }

    }


    private fun findPlaceByLocation(name: String, localTitle: String, localSubtitle: String) {
        viewModelScope.launch(Dispatchers.IO) {
            loadingState.postValue(LoadingState.Load)

            try {
                repository.fetchLocationDetails(name, "newCurrent", false, localTitle, localSubtitle)?: throw NetworkError
                //_weatherData.postValue(repository.getCurrentWeather(name))
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


    fun chooseForecastDay(item: ru.javacat.justweather.domain.models.Forecastday) {
        viewModelScope.launch {
            try {
                _forecastData.postValue(item)
                Log.i("MyTag", "forecast: ${_forecastData.value}")
            } catch (e: Exception) {
                loadingState.postValue(LoadingState.NetworkError)
            }
        }
    }

    fun getHours(weatherId: String, date: String) {
        viewModelScope.launch {
            try {
                println("getting hours in vm")
                _hoursData.value = repository.getHours(weatherId, date)
                //Log.i("HOURS", "${hoursData.value}")
            } catch (e: Exception) {
                loadingState.postValue(LoadingState.NetworkError)
            }
        }


    }
}