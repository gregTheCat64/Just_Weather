package ru.javacat.justweather.repository

import kotlinx.coroutines.flow.StateFlow
import ru.javacat.justweather.response_models.SearchLocation
import ru.javacat.justweather.response_models.Weather

interface Repository {
    val weatherFlow: StateFlow<Weather?>
    suspend fun fetchLocationDetails(name: String, daysCount: Int): Weather?

    suspend fun findLocation(name: String): List<SearchLocation>
}