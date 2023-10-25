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
import ru.javacat.justweather.domain.models.Weather

@Dao
interface WeatherDao {
    @Transaction
    @Upsert
    suspend fun insert(
        weather: DbWeather,
        alerts: List<DbAlert>,
        forecasts: List<DbForecastday>,
        hours: List<DbHour>,
    )

    @Query("UPDATE weathers_table SET isCurrent = 0")
    fun unCheckCurrents()

    @Transaction
    @Query("SELECT * FROM weathers_table WHERE name = :locId"

            )
    suspend fun getByName(locId: String): List<DbWeatherWithForecastsAndAlerts>

//    @Transaction
//    @Query("SELECT * FROM weathers_table WHERE id = :locId AND date = :forecastDate"
//
//    )
//    fun getForecast(locId: String, forecastDate: String): Flow<List<DbWeatherWithForecastsAndAlerts>>

    @Query("SELECT * FROM weathers_table")
    suspend fun getAll(): List<DbWeatherWithForecastsAndAlerts>?

    @Query("SELECT * FROM weathers_table WHERE isCurrent = 1")
    fun getCurrentFlow(): Flow<List<DbWeatherWithForecastsAndAlerts>>

    @Query("SELECT * FROM weathers_table WHERE isCurrent = 1")
    suspend fun getCurrent(): List<DbWeatherWithForecastsAndAlerts>

    @Query("UPDATE weathers_table SET isCurrent = 1 WHERE id = :id")
    suspend fun setCurrent(id: String)

    @Query("SELECT * FROM hours_table WHERE forecastDate = :date AND weatherId = :weatherId "
    )
    suspend fun getHours(weatherId: String, date: String): List<DbHour>


    @Transaction
    @Query("DELETE FROM weathers_table WHERE id = :id")
    suspend fun removeById(id: String)

    @Transaction
    @Query("DELETE FROM weathers_table")
    suspend fun clearDb()

    @Transaction
    @Query("DELETE FROM hours_table")
    suspend fun clearHoursDb()

}