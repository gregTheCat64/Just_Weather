package ru.javacat.justweather.repository

import ru.javacat.justweather.response_models.Location

interface CurrentPlaceRepository {
    fun getFromPlacesList(): Location?
    fun saveToPlacesList(location: Location)

}