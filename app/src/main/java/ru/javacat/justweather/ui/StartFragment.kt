package ru.javacat.justweather.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import ru.javacat.justweather.R
import ru.javacat.justweather.databinding.FragmentStartBinding
import ru.javacat.justweather.util.isPermissionGranted

class StartFragment: Fragment() {
    private lateinit var binding: FragmentStartBinding
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
    ): View {
        binding = FragmentStartBinding.inflate(inflater, container, false)

        fLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        getLocation()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkPermission()
        initObserver()
    }

    private fun initObserver(){
        viewModel.data.observe(viewLifecycleOwner){
            parentFragmentManager
                .beginTransaction()
                .addToBackStack(null)
                .replace(R.id.fragmentContainer, MainFragment.newInstance())
                .commit()
        }
    }

    private fun loadData(lat: Double, long: Double){
        Log.i("MyLog", "Loading data")
        viewModel.loadWeatherByName("$lat,$long", 3)
    }

    private fun getLocation(){
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
                Toast.makeText(requireContext(), "Чтобы пользоваться приложением, необходимо разрешение", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkPermission(){
        if (!isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)){
            permissionListener()
            pLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else getLocation()
    }

    companion object{
        fun newInstance(): StartFragment{
            return StartFragment()
        }
    }
}