package ru.javacat.justweather.repository

import ru.javacat.justweather.response_models.Location

interface CurrentPlaceRepository {
    fun getCurrentPlace(): Location?
    fun saveCurrentPlace(location: Location)

}