package ru.javacat.justweather.repository

import androidx.lifecycle.LiveData
import ru.javacat.justweather.models.Place

interface PlacesRepository {
    fun getPlaces(): LiveData<List<Place>>
    fun save(place: Place)
    fun removeById(id: Int)
}