package ru.javacat.justweather.data.impl

import android.util.Log
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
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
import ru.javacat.justweather.util.dbQuery
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

//    override val allWeathers: Flow<List<Weather?>> =
//        dao.getAll().map { it.map { it.toModel() } }


    override val currentWeatherFlow: Flow<Weather?> =
        dao.getCurrentFlow().map {
            it.firstOrNull()
        }.map {
            it?.toModel()
        }

    override suspend fun getAllWeathers(): List<Weather>? {
        return dbQuery { dao.getAll()?.map { it.toModel() } }
    }

    override suspend fun getCurrentWeather(name: String): Weather? {
        Log.i("MyTag", "Getting weahter in repo")
//        val daores =
//        Log.i("daores:", "daores: ${daores?.location}")
        return dbQuery { dao.getByName(name).firstOrNull()?.toModel() }
    }

    override suspend fun setCurrentWeather(weather: Weather) {
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

        dbQuery { dao.insert(
            weather,
            alerts,
            forecasts,
            hours
        ) }

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

    override suspend fun updateDb() {
        //получаем координаты всех городов в БД
        val lats:List<Double>? = getAllWeathers()?.map { it.location.lat }
        val longs: List<Double>? = getAllWeathers()?.map { it.location.lon }

        Log.i("lats:", "${lats?.size}")
        Log.i("longs:", "${longs?.size}")

        //получаем Id текущего города
        val previousCurrentId = dao.getCurrent().firstOrNull()?.weather?.id
        val previousCurrentName = dao.getCurrent().firstOrNull()?.weather?.location?.name
        Log.i("MyTag", "ГОРОД: $previousCurrentName")

        //обновляем табличку:
        if (!lats.isNullOrEmpty() && !longs.isNullOrEmpty()){
            val pairList = lats.zip(longs)
            dao.clearDb()
            dao.clearHoursDb()

            for (pair in pairList){
                //добавляем в параметр айди текущего города, чтобы в обновлении городов снова его вставить
                    fetchLocationDetails("${pair.first},${pair.second}", previousCurrentId.toString())
            }
        }

    }

}