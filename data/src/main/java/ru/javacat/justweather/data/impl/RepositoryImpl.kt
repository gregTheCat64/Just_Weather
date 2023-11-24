package ru.javacat.justweather.data.impl

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.javacat.justweather.data.apiRequest
import ru.javacat.justweather.data.db.dao.WeatherDao
import ru.javacat.justweather.data.db.entities.DbHour
import ru.javacat.justweather.data.dbQuery
import ru.javacat.justweather.data.mapper.toDb
import ru.javacat.justweather.data.mapper.toDbAlert
import ru.javacat.justweather.data.mapper.toDbForecastday
import ru.javacat.justweather.data.mapper.toDbHour
import ru.javacat.justweather.data.mapper.toDbWeather
import ru.javacat.justweather.data.mapper.toModel
import ru.javacat.justweather.data.network.ApiService
import ru.javacat.justweather.data.toBase64
import ru.javacat.justweather.domain.models.geoCoderModels.GeoObjectCollection
import ru.javacat.justweather.domain.models.geoCoderModels.Point
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
            it?.toModel()
        }

    override suspend fun getAllWeathers(): List<ru.javacat.justweather.domain.models.Weather>? {
        return dbQuery { dao.getAllWeathers().map { it.toModel() } }
    }

    override suspend fun getCurrentWeather(): ru.javacat.justweather.domain.models.Weather? {
        Log.i("MyTag", "Getting weahter in repo")
        return dbQuery { dao.getCurrent().firstOrNull()?.toModel() }
    }

    override suspend fun updateWeatherById(locationId: String, setCurrent: Boolean) {
        //получаем запись из БД
        val dbWeather = dbQuery { dao.getByLocationId(locationId) }
        //Log.i("MyTag", "dbweather: ${dbWeather.weather.positionId}")
        val weatherList = dbQuery { dao.getAllWeathers() }
        Log.i("Positions", "allweatherListIds: ${weatherList.map { it.weather.positionId }}" )

        //анчекаем текущие города
        unCheckCurrent()

        val coords = "${dbWeather?.location?.lat},${dbWeather?.location?.lon}"

        //получаем погоду из сети
        val weatherResponse = apiRequest {
            apiService.getByName(coords)
        }

        //восстанавливаем айдишник
        val locationName = weatherResponse.location.name
        val region = weatherResponse.location.region
        val weatherId = (locationName + region).toBase64()

        //формируем табличку Алертов
        val alerts = weatherResponse.alerts.alert.map {
            it.toDbAlert(weatherId)
        }

        //формируем табличку Прогнозов
        val forecasts = weatherResponse.forecast.forecastday.map {
            it.toDbForecastday(weatherId)
        }

        //формируем табличку Погоды
        val weather = weatherResponse.toDbWeather(weatherId)

        //восстанавливаем параметры из БД
        weather.isLocated = dbWeather?.weather?.isLocated == true
        if (setCurrent) {
            weather.isCurrent = true
        } else
        {
            weather.isCurrent = dbWeather?.weather?.isCurrent == true
        }

        val listToChangePosition =
            weatherList.filter {
                it.weather.positionId != 0 && it.weather.positionId < dbWeather?.weather?.positionId!!
            }

        Log.i("Positions", "listToChange: ${listToChangePosition.map { it.weather.positionId }}")
        Log.i("Positions", "currentPos: ${dbWeather?.weather?.positionId}")

        // двигаем города, если это не нулевой город и если стоит флаг - сетКарент
        if (setCurrent && !weather.isLocated) {
            for (i in listToChangePosition){
                Log.i("Positions", "changinPos: ${i.weather.positionId}")
                changePositionId(i.weather.id)
                Log.i("Positions", "now pos is: ${i.weather.positionId}")
            }
            weather.positionId = 1
        } else {
            weather.positionId = dbWeather?.weather?.positionId!!
        }



        //формируем табличку Часиков
        val hours = weatherResponse.forecast.forecastday.map { forecastdays ->
            forecastdays.hour.map {
                it.toDbHour(weatherId, forecastdays.date)
            }
        }.flatten()

        //получаем каждый 3й час
        val everyThirdHourList = mutableListOf<DbHour>()
        for (i in hours.indices){
            if (i%3==0){
                everyThirdHourList.add(hours[i])
            }
        }

        //вставляем таблички в БД
        dbQuery { dao.update(
            weather,
            alerts,
            forecasts,
            everyThirdHourList
        ) }
    }


    override suspend fun getNewPlaceDetails(name: String, isLocated: Boolean, localTitle: String, localSubtitle: String, locationsLimit: Int) {
        Log.i("Repo", "loadingData")
        val weatherResponse = apiRequest {
            apiService.getByName(name)
        }

        val weatherList = dbQuery { dao.getAllWeathers() }

        if (weatherList.size>=4 && !isLocated) {
            val lastLocationId = weatherList.findLast { it.weather.positionId == locationsLimit }?.weather?.id
            dbQuery {
                if (lastLocationId != null) {
                    dao.removeById(lastLocationId)
                }
            }
        }


        Log.i("MyTag", "1weatherListsize: ${weatherList.size}")
        val locationName = weatherResponse.location.name
        val region = weatherResponse.location.region
        val weatherId = (locationName + region).toBase64()

        //анчекаем текущие города
        unCheckCurrent()

        //получаем таблички:
        val alerts = weatherResponse.alerts.alert.map {
            it.toDbAlert(weatherId)
        }

        val forecasts = weatherResponse.forecast.forecastday.map {
            it.toDbForecastday(weatherId)
        }

        val weather = weatherResponse.toDbWeather(weatherId)
        //Log.i("MyTag", "$currentId and $weatherId")

        val location = weatherResponse.location.toDb(weatherId)

        //определяем список для изменения положения,
        //погода с меньшим positionId и не равным нулю, прибавляет единицу

        Log.i("MyTag", "currentPosId: ${weather.positionId}")
        //Log.i("MyTag", "otherPosIds: ${weatherList.map { it.weather.positionId }}")

        val listToChangePosition =
            weatherList.filter {
                it.weather.positionId != 0
            }


        Log.i("MyTag", "lesspositionlist: ${listToChangePosition.map { it.weather.positionId }}")


        if (isLocated) {
            for (i in weatherList){
                Log.i("MyTag","changin position at ${i.location?.name}")
                changePositionId(i.weather.id)
            }
            weather.isLocated = true
            weather.positionId = 0
        } else {
            //Двигаем номер позиции
            
            for (i in listToChangePosition){
                Log.i("MyTag","changin position at ${i.location?.name}")
                changePositionId(i.weather.id)
            }
            weather.positionId = 1
        }

        weather.isCurrent = true
        location.localTitle = localTitle
        location.localSubtitle = localSubtitle

        val hours = weatherResponse.forecast.forecastday.map { forecastdays ->
            forecastdays.hour.map {
                it.toDbHour(weatherId, forecastdays.date)
            }
        }.flatten()

        val everyThirdHourList = mutableListOf<DbHour>()
        for (i in hours.indices){
            if (i%3==0){
                everyThirdHourList.add(hours[i])
            }
        }

         Log.i("MyTag", "INSERTING TO DB")
        dbQuery { dao.insert(
            weather,
            location,
            alerts,
            forecasts,
            everyThirdHourList
        ) }
    }


    override suspend fun findLocation(name: String): SuggestLocationList {
        val result = apiRequest {
            apiService.suggestLocation(name)
        }

        Log.i("MyTag", "result: ${result.results}")
        return result
    }

    override suspend fun getCoords(uri: String): Point {
        val result = apiRequest {
            apiService.getCoords(uri)
        }.response.GeoObjectCollection.featureMember[0].GeoObject.Point

        Log.i("MyTag", "position: ${result}")

        return result
    }

    override suspend fun getLocationByCoords(coords: String): GeoObjectCollection {
        val result = apiRequest {
            apiService.getLocationByCoords(coords)
        }.response.GeoObjectCollection
        return result
    }

    override suspend fun getHours(weatherId: String, date: String): List<ru.javacat.justweather.domain.models.Hour> {
        println("getting hours")

        val result = dao.getHours(weatherId, date).map {list->
          list.toModel()
        }
        return  result
    }


    override suspend fun unCheckLocated() {
        dbQuery { dao.unCheckLocated() }
    }

    override suspend fun unCheckCurrent() {
        dbQuery { dao.unCheckCurrents() }
    }

    override suspend fun changePositionId(locId: String) {
        dbQuery { dao.increaseId(locId) }
        Log.i("MyTag","position changed")
    }

    override suspend fun removeById(id: String) {
        val location = dbQuery { dao.getByLocationId(id) }
        val locatedPlace = dbQuery { dao.getAllWeathers().firstOrNull { it.weather.isLocated } }
        val listToChangePosition = dbQuery { dao.getAllWeathers() }.filter { it.weather.positionId > location?.weather?.positionId!! }
        Log.i("MyTag", "locatedplace: ${locatedPlace?.location?.name}")
        Log.i("MyTag", "locatedplaceId: ${locatedPlace?.weather?.id}")

        if (listToChangePosition.isNotEmpty()){
            for (i in listToChangePosition){
                dbQuery { dao.decreaseId(i.weather.id) }
            }
        }
        if (location?.weather?.isCurrent == true){
            dao.removeById(id)
            dbQuery { locatedPlace?.weather?.id?.let { dao.setCurrent(it) } }
            Log.i("MyTag", "locatedplaceId:")
            return
        }
        //locatedPlace?.weather?.id?.let { dao.setCurrent(it) }
        dao.removeById(id)
    }

    override suspend fun clearDbs() {
        dao.clearDb()
        dao.clearHoursDb()
    }

}