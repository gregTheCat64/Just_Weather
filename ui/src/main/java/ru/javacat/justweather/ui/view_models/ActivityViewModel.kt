package ru.javacat.justweather.ui.view_models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.javacat.justweather.domain.ApiError
import ru.javacat.justweather.domain.NetworkError
import javax.inject.Inject

@HiltViewModel
class ActivityViewModel @Inject constructor(
    private val repository: ru.javacat.justweather.domain.repos.Repository,
    private val currentPlaceRepository: ru.javacat.justweather.domain.repos.CurrentPlaceRepository
): ViewModel() {

    private val _appMinimized = MutableLiveData(false)
    val appMinimized: LiveData<Boolean>
        get() = _appMinimized

    fun getCurrentPlace(){
        viewModelScope.launch(Dispatchers.IO){
            try {
                val currentLocation = currentPlaceRepository.getFromPlacesList()
                val coords = currentLocation?.lat.toString() +","+ currentLocation?.lon.toString()
                if (currentLocation != null) {
                    repository.fetchLocationDetails(coords, "newCurrent")?: throw NetworkError
                }

            } catch (e: ApiError) {
                Log.i("MyTag", "ОШИБКА: ${e.code}")
            } catch (e: NetworkError) {
                Log.i("MyTag", "ОШИБКА: NETWORK")
            }
        }
    }

//    fun saveCurrentPlace(){
//        viewModelScope.launch{
//            repository.weatherFlow.collect{
//                it?.location?.let { location -> currentPlaceRepository.saveCurrentPlace(location) }
//            }
//        }
//    }

    fun setMinimized(state: Boolean){
        _appMinimized.value = state
        println("minimized: ${_appMinimized.value}")
    }
}