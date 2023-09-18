package ru.javacat.justweather.repository

import androidx.lifecycle.LiveData
import ru.javacat.justweather.models.Place

interface PlacesRepository {
    suspend fun getPlaces(): LiveData<List<Place>>
    suspend fun save(place: Place)
    suspend fun removeById(id: Int)
}