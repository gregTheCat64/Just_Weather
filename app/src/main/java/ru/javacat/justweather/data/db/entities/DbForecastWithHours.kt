package ru.javacat.justweather.data.db.entities

import androidx.room.Embedded
import androidx.room.Relation
import ru.javacat.justweather.data.network.response_models.HourResponse
import ru.javacat.justweather.domain.models.Hour

data class DbForecastWithHours(
    @Embedded val forecastday: DbForecastday,

    @Relation(
        parentColumn = "date",
        entityColumn = "forecastDate"
    )
    val hours: List<DbHour>
)
