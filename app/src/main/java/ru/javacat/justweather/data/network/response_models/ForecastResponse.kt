package ru.javacat.justweather.data.network.response_models

data class ForecastResponse(
    val forecastday: List<ForecastdayResponse>
)