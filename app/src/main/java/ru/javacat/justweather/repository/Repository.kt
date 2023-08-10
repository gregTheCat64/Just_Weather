package ru.javacat.justweather.repository

import androidx.lifecycle.LiveData
import ru.javacat.justweather.response_models.Weather

interface Repository {
    //val data: LiveData<Weather>
    suspend fun getByName(name: String, daysCount: Int): Weather?
}