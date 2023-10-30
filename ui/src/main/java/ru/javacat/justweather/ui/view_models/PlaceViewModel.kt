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
import ru.javacat.justweather.domain.models.SearchLocation
import ru.javacat.justweather.ui.LoadingState
import ru.javacat.justweather.ui.SingleLiveEvent
import javax.inject.Inject

@HiltViewModel
class PlaceViewModel @Inject constructor(
    private val repository: ru.javacat.justweather.domain.repos.Repository,
) : ViewModel() {

    val allWeathersFlow = repository.allWeathers.asLiveData(viewModelScope.coroutineContext)

    //val currentWeatherFlow = repository.currentWeatherFlow.asLiveData(viewModelScope.coroutineContext)

    private var _foundLocations = MutableLiveData<List<SearchLocation>>()
    val foundLocations: LiveData<List<SearchLocation>>
        get() = _foundLocations

//    private var _allWeathers = MutableLiveData<List<Weather>?>()
//    val allWeathers: LiveData<List<Weather>?>
//        get() = _allWeathers

    val loadingState = SingleLiveEvent<LoadingState>()

    init {

        //getAllWeathers()
    }

//    private fun getAllWeathers() {
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                val result = repository.getAllWeathers()
//                _allWeathers.postValue(result)
//            } catch (e: ApiError) {
//                loadingState.postValue(LoadingState.InputError)
//            }
//        }
//    }

    suspend fun updateDb(){
        viewModelScope.launch(Dispatchers.IO) {
            //получаем координаты всех городов в БД
            val lats:List<Double>? = repository.getAllWeathers()?.map { it.location.lat }
            val longs: List<Double>? = repository.getAllWeathers()?.map { it.location.lon }

            Log.i("MyTag:", "lats: ${lats?.size}")
            Log.i("MyTag:", "longs: ${longs?.size}")

            //получаем Id текущего города
            val previousCurrentId = repository.getCurrentWeather()?.id
            val previousCurrentName = repository.getCurrentWeather()?.location?.name
            Log.i("MyTag", "ГОРОД: $previousCurrentName")
            Log.i("MyTag", "id: $previousCurrentId")

            if (!lats.isNullOrEmpty() && !longs.isNullOrEmpty()){
                val pairList = lats.zip(longs)
                repository.clearDbs()
                for (pair in pairList){
                    //добавляем в параметр айди текущего города, чтобы в обновлении городов снова его вставить
                    repository.fetchLocationDetails("${pair.first},${pair.second}", previousCurrentId.toString())
                }
            }
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
        //_foundLocations.value = emptyList()
    }
}