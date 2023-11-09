package ru.javacat.justweather.domain.models.geoCoderModels

data class GeocoderResponseMetaData(
    val found: String,
    val request: String,
    val results: String
)