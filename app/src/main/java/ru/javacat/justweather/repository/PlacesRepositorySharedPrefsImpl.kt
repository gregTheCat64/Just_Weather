package ru.javacat.justweather.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.javacat.justweather.models.Place

class PlacesRepositorySharedPrefsImpl(
    private val context: Context
): PlacesRepository {
    private val gson = Gson()
    private val prefs = context.getSharedPreferences("repo", Context.MODE_PRIVATE)
    private val type = TypeToken.getParameterized(List::class.java, Place::class.java).type
    private val key = "places"
    private var lastId = 0
    private var places = emptyList<Place>()
    private val data = MutableLiveData(places)

    init {
        prefs.getString(key, null)?.let {
            places = gson.fromJson(it, type)
            data.value = places
        }
    }


    override fun getPlaces(): LiveData<List<Place>> = data

    override fun save(place: Place) {
        if (places.isNotEmpty() ){
            lastId = places.last().id
        }
        places = listOf(
            place.copy(id = lastId++)
        ) + places

        data.value = places
        sync()
    }

    override fun removeById(id: Int) {
        places = places.filter { it.id != id }
        data.value = places
        sync()
    }

    private fun sync() {
        with(prefs.edit()) {
            putString(key, gson.toJson(places))
            apply()
        }
    }
}