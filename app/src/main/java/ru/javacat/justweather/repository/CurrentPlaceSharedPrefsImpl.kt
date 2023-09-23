package ru.javacat.justweather.repository

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.javacat.justweather.models.Place
import ru.javacat.justweather.response_models.Location
import javax.inject.Inject

class CurrentPlaceSharedPrefsImpl @Inject constructor(
    @ApplicationContext private val context: Context
):CurrentPlaceRepository {

    private val gson = Gson()
    private val type = TypeToken.getParameterized(Location::class.java).type
    private val prefs = context.getSharedPreferences("currentPlaceRepo", Context.MODE_PRIVATE)
    private val key = "currentLocation"
    //private val data = MutableLiveData<Place>()
    private var place: Location? = null


    override fun getCurrentPlace(): Location? {
        prefs.getString(key, null)?.let {
            place = gson.fromJson(it, type)
        }
        //println("prefs= " + place?.name)
        return place
    }

    override fun saveCurrentPlace(location: Location) {
        with(prefs.edit()){
            putString(key, gson.toJson(location))
            apply()
            Log.i("CurrentPlaceSharedRepo", "saving location: ${getCurrentPlace()?.name}")

        }
    }
}