package ru.javacat.justweather.domain.repos

import kotlinx.coroutines.flow.Flow
import ru.javacat.justweather.domain.models.SearchLocation
import ru.javacat.justweather.domain.models.Weather

interface Repository {
    val weatherFlow: Flow<Weather>?

    //val forecastFlow: Flow<List<ForecastdayWithHours>>
    suspend fun fetchLocationDetails(name: String)

    suspend fun findLocation(name: String): List<SearchLocation>
}