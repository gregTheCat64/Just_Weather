package ru.javacat.justweather.data.db.entities

data class DbLocation(
    val country: String,
    val lat: Double,
    val localtime: String,
    val localtime_epoch: Int,
    val lon: Double,
    val name: String,
    val region: String,
    val tz_id: String,
    var localTitle: String,
    var localSubtitle: String
)