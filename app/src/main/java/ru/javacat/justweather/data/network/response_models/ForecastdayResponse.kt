package ru.javacat.justweather.data.network.response_models

data class ForecastdayResponse(
    val astro: AstroResponse,
    val date: String,
    val date_epoch: Int,
    val day: DayResponse,
    val hour: List<HourResponse>
)