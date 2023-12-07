package ru.javacat.justweather.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.javacat.justweather.common.util.asLocalDate
import ru.javacat.justweather.ui.adapters.ForecastAdapter
import ru.javacat.justweather.ui.base.BaseFragment
import ru.javacat.justweather.ui.util.load
import ru.javacat.justweather.ui.view_models.MainViewModel
import ru.javacat.ui.R
import ru.javacat.ui.databinding.FragmentForecastBinding
import kotlin.math.roundToInt

class ForecastFragment : BaseFragment<FragmentForecastBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentForecastBinding =
        { inflater, container ->
            FragmentForecastBinding.inflate(inflater, container, false)
        }
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var forecastAdapter: ForecastAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("ForecastFrag", "onCreate2")
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.slide_right)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.i("ForecastFrag", "onViewCreated")
        binding.backBtn.setOnClickListener {
            //parentFragmentManager.popBackStack()
            findNavController().navigateUp()
        }

        val args = arguments
        val locName = args?.getString("LOC_NAME", "") ?: ""

        forecastAdapter = ForecastAdapter()

        binding.hoursRecView.adapter = forecastAdapter


        initForecastObserver(locName)
        initHoursObserver()

        super.onViewCreated(view, savedInstanceState)
    }


    private fun initForecastObserver(locName: String) {
        Log.i("ForecastFrag", "initForecastObserver")
        val celciusSign = "°"
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.forecastData.collectLatest {
                    Log.i("ForecastFrag", "getting data")
                    viewModel.getHours(it.weatherId, it.date.toString())
                    binding.apply {
                        mainTextView.text = locName
                        conditionImage.load(it.day.condition.icon)
                        conditionValue.text = it.day.condition.text
                        dateTxtView.text = it.date.asLocalDate()
                        avgTempValue.text = "${it.day.avgtemp_c.roundToInt()}$celciusSign"
                        maxTempValue.text = it.day.maxtemp_c.roundToInt().toString() + celciusSign
                        minTempValue.text = it.day.mintemp_c.roundToInt().toString() + celciusSign
                        maxWindSpeedValue.text = (it.day.maxwind_kph*0.28).roundToInt().toString() + " м/с"
                        totalPrecipValue.text = it.day.totalprecip_mm.roundToInt().toString() + " мм"
                        avgHumidityValue.text = it.day.avghumidity.roundToInt().toString() + "%"
                        avgvisValue.text = it.day.avgvis_km.toString() + " км"
                        uvIndexValue.text = it.day.uv.toString()
                        sunRiseValue.text = it.astro.sunrise
                        sunSetValue.text = it.astro.sunset
                        moonRiseValue.text = it.astro.moonrise
                        moonSetValue.text = it.astro.moonset
                        var precipChance = ""
//                if (it.day.daily_chance_of_rain > 0 || it.day.daily_chance_of_snow > 0) {
//                    precipChance.append("дождь: ${it.day.daily_chance_of_rain} % ")
//                    precipChance.append("  снег: ${it.day.daily_chance_of_snow} %")
//                    precipChanceValue.text = precipChance
//                }

                        if (it.day.daily_will_it_rain == 1 && it.day.daily_will_it_snow == 1) {
                            precipChance = "Cнег с дождем"
                        }  else {
                            if (it.day.daily_will_it_rain == 1) precipChance = "Будет дождик"
                            if (it.day.daily_will_it_snow == 1) precipChance = "Будет снег"
                        }
                        precipChanceValue.text = precipChance




                        moonPhaseValue.text = when (it.astro.moon_phase) {
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
                    }
                }
            }

        }

    }

    private fun initHoursObserver() {
        viewModel.hoursData?.observe(viewLifecycleOwner) {
            initForecastRecView()
        }
    }

    private fun initForecastRecView() {

        val list = viewModel.hoursData?.value
        forecastAdapter.submitList(list)

        binding.hoursRecView.scrollToPosition(6)
    }


    companion object {
        fun newInstance(): ForecastFragment {
            return ForecastFragment()
        }
    }
}