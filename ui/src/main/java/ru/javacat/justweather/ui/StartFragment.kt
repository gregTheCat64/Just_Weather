package ru.javacat.justweather.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
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
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.javacat.justweather.ui.base.BaseFragment
import ru.javacat.justweather.ui.util.isPermissionGranted
import ru.javacat.justweather.ui.util.snack
import ru.javacat.justweather.ui.view_models.StartViewModel
import ru.javacat.ui.R
import ru.javacat.ui.databinding.FragmentStartBinding

@AndroidEntryPoint
class StartFragment: BaseFragment<FragmentStartBinding>(), LocationListener {

    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentStartBinding = {
        inflater, container ->
        FragmentStartBinding.inflate(inflater, container, false)
    }

    private lateinit var pLauncher: ActivityResultLauncher<String>
    private lateinit var locationManager: LocationManager
    //private lateinit var fLocationClient: FusedLocationProviderClient
    private val viewModel: StartViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i("StartFrag", "onCreate")
        super.onCreate(savedInstanceState)
        permissionListener()
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

        locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i("StartFrag", "onViewCreated")
        initLoadingStateObserving()
        init()

        binding.repeatBtn.setOnClickListener {
            it.isVisible = false
            binding.progressBar.isVisible = true
            pLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)

        }
    }

    private fun init(){
        Log.i("StartFrag", "init")
        //updateDb()
        initObserver()
        checkPermission()


    }

    private fun initObserver(){
        Log.i("StartFrag", "initObserver")
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.currentWeatherFlow.observe(viewLifecycleOwner) {
                    println("currentflow: ${it?.location}")
                    it?.let {
                        //findNavController().navigate(R.id.mainFragment)
                    }
                }
            }
        }

    }

//    private fun updateDb(){
//        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO){
//            viewModel.updateDb()
//        }
//    }

    private fun initLoadingStateObserving(){
        viewModel.loadingState.observe(viewLifecycleOwner){
            when (it) {
                is LoadingState.NetworkError ->  {
                    Snackbar.make(requireView(), getString(R.string.network_error), Snackbar.LENGTH_LONG)
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
                    binding.progressBar.isVisible = false
                    binding.repeatBtn.isVisible = false
                    findNavController().navigate(R.id.mainFragment)
                }
                else ->  binding.progressBar.isVisible = false
            }
        }
    }

    private fun loadData(lat: Double, long: Double){
        //Log.i("MyLog", "Loading data")
        viewModel.findPlaceByLocation("$lat,$long")
    }

    private fun checkPermission(){
        if (!isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)){
            //permissionListener()
            pLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else getLocation()
    }

    private fun isLocationEnabled(): Boolean{
        //val lm = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }


    private fun permissionListener(){
        pLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ){
            if (it == true) {
                getLocation()
            } else {
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

    private fun getLocation(){
        Log.i("StartFrag", "getLocation")
        when (Build.VERSION.SDK_INT) {
            in 1..29 -> {
                getLocationOverGps()
            }else -> getLocationOverNetwork()
        }
    }

    private fun getLocationOverGps(){
        Log.i("StartFrag", "getLocationOverGPS")
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            snack(getString(R.string.location_disabled))
            binding.repeatBtn.isVisible = true
            binding.progressBar.isVisible = false
            return
        }

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED

        ) {
            Log.i("StartFrag", "access fine is not permited")
            snack(getString(R.string.location_disabled))
            binding.repeatBtn.isVisible = true
            binding.progressBar.isVisible = false
            return
        }
                locationManager.requestSingleUpdate(
                    LocationManager.GPS_PROVIDER,
                    this,
                    null
                )
        }



    @RequiresApi(Build.VERSION_CODES.R)
    private fun getLocationOverNetwork(){
        Log.i("StartFrag", "getLocationOverNetwork")
        if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            snack(getString(R.string.location_disabled))
            binding.repeatBtn.isVisible = true
            binding.progressBar.isVisible = false
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
        ) {
            loadData(it.latitude, it.longitude)

        }
    }

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

    override fun onLocationChanged(loc: Location) {
        Log.i("StartFrag", "location changed")
        loadData(loc.latitude, loc.longitude)
    }

    override fun onProviderDisabled(provider: String) {
        Log.i("StartFrag", "provoder disabled")
        snack(getString(R.string.location_disabled))
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        Log.i("StartFrag", "status changed")
        //snack("status changed")
    }
}