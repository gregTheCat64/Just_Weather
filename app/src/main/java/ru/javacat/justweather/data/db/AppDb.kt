package ru.javacat.justweather.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.javacat.justweather.data.db.dao.WeatherDao
import ru.javacat.justweather.data.db.entities.DbAlert
import ru.javacat.justweather.data.db.entities.DbForecastday
import ru.javacat.justweather.data.db.entities.DbHour
import ru.javacat.justweather.data.db.entities.DbWeather

@Database(
    entities = [
        DbWeather::class,
        DbAlert::class,
        DbForecastday::class,
        DbHour::class
    ],
    version = 1
)

abstract class AppDb : RoomDatabase() {
    abstract val weatherDao: WeatherDao
}