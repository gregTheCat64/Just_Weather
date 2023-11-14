package ru.javacat.justweather.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ru.javacat.justweather.data.db.entities.*

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

    @Update
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

    @Query("SELECT * FROM weathers_table WHERE isCurrent = 1")
    suspend fun getCurrent(): List<DbWeatherWithForecastsAndAlerts>

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

    @Transaction
    @Query("DELETE FROM hours_table")
    suspend fun clearHoursDb()

}