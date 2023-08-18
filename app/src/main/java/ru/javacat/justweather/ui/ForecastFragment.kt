package ru.javacat.justweather.ui

import android.os.Build
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import ru.javacat.justweather.R
import ru.javacat.justweather.databinding.FragmentForecastBinding
import ru.javacat.justweather.util.asLocalDate
import ru.javacat.justweather.util.toLocalDate

class ForecastFragment: Fragment() {
    private lateinit var binding: FragmentForecastBinding
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var forecastAdapter: ForecastAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.slide_right)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentForecastBinding.inflate(inflater, container, false)


//        val forecastday = viewModel.forecastData.value
//        binding.dateTxtView.text = forecastday?.date
        initForecastObserver()


        Log.i("MyTag", "forecastVM: $viewModel")
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initForecastObserver(){
        lifecycleScope.launch {
            viewModel.forecastData.observe(viewLifecycleOwner) {
               // Toast.makeText(requireContext(), "Llala", Toast.LENGTH_SHORT).show()

                binding.dateTxtView.text = it.date.toLocalDate().asLocalDate()
                binding.maxTempValue.text = it.day.maxtemp_c.toString()
                binding.minTempValue.text = it.day.mintemp_c.toString()
                binding.maxWindSpeedValue.text = it.day.maxwind_kph.toString() + "км/ч"
                binding.totalPrecipValue.text = it.day.totalprecip_mm.toString() + "мм"
                binding.avgHumidityValue.text = it.day.avghumidity.toString() + "%"
                binding.avgvisValue.text = it.day.avgvis_km.toString() + "км"
                binding.uvIndexValue.text = it.day.uv.toString()
                binding.sunRiseValue.text = it.astro.sunrise
                binding.sunSetValue.text = it.astro.sunset
                binding.moonRiseValue.text = it.astro.moonrise
                binding.moonSetValue.text = it.astro.moonset
                binding.moonPhaseValue.text = it.astro.moon_phase
                binding.moonIllumTxtValue.text = it.astro.moon_illumination

                initForecastRecView()
            }
        }
    }

    private fun initForecastRecView() {
        forecastAdapter = ForecastAdapter()
        //viewModel.chooseForecastData(Forecastday())

        binding.hoursRecView.adapter = forecastAdapter
        val list = viewModel.forecastData.value?.hour
        forecastAdapter.submitList(list)

        binding.hoursRecView.scrollToPosition(6)
    }


    companion object{
        fun newInstance(): ForecastFragment{
            return ForecastFragment()
        }
    }
}