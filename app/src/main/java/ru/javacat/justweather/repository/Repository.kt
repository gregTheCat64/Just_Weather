package ru.javacat.justweather.repository

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.SharedFlow
import ru.javacat.justweather.response_models.Weather

interface Repository {
    val weatherFlow: SharedFlow<Weather?>
    suspend fun loadByName(name: String, daysCount: Int)
}