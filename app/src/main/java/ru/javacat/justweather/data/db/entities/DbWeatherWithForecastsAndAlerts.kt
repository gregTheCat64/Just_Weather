package ru.javacat.justweather.data.db.entities

import androidx.room.Embedded
import androidx.room.Relation

data class DbWeatherWithForecastsAndAlerts (
    @Embedded
    val weather: DbWeather,

    @Relation(
        parentColumn = "id",
        entityColumn = "weatherId"
    )
    val forecasts: List<DbForecastday>,

    @Relation(
        parentColumn = "id",
        entityColumn = "weatherId"
    )
    val alerts: List<DbAlert>,

//    @Embedded
//    val hours: List<DbHour>
)