package ru.javacat.justweather.data.network.response_models

data class SearchLocationResponse(
    val country: String,
    val id: Int,
    val lat: Double,
    val lon: Double,
    val name: String,
    val region: String,
    val url: String
)