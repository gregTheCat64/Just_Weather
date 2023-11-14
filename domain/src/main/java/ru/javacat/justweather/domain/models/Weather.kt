package ru.javacat.justweather.domain.models

data class Weather(
    val id: String,
    val positionId: Int,
    val alerts: List<Alert>,
    val current: Current,
    val forecasts: List<Forecastday>,
    val location: Location,
    val isCurrent: Boolean,
    var isLocated: Boolean
    //val hours: List<Hour>
)

