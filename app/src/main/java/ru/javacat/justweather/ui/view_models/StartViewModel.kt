package ru.javacat.justweather.ui.view_models

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.javacat.justweather.ApiError
import ru.javacat.justweather.NetworkError
import ru.javacat.justweather.repository.CurrentPlaceRepository
import ru.javacat.justweather.repository.Repository
import ru.javacat.justweather.response_models.Weather
import ru.javacat.justweather.ui.LoadingState
import ru.javacat.justweather.ui.SingleLiveEvent
import javax.inject.Inject

@HiltViewModel
class StartViewModel @Inject constructor(
    private val repository: Repository,
): ViewModel() {

    val weatherFlow: SharedFlow<Weather?> = repository.weatherFlow
    val loadingState = SingleLiveEvent<LoadingState>()

    fun findPlaceByLocation(name: String, daysCount: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            loadingState.postValue(LoadingState.Load)

            try {
                repository.loadByName(name, 3)?: throw NetworkError
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