package ru.javacat.justweather.domain.models

data class Weather(
    val alerts: List<Alert>,
    val current: Current,
    val forecasts: List<Forecastday>,
    val location: Location
)