package ru.javacat.justweather.data.network.response_models

import ru.javacat.justweather.domain.models.Alert
import ru.javacat.justweather.domain.models.Current
import ru.javacat.justweather.domain.models.Forecastday
import ru.javacat.justweather.domain.models.Location

data class WeatherResponse(
    val alerts: List<Alert>,
    val current: Current,
    val forecasts: List<ForecastdayResponse>,
    val location: Location
)