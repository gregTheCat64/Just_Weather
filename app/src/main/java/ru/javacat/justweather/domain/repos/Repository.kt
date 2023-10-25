package ru.javacat.justweather.domain.repos

import kotlinx.coroutines.flow.Flow
import ru.javacat.justweather.domain.models.Hour
import ru.javacat.justweather.domain.models.SearchLocation
import ru.javacat.justweather.domain.models.Weather

interface Repository {
    //val allWeathers: Flow<List<Weather?>>
    val currentWeatherFlow: Flow<Weather?>
    //val hoursFlow: Flow<List<Hour>>?

    //val forecastFlow: Flow<List<ForecastdayWithHours>>

    suspend fun getAllWeathers(): List<Weather>?
    suspend fun fetchLocationDetails(name: String, currentId: String)

    suspend fun findLocation(name: String): List<SearchLocation>

    suspend fun getCurrentWeather(name: String): Weather?

    suspend fun setCurrentWeather(weather: Weather)

    suspend fun getHours(weatherId: String, date: String): List<Hour>

    suspend fun removeById(id: String)

    suspend fun  updateDb()



}