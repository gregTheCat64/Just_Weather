package ru.javacat.justweather.ui.view_models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.javacat.justweather.models.Place
import ru.javacat.justweather.repository.PlacesRepository
import ru.javacat.justweather.repository.PlacesRepositorySharedPrefsImpl

class PlacesViewModel(application: Application): AndroidViewModel(application) {

    private val repository: PlacesRepository = PlacesRepositorySharedPrefsImpl(application)

    private val _data = MutableLiveData<List<Place>>()

    val data: LiveData<List<Place>>
        get() = _data

    fun loadPlaces(){
        _data.value = repository.getPlaces().value
    }

    fun savePlace(place: Place){
        repository.save(place)
    }

    fun removePlace(id: Int){
        repository.removeById(id)
    }
}