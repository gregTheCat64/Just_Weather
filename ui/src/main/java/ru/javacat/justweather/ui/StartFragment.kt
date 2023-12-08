package ru.javacat.justweather.ui

import android.Manifest
import android.location.LocationListener
import android.os.Build
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.javacat.justweather.ui.util.LocationListenerImplFragment
import ru.javacat.justweather.ui.util.coordsFlow
import ru.javacat.justweather.ui.util.getLocationOverGps
import ru.javacat.justweather.ui.util.getLocationOverNetwork
import ru.javacat.justweather.ui.util.isPermissionGranted
import ru.javacat.justweather.ui.util.locationState
import ru.javacat.justweather.ui.view_models.StartViewModel
import ru.javacat.ui.R
import ru.javacat.ui.databinding.FragmentStartBinding

@AndroidEntryPoint
class StartFragment : LocationListenerImplFragment<FragmentStartBinding>(), LocationListener {

    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentStartBinding =
        { inflater, container ->
            FragmentStartBinding.inflate(inflater, container, false)
        }

    private lateinit var pLauncher: ActivityResultLauncher<String>
    //private lateinit var locationManager: LocationManager
    private lateinit var dispatcher: CoroutineDispatcher

    //private lateinit var fLocationClient: FusedLocationProviderClient
    private val viewModel: StartViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i("StartFrag", "onCreate")
        super.onCreate(savedInstanceState)

        permissionListener()
        dispatcher = Dispatchers.Default


        val inflater = TransitionInflater.from(requireContext())
        exitTransition = inflater.inflateTransition(R.transition.fade)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i("StartFrag", "onCreateView")
        //fLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

//        locationManager =
//            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager



        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i("StartFrag", "onViewCreated")

        locationStateObserver()
        initLoadingStateObserving()
        checkPermission()

        binding.repeatBtn.setOnClickListener {
            it.isVisible = false
            binding.progressBar.isVisible = true
            pLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

    }

    private fun initObservers() {
        Log.i("StartFrag", "initObservers")

        initGetLocationObserver()
        currentFlowObserver()

    }

    private fun currentFlowObserver() {
        Log.i("StartFrag", "currentFlowObserver")
        viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.getCurrentWeatherFlow().collectLatest  {
                        Log.i("StartFrag", "curFlow: ${it?.location}")
                        if (it != null){
                            //viewModel.updateCurrentWeather()
                            findNavController().navigate(R.id.mainFragment)
                        } else {
                            getLocation()
                        }
                    }
                }

        }

    }

    private fun locationStateObserver(){
        Log.i("StartFrag", "LocationStateObserver")
        locationState.observe(viewLifecycleOwner){
            if (it == LoadingState.LocationIsUnabled){
                binding.progressBar.isVisible = false
                binding.repeatBtn.isVisible = true
            }
        }
    }

    private fun initGetLocationObserver() {
        Log.i("StartFrag", "initGetLocationObserver")
        lifecycleScope.launch {
            coordsFlow.collectLatest {
                loadData(it.first,it.second)
            }
        }
    }

//    private fun updateDb(){
//        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO){
//            viewModel.updateDb()
//        }
//    }

    private fun initLoadingStateObserving() {
        viewModel.loadingState.observe(viewLifecycleOwner) {
            when (it) {
                is LoadingState.NetworkError -> {
                    Snackbar.make(
                        requireView(),
                        getString(R.string.network_error),
                        Snackbar.LENGTH_LONG
                    )
//                        .setAction("Повторить") {
//                            init()
//                        }
                        .show()
                    binding.progressBar.isVisible = false
                    binding.repeatBtn.isVisible = true
                }

                is LoadingState.Load -> {
                    binding.progressBar.isVisible = true
                    binding.repeatBtn.isVisible = false
                }

                is LoadingState.Success -> {
                    findNavController().navigate(R.id.mainFragment)
                }

                else -> binding.progressBar.isVisible = false
            }
        }
    }

    private fun loadData(lat: Double, long: Double) {
        viewModel.getLocationByCoords("$lat,$long")
        //viewModel.getLocationByCoords("0,0")
    }

    private fun checkPermission() {
        Log.i("StartFrag", "checkPermission")
        if (!isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
            Log.i("StartFrag", "permission has not got")
            pLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else initObservers()
    }

//    private fun isLocationEnabled(): Boolean {
//        //val lm = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
//        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
//            LocationManager.NETWORK_PROVIDER
//        )
//    }


    private fun permissionListener() {
        Log.i("StartFrag", "permissionListener2")
        pLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            if (it == true) {
                Log.i("StartFrag", "true")
                //initLoadingStateObserving()
                //getLocation()
                initObservers()

            } else {
                Log.i("StartFrag", "false")
                Toast.makeText(
                    requireContext(),
                    getString(R.string.permission_alarm),
                    Toast.LENGTH_SHORT
                ).show()
                binding.repeatBtn.isVisible = true
                binding.progressBar.isVisible = false
            }
        }
    }

    private fun getLocation() {
        Log.i("StartFrag", "getLocation")
        when (Build.VERSION.SDK_INT) {
            in 1..29 -> {
                getLocationOverGps()
            }

            else -> {
                getLocationOverNetwork()
            }
        }
    }

