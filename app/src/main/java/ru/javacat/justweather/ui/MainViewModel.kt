package ru.javacat.justweather.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.javacat.justweather.NetworkError
import ru.javacat.justweather.repository.Repository
import ru.javacat.justweather.repository.RepositoryImpl
import ru.javacat.justweather.response_models.Weather

class MainViewModel(application: Application): AndroidViewModel(application) {

    private val repository: Repository = RepositoryImpl()

    private val _data = MutableLiveData<Weather>()
    val data: LiveData<Weather>
        get() = _data


    fun loadWeatherByName(name: String){
        viewModelScope.launch {
            try {
                val response = repository.getByName(name)
                if (response != null) {
                    _data.value = response!!
                    println(response.current.temp_c)
                } else return@launch
            } catch (e: NetworkError){
                e.printStackTrace()
            }
        }
    }
}