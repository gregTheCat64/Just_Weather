package ru.javacat.justweather.ui.view_models

import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.javacat.justweather.ApiError
import ru.javacat.justweather.NetworkError
import ru.javacat.justweather.repository.CurrentPlaceRepository
import ru.javacat.justweather.repository.Repository
import ru.javacat.justweather.ui.LoadingState
import javax.inject.Inject

@HiltViewModel
class ActivityViewModel @Inject constructor(
    private val repository: Repository,
    private val currentPlaceRepository: CurrentPlaceRepository
): ViewModel() {

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
                Log.i("MyTag", "saving ${it?.location?.name}")
                it?.location?.let { location -> currentPlaceRepository.saveCurrentPlace(location) }
            }
        }
    }

}