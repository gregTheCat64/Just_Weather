package ru.javacat.justweather.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.javacat.justweather.models.Place
import javax.inject.Inject

class PlacesRepositorySharedPrefsImpl @Inject constructor(
    @ApplicationContext private val context: Context
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


    override suspend fun getPlaces(): LiveData<List<Place>> = data

    override suspend fun save(place: Place) {
        if (places.isNotEmpty() ){
            lastId = places.first().id
        }
        println("lastID: $lastId")
        places = listOf(
            place.copy(id = lastId+ 1)
        ) + places

        data.postValue(places)
        Log.i("PlacesRepo", "${place.name}")
        sync()
    }

    override suspend fun removeById(id: Int) {
        places = places.filter { it.id != id }
        data.postValue(places)
        sync()
    }

    private fun sync() {
        with(prefs.edit()) {
            putString(key, gson.toJson(places))
            apply()
        }
    }
}