package ru.javacat.justweather.ui

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import ru.javacat.justweather.R
import ru.javacat.justweather.databinding.FragmentMainBinding
import ru.javacat.justweather.response_models.Forecastday
import ru.javacat.justweather.util.isPermissionGranted
import ru.javacat.justweather.util.load
import kotlin.math.roundToInt


class MainFragment: Fragment() {
    private lateinit var pLauncher: ActivityResultLauncher<String>
    private lateinit var binding: FragmentMainBinding
    private lateinit var adapter: ForecastAdapter
    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container,false)

        viewModel.loadWeatherByName("Saratov", 3)

        binding.refresh.setOnClickListener {
            viewModel.loadWeatherByName("Saratov", 3)
        }

        viewModel.data.observe(viewLifecycleOwner){
            val image = "https://${it.current.condition.icon}"
            binding.apply {
                //it.forecast.forecastday.get(0).astro.is_moon_up
                tempTxtView.text = it.current.temp_c.roundToInt().toString() + "°"
                cityTxtView.text = it.location.name
                conditionTxtView.text = it.current.condition.text
                realFeelTxtView.text = it.current.feelslike_c.toString() + "°"
                imageView.load(image)
                detailsLayout.cloud.text = it.current.cloud.toString()+"%"
                detailsLayout.windSpeed.text = it.current.wind_kph.roundToInt().toString()+"км/ч"
                detailsLayout.windDir.text = it.current.wind_dir
                detailsLayout.precipation.text = it.current.precip_mm.toString()+"мм"
                detailsLayout.humidity.text = it.current.humidity.toString()+"%"
                detailsLayout.uvIndex.text = it.current.uv.toString()
                val alerts = it.alerts.alert
                for (element in alerts){
                    if (element.desc.isNotEmpty()){
                        //Toast.makeText(requireContext(), element.desc, Toast.LENGTH_LONG).show()
                        Snackbar.make(requireView(),element.desc,Snackbar.LENGTH_LONG).show()
                    }
                }
                initRecView()

            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermission()
        //initRecView()

    }

    private fun initRecView() {
        binding.daysRecView.layoutManager = LinearLayoutManager(requireContext())
        adapter = ForecastAdapter(object : OnInteractionListener{
            override fun onForecastItem(item: Forecastday) {
                viewModel.chooseForecastData(item)

                parentFragmentManager
                    .beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.fragmentContainer, ForecastFragment.newInstance())
                    .commit()

            }
        })
        binding.daysRecView.adapter = adapter
        val list = viewModel.data.value?.forecast?.forecastday
        println("list: ${list?.size}")
        adapter.submitList(list)
    }

    private fun permissionListener(){
        pLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ){
            Toast.makeText(requireContext(), "Permission is $it", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkPermission(){
        if (!isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)){
            permissionListener()
            pLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    companion object {
        fun newInstance(): MainFragment {
            return MainFragment()
        }
    }
}