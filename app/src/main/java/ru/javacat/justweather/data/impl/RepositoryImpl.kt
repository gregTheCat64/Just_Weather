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
import ru.javacat.justweather.domain.models.ForecastdayWithHours
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

//    private val _weatherFlow = MutableSharedFlow<Weather?>(1,1,BufferOverflow.SUSPEND)
//    override val weatherFlow: SharedFlow<Weather?>
//        get() = _weatherFlow

    //private val _weatherFlow = MutableStateFlow<Weather?>(null)
    override val weatherFlow: Flow<Weather?> = dao.getCurrent().map { it.toModel() }
        //.map { it.toModel() }
        //get() = _weatherFlow

    override val forecastFlow: Flow<List<ForecastdayWithHours>>
        get() = dao.getForecast().map { it.map { it.toModel() } }

    override suspend fun fetchLocationDetails(name: String) {
        val weatherResponse = apiRequest {
            apiService.getByName(name)
        }
        //_weatherFlow.emit(result.toDb())
        //Log.i("MyTag", "emiting result: ${result.location}")
        val name = weatherResponse.location.name
        val region = weatherResponse.location.region
        val weatherId = (name + region).toBase64()
        val alerts = weatherResponse.alerts.map { it.toDbAlert(weatherId) }

        val forecastDate = weatherResponse.forecasts[0].date
        val forecastId = (name + forecastDate).toBase64()
        val forecasts = weatherResponse.forecasts.map { it.toDbForecastday(weatherId, forecastId) }

        val weather = weatherResponse.toDbWeather()
        val hours = weatherResponse.forecasts.map { it.hour.map { it.toDbHour(forecastId) } }.flatten()

        dao.insert(weather, alerts, forecasts, hours)

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