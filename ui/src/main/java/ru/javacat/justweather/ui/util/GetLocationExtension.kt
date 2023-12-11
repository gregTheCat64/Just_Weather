package ru.javacat.justweather.ui.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import ru.javacat.justweather.ui.LoadingState
import ru.javacat.justweather.ui.SingleLiveEvent
import ru.javacat.justweather.ui.base.BaseFragment
import ru.javacat.ui.R


var coords: Pair<Double, Double>? = null


val coordsFlow: MutableSharedFlow<Pair<Double, Double>> = MutableSharedFlow()

val locationState = SingleLiveEvent<LoadingState>()


abstract class LocationListenerImplFragment<VB : ViewBinding> : BaseFragment<VB>(),
    LocationListener {
    override fun onLocationChanged(loc: Location) {
        Log.i("GetLocFunc", "location changed")
        lifecycleScope.launch(Dispatchers.IO) {
            coordsFlow.emit(Pair(loc.latitude, loc.longitude))
            Log.i("GetLocFunc", "emited: ${loc.latitude}")
        }
    }

    override fun onProviderDisabled(provider: String) {
        Log.i("GetLocFunc", "provider disabled")
        locationState.setValue(LoadingState.LocationIsUnabled)
        snack(getString(R.string.location_disabled))
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        Log.i("GetLocFunc", "status changed")
    }
}

private lateinit var locationManager: LocationManager
fun <VB : ViewBinding> LocationListenerImplFragment<VB>.getLocationOverGps() {
    Log.i("GetLocFunc", "getLocationOverGPS")
    locationManager =
        requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager

    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
        locationState.setValue(LoadingState.LocationIsUnabled)
        snack(getString(R.string.location_disabled))
        return
    }

    if (ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        locationState.setValue(LoadingState.LocationIsUnabled)
        snack(getString(R.string.location_disabled))

        return
    }
    locationManager.requestSingleUpdate(
        LocationManager.GPS_PROVIDER,
        this,
        null
    )

    return
}

@RequiresApi(Build.VERSION_CODES.P)
fun <VB : ViewBinding> LocationListenerImplFragment<VB>.getLocationOverNetwork() {
    Log.i("GetLocFunc", "getLocationOverNetwork")
    locationManager =
        requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
    if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) &&
        !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    ) {
        locationState.setValue(LoadingState.LocationIsUnabled)
        snack(getString(R.string.location_disabled))
        return
    }
    if (ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        return
    }
    locationManager.getCurrentLocation(
        LocationManager.NETWORK_PROVIDER,
        null,
        requireContext().mainExecutor
    ) { location ->
        if (location != null) {
            coords = Pair(location.latitude, location.longitude)
            lifecycleScope.launch(Dispatchers.IO) {
                coordsFlow.emit(Pair(location.latitude, location.longitude))
            }

        } else {
            snack("${R.string.network_error}")
        }
    }

    return

}

