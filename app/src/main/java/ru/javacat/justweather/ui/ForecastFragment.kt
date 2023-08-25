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
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import ru.javacat.justweather.R
import ru.javacat.justweather.databinding.FragmentForecastBinding
import ru.javacat.justweather.ui.adapters.ForecastAdapter
import ru.javacat.justweather.ui.view_models.MainViewModel
import ru.javacat.justweather.util.asLocalDate
import ru.javacat.justweather.util.load
import ru.javacat.justweather.util.toLocalDate
import ru.javacat.justweather.util.toLocalTime
import kotlin.math.roundToInt

class ForecastFragment: Fragment() {
    private lateinit var binding: FragmentForecastBinding
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var forecastAdapter: ForecastAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //val inflater = TransitionInflater.from(requireContext())
        //enterTransition = inflater.inflateTransition(R.transition.slide_right)
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

        binding.backBtn.setOnClickListener {
            //parentFragmentManager.popBackStack()
            findNavController().navigateUp()
        }
        initForecastObserver()


        Log.i("MyTag", "forecastVM: $viewModel")
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initForecastObserver(){
        lifecycleScope.launch {
            viewModel.forecastData.observe(viewLifecycleOwner) {
               // Toast.makeText(requireContext(), "Llala", Toast.LENGTH_SHORT).show()

                binding.conditionImage.load(it.day.condition.icon)
                binding.conditionValue.text = it.day.condition.text
                binding.dateTxtView.text = it.date.toLocalDate().asLocalDate()
                binding.avgTempValue.text = it.day.avgtemp_c.toString()+"°"
                binding.maxTempValue.text = it.day.maxtemp_c.toString()
                binding.minTempValue.text = it.day.mintemp_c.toString()
                binding.maxWindSpeedValue.text = it.day.maxwind_kph.roundToInt().toString() + "км/ч"
                binding.totalPrecipValue.text = it.day.totalprecip_mm.toString() + "мм"
                binding.avgHumidityValue.text = it.day.avghumidity.toString() + "%"
                binding.avgvisValue.text = it.day.avgvis_km.toString() + "км"
                binding.uvIndexValue.text = it.day.uv.toString()
                binding.sunRiseValue.text = it.astro.sunrise
                binding.sunSetValue.text = it.astro.sunset
                binding.moonRiseValue.text = it.astro.moonrise
                binding.moonSetValue.text = it.astro.moonset

                binding.moonPhaseValue.text = when(it.astro.moon_phase){
                    "New Moon" -> getString(R.string.Full_Moon)
                    "Waxing Crescent" -> getString(R.string.Waxing_Crescent)
                    "First Quarter" -> getString(R.string.First_Quarter)
                    "Waxing Gibbous" -> getString(R.string.Waning_Gibbous)
                    "Full Moon" -> getString(R.string.Full_Moon)
                    "Waning Gibbous" -> getString(R.string.Waning_Gibbous)
                    "Last Quarter" -> getString(R.string.Last_Quarter)
                    "Waning Crescent" -> getString(R.string.Waning_Crescent)
                    else -> "Какой-то новый вид луны, неизвестный разработчику"
                }

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