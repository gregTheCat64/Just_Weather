package ru.javacat.justweather.response_models

data class Weather(
    val alerts: Alerts,
    val current: Current,
    val forecast: Forecast,
    val location: Location
)