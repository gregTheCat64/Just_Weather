package ru.javacat.justweather.data.impl

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import ru.javacat.justweather.data.db.dao.WeatherDao
import ru.javacat.justweather.data.mapper.toDbAlert
import ru.javacat.justweather.data.mapper.toDbForecastday
import ru.javacat.justweather.data.mapper.toDbHour
import ru.javacat.justweather.data.mapper.toDbWeather
import ru.javacat.justweather.data.mapper.toModel
import ru.javacat.justweather.data.network.ApiService
import ru.javacat.justweather.data.toBase64
import ru.javacat.justweather.domain.models.Hour
import ru.javacat.justweather.domain.models.SearchLocation
import ru.javacat.justweather.domain.repos.Repository
import ru.javacat.justweather.domain.models.Weather
import ru.javacat.justweather.util.apiRequest
import javax.inject.Inject
import javax.inject.Singleton



@Singleton
class RepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val dao: WeatherDao,
) : Repository {

    //override var hoursFlow: Flow<List<Hour>>? = null

//    override val weatherFlow: Flow<Weather?> =
//        dao.getCurrent().map {
//            it.firstOrNull()
//        }.map {
//            it?.toModel()
//        }

    override val allWeathers: Flow<List<Weather?>> =
        dao.getAll().map { it.map { it.toModel() } }


    override val currentWeatherFlow: Flow<Weather?> =
        dao.getCurrent().map {
            it.firstOrNull()
        }.map {
            it?.toModel()
        }

    override suspend fun getCurrentWeather(name: String): Weather? {
        Log.i("MyTag", "Getting weahter in repo")
        val daores = dao.getByName(name).firstOrNull()?.toModel()
        Log.i("daores:", "daores: ${daores?.location}")
        return daores
    }

    override suspend fun fetchLocationDetails(name: String) {
        Log.i("Repo", "loadingData")
        val weatherResponse = apiRequest {
            apiService.getByName(name)
        }

        Log.i("MyTag", "weather: ${weatherResponse.location}")
        val locationName = weatherResponse.location.name
        val region = weatherResponse.location.region
        val weatherId = (locationName + region).toBase64()

        //анчекаем текущий город
        dao.unCheckCurrents()

        //получаем таблички:
        val alerts = weatherResponse.alerts.alert.map {
            it.toDbAlert(weatherId)
        }

        val forecasts = weatherResponse.forecast.forecastday.map {
            it.toDbForecastday(weatherId)
        }

        val weather = weatherResponse.toDbWeather(weatherId)
        weather.isCurrent = true

        val hours = weatherResponse.forecast.forecastday.map { forecastdays ->
            forecastdays.hour.map {
                it.toDbHour(weatherId, forecastdays.date)
            }
        }.flatten()


        dao.insert(
            weather,
            alerts,
            forecasts,
            hours
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

    override suspend fun getHours(weatherId: String, date: String): List<Hour> {
        println("getting hours")

        val result = dao.getHours(weatherId, date).map {list->
          list.toModel()
        }

        return  result
    }

    override suspend fun removeById(id: String) {
        dao.removeById(id)
    }
}