//    private fun getLocationOverGps() {
//        Log.i("StartFrag", "getLocationOverGPS")
//        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//            snack(getString(R.string.location_disabled))
//            binding.repeatBtn.isVisible = true
//            binding.progressBar.isVisible = false
//            return
//        }
//
//        if (ActivityCompat.checkSelfPermission(
//                requireContext(),
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//
//        ) {
//            Log.i("StartFrag", "access fine is not permited")
//            snack(getString(R.string.location_disabled))
//            binding.repeatBtn.isVisible = true
//            binding.progressBar.isVisible = false
//            return
//        }
//        lifecycle.coroutineScope.launch {
//            println("getLocationOverGPSThread: ${Thread.currentThread().name}")
//            locationManager.requestSingleUpdate(
//                LocationManager.GPS_PROVIDER,
//                this@StartFragment,
//                null
//            )
//        }
//    }
//
//
//    @RequiresApi(Build.VERSION_CODES.R)
//    private fun getLocationOverNetwork() {
//        println("getLocationOverNetworkThread: ${Thread.currentThread().name}")
//        Log.i("StartFrag", "getLocationOverNetwork")
//        if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) || !locationManager.isProviderEnabled(
//                LocationManager.GPS_PROVIDER
//            )
//        ) {
//            snack(getString(R.string.location_disabled))
//            binding.repeatBtn.isVisible = true
//            binding.progressBar.isVisible = false
//            return
//        }
//
//        if (ActivityCompat.checkSelfPermission(
//                requireContext(),
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//
//        ) {
//            return
//        }
//        viewLifecycleOwner.lifecycleScope.launch {
//            println("getLocationOverNetworkThread: ${Thread.currentThread().name}")
//            locationManager.getCurrentLocation(
//                LocationManager.NETWORK_PROVIDER,
//                null,
//                requireContext().mainExecutor
//            ) { location ->
//                if (location != null) {
//                    loadData(location.latitude, location.longitude)
//                } else {
//                    locationManager.getCurrentLocation(
//                        LocationManager.GPS_PROVIDER,
//                        null,
//                        requireContext().mainExecutor
//                    ) {
//                        if (it != null) {
//                            loadData(it.latitude, it.longitude)
//                        } else {
//                            println("Мы так и не добились никаких данных, хз в чем проблема")
//                            Toast.makeText(
//                                requireContext(),
//                                "Нажмите повторить",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                            binding.repeatBtn.isVisible = true
//                            binding.progressBar.isVisible = false
//                        }
//                    }
//                }
//
//            }
//        }
//
//
//    }

//    private fun getLocationOverGoogle(){
//        if (!isLocationEnabled()){
//            snack(getString(R.string.location_disabled))
//            binding.repeatBtn.isVisible = true
//            binding.progressBar.isVisible = false
//            return
//
//        }
//        val ct = CancellationTokenSource()
//        if (ActivityCompat.checkSelfPermission(
//                requireContext(),
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED &&
//            ActivityCompat.checkSelfPermission(
//                requireContext(),
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            //checkPermission()
//            return
//        }
//        fLocationClient
//            .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, ct.token)
//            .addOnCompleteListener{
//                Log.i("MyLog", "getting location")
//                loadData(it.result.latitude, it.result.longitude)
//            }
//
//    }

//    override fun onLocationChanged(loc: Location) {
//        Log.i("StartFrag", "location changed")
//        loadData(loc.latitude, loc.longitude)
//    }
//
//    override fun onProviderDisabled(provider: String) {
//        Log.i("StartFrag", "provider disabled")
//        snack(getString(R.string.location_disabled))
//    }
//
//    @Deprecated("Deprecated in Java")
//    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
//        Log.i("StartFrag", "status changed")
//        //snack("status changed")
//    }
}