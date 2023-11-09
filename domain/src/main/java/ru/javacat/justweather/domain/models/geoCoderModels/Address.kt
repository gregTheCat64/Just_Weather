package ru.javacat.justweather.domain.models.geoCoderModels

data class Address(
    val Components: List<Component>,
    val country_code: String,
    val formatted: String
)