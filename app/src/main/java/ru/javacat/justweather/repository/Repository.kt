package ru.javacat.justweather.repository

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import ru.javacat.justweather.response_models.Weather

interface Repository {
    val weatherFlow: StateFlow<Weather?>
    suspend fun loadByName(name: String, daysCount: Int): Weather?

    suspend fun findByName(name: String, daysCount: Int): Weather?
}