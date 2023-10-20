package ru.javacat.justweather.data.network.response_models

data class WeatherResponse(
    val alerts: AlertsResponse,
    val current: CurrentResponse,
    val forecast: ForecastResponse,
    val location: LocationResponse
)