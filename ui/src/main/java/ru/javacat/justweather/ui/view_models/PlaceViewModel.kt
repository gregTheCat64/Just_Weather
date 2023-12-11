package ru.javacat.justweather.ui.view_models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.javacat.justweather.common.util.LOC_LIMIT
import ru.javacat.justweather.domain.ApiError
import ru.javacat.justweather.domain.NetworkError
import ru.javacat.justweather.domain.models.suggestModels.FoundLocation
import ru.javacat.justweather.ui.LoadingState
import ru.javacat.justweather.ui.SingleLiveEvent
import javax.inject.Inject

@HiltViewModel
class PlaceViewModel @Inject constructor(
    private val repository: ru.javacat.justweather.domain.repos.Repository,
) : ViewModel() {

    private val locationsLimit = LOC_LIMIT

    val allWeathersFlow = repository.allWeathers.asLiveData(viewModelScope.coroutineContext)

    private var _foundLocations = MutableLiveData<List<FoundLocation>>()
    val foundLocations: LiveData<List<FoundLocation>>
        get() = _foundLocations

    val loadingState = SingleLiveEvent<LoadingState>()


    suspend fun updateDb(){
        viewModelScope.launch(Dispatchers.IO) {
            loadingState.postValue(LoadingState.Load)
            try {
                //repository.updateAllWeathers()
                loadingState.postValue(LoadingState.Updated)
            }catch (e: ApiError) {
                loadingState.postValue(LoadingState.InputError)
                Log.i("MyTag", "ОШИБКА: ${e.code}")
            } catch (e: NetworkError) {
                loadingState.postValue(LoadingState.NetworkError)
                Log.i("MyTag", "ОШИБКА: NETWORK")
            }


        }
    }

    fun getLocations(query: String){
        //var searchLocations = emptyList<SearchLocation>()
        viewModelScope.launch(Dispatchers.IO) {
            loadingState.postValue(LoadingState.Load)

            try {
                val found = repository.findLocation(query).results
                _foundLocations.postValue(found)
                loadingState.postValue(LoadingState.Found)
            } catch (e: ApiError) {
                loadingState.postValue(LoadingState.InputError)
                Log.i("MyTag", "ОШИБКА: ${e.code}")
            } catch (e: NetworkError) {
                loadingState.postValue(LoadingState.NetworkError)
                Log.i("MyTag", "ОШИБКА: NETWORK")
            }
        }
    }

    fun getCoordinates(uri: String, localTitle: String, localSubtitle: String){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = repository.getCoords(uri)

                val coordList = result.pos.split(" ")
                val coords = coordList[1]+","+coordList[0]

                setNewLocation(coords, false, localTitle, localSubtitle)
            } catch (e: ApiError) {
                loadingState.postValue(LoadingState.InputError)
                Log.i("MyTag", "ОШИБКА: ${e.code}")
            } catch (e: NetworkError) {
                loadingState.postValue(LoadingState.NetworkError)
                Log.i("MyTag", "ОШИБКА: NETWORK")
            }
        }
    }

    fun getLocationByCoords(coords: String){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val listOfCoords = coords.split(",")
                val reversedCoords = listOfCoords[1]+","+listOfCoords[0]
                val result = repository.getLocationByCoords(reversedCoords)
                Log.i("MyTag", "getLocationByCoords: $result")

                //val localTitle = result.featureMember[0].GeoObject.name
                val localAddress = result.featureMember[0].GeoObject.metaDataProperty.GeocoderMetaData.Address

                val country = localAddress.Components.firstOrNull{it.kind == "country"}?.name
                val province = localAddress.Components.firstOrNull{it.kind == "province"}?.name
                val localTitle = localAddress.Components.lastOrNull { it.kind == "locality" }?.name.toString()
                val localSubtitle = "$country, $province"

                repository.unCheckLocated()

                setNewLocation(coords, true, localTitle, localSubtitle)
            } catch (e: ApiError) {
                loadingState.postValue(LoadingState.InputError)
                Log.i("MyTag", "ОШИБКА: ${e.code}")
            } catch (e: NetworkError) {
                loadingState.postValue(LoadingState.NetworkError)
                Log.i("MyTag", "ОШИБКА: NETWORK")
            }
        }
    }



    private fun setNewLocation(request: String, isLocated: Boolean, localTitle: String, localSubtitle: String) {
        viewModelScope.launch(Dispatchers.IO) {
            loadingState.postValue(LoadingState.Load)
            try {
                repository.getNewPlaceDetails(request, isLocated, localTitle, localSubtitle, locationsLimit)
                loadingState.postValue(LoadingState.Success)
            } catch (e: ApiError) {
                loadingState.postValue(LoadingState.InputError)
                Log.i("MyTag", "ОШИБКА: ${e.code}")
            } catch (e: NetworkError) {
                loadingState.postValue(LoadingState.NetworkError)
                Log.i("MyTag", "ОШИБКА: NETWORK")
            }
        }
    }

    fun setLocation(locationId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            loadingState.postValue(LoadingState.Load)

            try {
                repository.updateWeatherById(locationId, true)
                loadingState.postValue(LoadingState.Success)

            } catch (e: ApiError) {
                loadingState.postValue(LoadingState.InputError)
                Log.i("MyTag", "ОШИБКА: ${e.code}")
            } catch (e: NetworkError) {
                loadingState.postValue(LoadingState.NetworkError)
                Log.i("MyTag", "ОШИБКА: NETWORK")
            }
        }
    }

    fun removeLocation(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.removeById(id)
        }
    }

    fun clearPlace(){
        _foundLocations.value = emptyList()
    }
}