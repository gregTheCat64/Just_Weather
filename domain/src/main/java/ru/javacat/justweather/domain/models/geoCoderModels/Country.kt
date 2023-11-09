package ru.javacat.justweather.domain.models.geoCoderModels

data class Country(
    val AddressLine: String,
    val AdministrativeArea: AdministrativeArea,
    val CountryName: String,
    val CountryNameCode: String
)