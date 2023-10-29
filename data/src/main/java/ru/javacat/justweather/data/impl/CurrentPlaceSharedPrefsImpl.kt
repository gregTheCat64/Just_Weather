package ru.javacat.justweather.data.impl

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.javacat.justweather.domain.models.Location
import ru.javacat.justweather.domain.repos.CurrentPlaceRepository
import javax.inject.Inject

class CurrentPlaceSharedPrefsImpl @Inject constructor(
    @ApplicationContext private val context: Context
): ru.javacat.justweather.domain.repos.CurrentPlaceRepository {

    private val gson = Gson()
    private val type = TypeToken.getParameterized(ru.javacat.justweather.domain.models.Location::class.java).type
    private val prefs = context.getSharedPreferences("currentPlaceRepo", Context.MODE_PRIVATE)
    private val key = "currentLocation"
    //private val data = MutableLiveData<Place>()
    private var place: ru.javacat.justweather.domain.models.Location? = null


    override fun getFromPlacesList(): ru.javacat.justweather.domain.models.Location? {
        prefs.getString(key, null)?.let {
            place = gson.fromJson(it, type)
        }
        //println("prefs= " + place?.name)
        return place
    }

    override fun saveToPlacesList(location: ru.javacat.justweather.domain.models.Location) {
        with(prefs.edit()){
            putString(key, gson.toJson(location))
            apply()
            Log.i("CurrentPlaceSharedRepo", "saving location: ${getFromPlacesList()?.name}")

        }
    }
}