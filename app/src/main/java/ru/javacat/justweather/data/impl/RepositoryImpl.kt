package ru.javacat.justweather.data.impl

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.javacat.justweather.data.db.dao.WeatherDao
import ru.javacat.justweather.data.mapper.toDbAlert
import ru.javacat.justweather.data.mapper.toDbForecastday
import ru.javacat.justweather.data.mapper.toDbHour
import ru.javacat.justweather.data.mapper.toDbWeather
import ru.javacat.justweather.data.mapper.toModel
import ru.javacat.justweather.data.network.ApiService
import ru.javacat.justweather.data.toBase64
import ru.javacat.justweather.domain.models.SearchLocation
import ru.javacat.justweather.domain.repos.Repository
import ru.javacat.justweather.domain.models.Weather
import ru.javacat.justweather.util.apiRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val dao: WeatherDao
) : Repository {



    override var weatherFlow: Flow<Weather>? = null
//        dao.getCurrent()
//        ?.map {
//        it.toModel()
//    }

    init {
        updateWeather()
    }


    fun updateWeather(){
        val daoRes = dao.getCurrent()
        //println(daoRes)
        if (daoRes!= null) {
            weatherFlow = daoRes.map { it.toModel() }
        }
    }
//    override val forecastFlow: Flow<List<ForecastdayWithHours>>
//        get() = dao.getForecast().map { it.map { it.toModel() } }

    override suspend fun fetchLocationDetails(name: String) {
        Log.i("Repo", "loadingData")
        val weatherResponse = apiRequest {
            apiService.getByName(name)
        }
        Log.i("MyTag","weather: ${weatherResponse.location}")
        val name = weatherResponse.location.name
        val region = weatherResponse.location.region
        val weatherId = (name + region).toBase64()
        val alerts = weatherResponse.alerts.alert.map {
            it.toDbAlert(weatherId) }

        val forecasts = weatherResponse.forecast.forecastday.map {
            it.toDbForecastday(weatherId)
        }

        val weather = weatherResponse.toDbWeather(weatherId)
        val hours = weatherResponse.forecast
            .forecastday.map {
                it.hour.map { it.toDbHour(weatherId, weather.current.last_updated) }
            }.flatten()


        dao.insert(weather,
            alerts, forecasts
            //hours
        )

    }



    override suspend fun findLocation(name: String): List<SearchLocation> {
        val result = apiRequest {
            apiService.findLocation(name)
        }
        //_weatherFlow.emit(result)
        Log.i("MyTag", "emiting result: ${result}")
        return result
    }


}