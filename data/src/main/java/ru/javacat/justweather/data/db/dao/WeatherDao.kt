package ru.javacat.justweather.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import ru.javacat.justweather.data.db.entities.DbAlert
import ru.javacat.justweather.data.db.entities.DbForecastday
import ru.javacat.justweather.data.db.entities.DbHour
import ru.javacat.justweather.data.db.entities.DbLocation
import ru.javacat.justweather.data.db.entities.DbWeather
import ru.javacat.justweather.data.db.entities.DbWeatherWithForecastsAndAlerts

@Dao
interface WeatherDao {
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(
        weather: DbWeather,
        location: DbLocation,
        alerts: List<DbAlert>,
        forecasts: List<DbForecastday>,
        hours: List<DbHour>,
    )

    //@Insert(onConflict = OnConflictStrategy.REPLACE)
    @Upsert()
    suspend fun update(
        weather: DbWeather,
        alerts: List<DbAlert>,
        forecasts: List<DbForecastday>,
        hours: List<DbHour>,
    )

    @Query("UPDATE weathers_table SET isCurrent = 0")
    fun unCheckCurrents()

    @Query("UPDATE weathers_table SET isLocated = 0")
    fun unCheckLocated()

    @Query("UPDATE weathers_table  SET positionId=positionId+1 WHERE id = :locId")
    fun increaseId(locId: String)

    @Query("UPDATE weathers_table  SET positionId=positionId-1 WHERE id = :locId")
    fun decreaseId(locId: String)

    @Transaction
    @Query("SELECT * FROM weathers_table WHERE id = :locId")
    suspend fun getByLocationId(locId: String): DbWeatherWithForecastsAndAlerts?

    @Transaction
    @Query("SELECT * FROM weathers_table  ORDER BY positionId")
    fun getAll(): Flow<List<DbWeatherWithForecastsAndAlerts>>

    @Transaction
    @Query("SELECT * FROM weathers_table")
    fun getAllWeathers(): List<DbWeatherWithForecastsAndAlerts>

    @Transaction
    @Query("SELECT * FROM weathers_table WHERE isCurrent = 1")
    fun getCurrentFlow(): Flow<DbWeatherWithForecastsAndAlerts?>

    @Transaction
    @Query("SELECT * FROM weathers_table WHERE isCurrent = 1")
    suspend fun getCurrent(): DbWeatherWithForecastsAndAlerts?

    @Transaction
    @Query("UPDATE weathers_table SET isCurrent = 1 WHERE id = :weatherId")
    suspend fun setCurrent(weatherId: String)

    @Query("SELECT * FROM hours_table WHERE forecastDate = :date AND weatherId = :weatherId "
    )
    suspend fun getHours(weatherId: String, date: String): List<DbHour>

    @Transaction
    @Query("DELETE FROM weathers_table WHERE id = :id")
    suspend fun removeById(id: String)

    @Transaction
    @Query("DELETE FROM weathers_table")
    suspend fun clearDb()

    @Query("DELETE FROM forecast_days_table WHERE date(date) < date(:currentDate)")
    suspend fun clearOldForecastDays(currentDate: String)

    @Query("DELETE FROM hours_table WHERE date(forecastDate) < date(:currentDate)")
    suspend fun clearOldHours(currentDate: String)

    @Query("DELETE FROM alerts_table")
    suspend fun clearAlertsDb()
    @Query("DELETE FROM hours_table")
    suspend fun clearHoursDb()
    @Query("DELETE FROM forecast_days_table")
    suspend fun clearForecastDaysDb()
}