package ru.javacat.justweather.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import ru.javacat.justweather.ApiError
import ru.javacat.justweather.NetworkError
import ru.javacat.justweather.UnknownError
import ru.javacat.justweather.api.Api
import ru.javacat.justweather.response_models.Weather
import ru.javacat.justweather.util.apiRequest
import java.io.IOException
import java.lang.Exception

class RepositoryImpl: Repository {

    private val _weatherFlow = MutableSharedFlow<Weather?>(1)
    override val weatherFlow: SharedFlow<Weather?>
        get() = _weatherFlow

    override suspend fun loadByName(name: String, daysCount: Int) {
       val result =  apiRequest {
            Api.service.getByName(name, daysCount)
        }
        _weatherFlow.emit(result)
//        try {
//            val response = Api.service.getByName(name, daysCount)
//            if (!response.isSuccessful) {
//                throw ApiError(response.code(), response.message())
//            }
//            return response.body()
//        } catch (e: IOException){
//            throw NetworkError
//        } catch (e: Exception) {
//            throw UnknownError
//        }
        //return Api.service.getByName(name, daysCount).body()
    }
}