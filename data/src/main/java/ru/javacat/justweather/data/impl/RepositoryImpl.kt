package ru.javacat.justweather.data.impl

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.javacat.justweather.data.apiRequest
import ru.javacat.justweather.data.db.dao.WeatherDao
import ru.javacat.justweather.data.dbQuery
import ru.javacat.justweather.data.mapper.toDbAlert
import ru.javacat.justweather.data.mapper.toDbForecastday
import ru.javacat.justweather.data.mapper.toDbHour
import ru.javacat.justweather.data.mapper.toDbWeather
import ru.javacat.justweather.data.mapper.toModel
import ru.javacat.justweather.data.network.ApiService
import ru.javacat.justweather.data.toBase64
import ru.javacat.justweather.domain.models.suggestModels.SuggestLocationList
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class RepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val dao: WeatherDao,
) : ru.javacat.justweather.domain.repos.Repository {

    //override var hoursFlow: Flow<List<Hour>>? = null

//    override val weatherFlow: Flow<Weather?> =
//        dao.getCurrent().map {
//            it.firstOrNull()
//        }.map {
//            it?.toModel()
//        }

    override val allWeathers: Flow<List<ru.javacat.justweather.domain.models.Weather>> =
        dao.getAll().map { it.map { it.toModel() } }


    override val currentWeatherFlow: Flow<ru.javacat.justweather.domain.models.Weather?> =
        dao.getCurrentFlow().map {
            it.firstOrNull()
        }.map {
            it?.toModel()
        }

    override suspend fun getAllWeathers(): List<ru.javacat.justweather.domain.models.Weather>? {
        return dbQuery { dao.getAllWeathers().map { it.toModel() } }
    }

    override suspend fun getCurrentWeather(): ru.javacat.justweather.domain.models.Weather? {
        Log.i("MyTag", "Getting weahter in repo")
//        val daores =
//        Log.i("daores:", "daores: ${daores?.location}")
        return dbQuery { dao.getCurrent().firstOrNull()?.toModel() }
    }

    override suspend fun setCurrentWeather(weather: ru.javacat.justweather.domain.models.Weather) {
        TODO("Not yet implemented")
    }

    override suspend fun fetchLocationDetails(name: String, currentId: String) {
        Log.i("Repo", "loadingData")
        val weatherResponse = apiRequest {
            apiService.getByName(name)
        }

        Log.i("MyTag", "weather: ${weatherResponse.location}")
        val locationName = weatherResponse.location.name
        val region = weatherResponse.location.region
        val weatherId = (locationName + region).toBase64()

        //анчекаем текущие города
        dbQuery { dao.unCheckCurrents() }

        //получаем таблички:
        val alerts = weatherResponse.alerts.alert.map {
            it.toDbAlert(weatherId)
        }

        val forecasts = weatherResponse.forecast.forecastday.map {
            it.toDbForecastday(weatherId)
        }

        val weather = weatherResponse.toDbWeather(weatherId)
        Log.i("MyTag", "$currentId and $weatherId")
        weather.isCurrent = currentId == weatherId || currentId == "newCurrent"
        Log.i("MyTag", "isCurrent: ${weather.isCurrent}")

        val hours = weatherResponse.forecast.forecastday.map { forecastdays ->
            forecastdays.hour.map {
                it.toDbHour(weatherId, forecastdays.date)
            }
        }.flatten()

         Log.i("MyTag", "INSERTING TO DB")
        dbQuery { dao.insert(
            weather,
            alerts,
            forecasts,
            hours
        ) }

    }


    override suspend fun findLocation(name: String): SuggestLocationList {
        val result = apiRequest {
            apiService.suggestLocation(name)
        }
        //_weatherFlow.emit(result)
        Log.i("MyTag", "emiting result: ${result}")
        return result
    }

    override suspend fun getHours(weatherId: String, date: String): List<ru.javacat.justweather.domain.models.Hour> {
        println("getting hours")

        val result = dao.getHours(weatherId, date).map {list->
          list.toModel()
        }

        return  result
    }

    override suspend fun removeById(id: String) {
        dao.removeById(id)
    }

    override suspend fun clearDbs() {
        dao.clearDb()
        dao.clearHoursDb()
    }

}