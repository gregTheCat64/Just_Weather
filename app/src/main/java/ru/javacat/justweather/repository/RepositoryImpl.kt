package ru.javacat.justweather.repository

import ru.javacat.justweather.ApiError
import ru.javacat.justweather.NetworkError
import ru.javacat.justweather.UnknownError
import ru.javacat.justweather.api.Api
import ru.javacat.justweather.response_models.Weather
import ru.javacat.justweather.util.apiRequest
import java.io.IOException
import java.lang.Exception

class RepositoryImpl: Repository {

    override suspend fun loadByName(name: String, daysCount: Int): Weather {
        return apiRequest {
            Api.service.getByName(name, daysCount)
        }
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