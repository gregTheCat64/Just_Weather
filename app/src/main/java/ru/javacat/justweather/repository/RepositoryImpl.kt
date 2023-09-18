package ru.javacat.justweather.repository

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.javacat.justweather.api.ApiService
import ru.javacat.justweather.response_models.Weather
import ru.javacat.justweather.util.apiRequest
import javax.inject.Inject


class RepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : Repository {

//    private val _weatherFlow = MutableSharedFlow<Weather?>(1,1,BufferOverflow.SUSPEND)
//    override val weatherFlow: SharedFlow<Weather?>
//        get() = _weatherFlow

    private val _weatherFlow = MutableStateFlow<Weather?>(null)
    override val weatherFlow: StateFlow<Weather?>
        get() = _weatherFlow

    override suspend fun loadByName(name: String, daysCount: Int): Weather? {
       val result =  apiRequest {
            apiService.getByName(name, daysCount)
        }
        _weatherFlow.emit(result)
        Log.i("MyTag", "emiting result: ${result.location}")
        return result
    }

    suspend fun findByName(name: String, daysCount: Int): Weather? {
        val result =  apiRequest {
            apiService.getByName(name, daysCount)
        }
        _weatherFlow.emit(result)
        Log.i("MyTag", "emiting result: ${result.location}")
        return result
    }


}