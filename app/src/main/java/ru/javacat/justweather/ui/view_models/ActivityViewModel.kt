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
import ru.javacat.justweather.repository.CurrentPlaceRepository
import ru.javacat.justweather.repository.Repository
import javax.inject.Inject

@HiltViewModel
class ActivityViewModel @Inject constructor(
    private val repository: Repository,
    private val currentPlaceRepository: CurrentPlaceRepository
): ViewModel() {

    private val _appMinimized = MutableLiveData(false)
    val appMinimized: LiveData<Boolean>
        get() = _appMinimized

    fun getCurrentPlace(){
        viewModelScope.launch(Dispatchers.IO){
            try {
                val currentLocation = currentPlaceRepository.getCurrentPlace()
                val coords = currentLocation?.lat.toString() +","+ currentLocation?.lon.toString()
                repository.loadByName(coords, 3)?: throw NetworkError

            } catch (e: ApiError) {

                Log.i("MyTag", "ОШИБКА: ${e.code}")
            } catch (e: NetworkError) {

                Log.i("MyTag", "ОШИБКА: NETWORK")
            }
        }
    }

    fun saveCurrentPlace(){
        viewModelScope.launch{
            repository.weatherFlow.collect{
                it?.location?.let { location -> currentPlaceRepository.saveCurrentPlace(location) }
            }
        }
    }

    fun setMinimized(state: Boolean){
        _appMinimized.value = state
        println("minimized: ${_appMinimized.value}")
    }
}