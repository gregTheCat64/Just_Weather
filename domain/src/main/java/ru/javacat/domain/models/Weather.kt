package ru.javacat.domain.models

import ru.javacat.justweather.domain.models.Alert
import ru.javacat.justweather.domain.models.Forecastday
import ru.javacat.justweather.domain.models.Location

data class Weather(
    val id: String,
    val alerts: List<Alert>,
    val current: Current,
    val forecasts: List<Forecastday>,
    val location: Location,
    //val hours: List<Hour>
)

