package ru.javacat.justweather.domain.models.geoCoderModels

data class GeocoderMetaData(
    val Address: Address,
    val AddressDetails: AddressDetails,
    val kind: String,
    val precision: String,
    val text: String
)