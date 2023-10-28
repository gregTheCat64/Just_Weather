package ru.javacat.justweather.domain.repos

import ru.javacat.justweather.domain.models.Location

interface CurrentPlaceRepository {
    fun getFromPlacesList(): Location?
    fun saveToPlacesList(location: Location)

}