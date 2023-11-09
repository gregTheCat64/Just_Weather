package ru.javacat.justweather.domain.repos

import kotlinx.coroutines.flow.Flow
import ru.javacat.justweather.domain.models.Hour
import ru.javacat.justweather.domain.models.Weather
import ru.javacat.justweather.domain.models.geoCoderModels.GeoObjectCollection
import ru.javacat.justweather.domain.models.geoCoderModels.Point
import ru.javacat.justweather.domain.models.suggestModels.SuggestLocationList

interface Repository {
    val allWeathers: Flow<List<Weather>>
    val currentWeatherFlow: Flow<Weather?>
    //val hoursFlow: Flow<List<Hour>>?

    //val forecastFlow: Flow<List<ForecastdayWithHours>>

    //suspend fun getAllWeathers(): List<Weather>?

    suspend fun getAllWeathers(): List<Weather>?
    suspend fun fetchLocationDetails(name: String, currentId: String, isLocated: Boolean, localTitle: String, localSubtitle: String)

    suspend fun findLocation(name: String): SuggestLocationList

    suspend fun getCoords(uri: String): Point

    suspend fun getLocationByCoords(coords: String): GeoObjectCollection

    suspend fun getCurrentWeather(): Weather?

    suspend fun setCurrentWeather(weather: Weather)

    suspend fun getHours(weatherId: String, date: String): List<Hour>

    suspend fun removeById(id: String)

    suspend fun  clearDbs()



}