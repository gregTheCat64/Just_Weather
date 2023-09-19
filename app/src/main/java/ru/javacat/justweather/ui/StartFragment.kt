package ru.javacat.justweather.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.javacat.justweather.R
import ru.javacat.justweather.base.BaseFragment
import ru.javacat.justweather.databinding.FragmentStartBinding
import ru.javacat.justweather.ui.view_models.MainViewModel
import ru.javacat.justweather.util.isPermissionGranted
import ru.javacat.justweather.util.snack

@AndroidEntryPoint
class StartFragment: BaseFragment<FragmentStartBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentStartBinding = {
        inflater, container ->
        FragmentStartBinding.inflate(inflater, container, false)
    }

    private lateinit var pLauncher: ActivityResultLauncher<String>
    private lateinit var fLocationClient: FusedLocationProviderClient
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        val inflater = TransitionInflater.from(requireContext())
        exitTransition = inflater.inflateTransition(R.transition.fade)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initLoadingStateObserving()
        init()

        binding.repeatBtn.setOnClickListener {
            it.isVisible = false
            binding.progressBar.isVisible = true
            init()
        }
    }

    private fun init(){
        //getLocation()

        checkPermission()
        initObserver()

    }

    private fun initObserver(){
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.weatherFlow.collectLatest {
                    //updateTheme()
                    it?.let {
                        findNavController().navigate(R.id.mainFragment)
                    }

                }
            }

        }
    }

    private fun initLoadingStateObserving(){
        viewModel.loadingState.observe(viewLifecycleOwner){
            when (it) {
                is  LoadingState.NetworkError ->  {
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
                else ->  binding.progressBar.isVisible = false
            }
        }
    }

    private fun loadData(lat: Double, long: Double){
        Log.i("MyLog", "Loading data")
        viewModel.findPlaceByLocation("$lat,$long", 3)
    }

    private fun isLocationEnabled(): Boolean{
        val lm = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun getLocation(){
        if (!isLocationEnabled()){
            snack(getString(R.string.location_disabled))
            binding.repeatBtn.isVisible = true
            binding.progressBar.isVisible = false
            return

        }
        val ct = CancellationTokenSource()
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            //checkPermission()
            return
        }
        fLocationClient
            .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, ct.token)
            .addOnCompleteListener{
                Log.i("MyLog", "getting location")
                loadData(it.result.latitude, it.result.longitude)
            }
    }

    private fun permissionListener(){
        pLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ){
            if (it == true) {
                getLocation()
            } else
                Toast.makeText(requireContext(), getString(R.string.permission_alarm), Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkPermission(){
        if (!isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)){
            permissionListener()
            pLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else getLocation()
    }

    private fun updateTheme(){
        val weatherCondition = viewModel.weatherFlow.value?.current?.condition?.code
        if (weatherCondition == 1003 ) run {
            (requireActivity() as AppCompatActivity).applicationContext.setTheme(R.style.Base_Theme_RainWeather)
            snack("сегодня дождяра хлещет")
        }

    }

}