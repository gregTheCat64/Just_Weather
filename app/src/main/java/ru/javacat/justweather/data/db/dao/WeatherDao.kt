package ru.javacat.justweather.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import ru.javacat.justweather.data.db.entities.DbAlert
import ru.javacat.justweather.data.db.entities.DbWeatherWithForecastsAndAlerts
import ru.javacat.justweather.data.db.entities.DbForecastday
import ru.javacat.justweather.data.db.entities.DbHour
import ru.javacat.justweather.data.db.entities.DbWeather

@Dao
interface WeatherDao {

    @Transaction
    @Upsert
    suspend fun insert(
        weather: DbWeather,
        alerts: List<DbAlert>,
        forecasts: List<DbForecastday>,
        //hours: List<DbHour>,
    )

    @Transaction
    @Query("SELECT * FROM weathers_table "
            //"WHERE id = :locId"
            )
    fun getCurrent(): Flow<DbWeatherWithForecastsAndAlerts>?

    @Transaction
    @Query("SELECT * FROM weathers_table "
    //        "WHERE weatherId = :locId AND date = :forecastDate"
    )
    fun getForecast(): Flow<List<DbWeatherWithForecastsAndAlerts>>
}