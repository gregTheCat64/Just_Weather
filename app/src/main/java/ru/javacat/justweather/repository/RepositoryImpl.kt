package ru.javacat.justweather.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.javacat.justweather.ApiError
import ru.javacat.justweather.api.Api
import ru.javacat.justweather.response_models.Weather
import java.lang.Exception

class RepositoryImpl: Repository {

    override suspend fun getByName(name: String, daysCount: Int): Weather? {
        try {
            val response = Api.service.getByName(name, daysCount)
            return response.body()
        } catch (e: Exception){
            e.printStackTrace()
        }
        return null
    }
}