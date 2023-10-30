package ru.javacat.justweather.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
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
        //val inflater = TransitionInflater.from(requireContext())
        //enterTransition = inflater.inflateTransition(R.transition.slide_right)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.backBtn.setOnClickListener {
            //parentFragmentManager.popBackStack()
            findNavController().navigateUp()
        }
        initForecastObserver()
        initHoursObserver()

        super.onViewCreated(view, savedInstanceState)
    }


    private fun initForecastObserver() {
        lifecycleScope.launch {
            viewModel.forecastData.observe(viewLifecycleOwner) {
                binding.apply {
                    conditionImage.load(it.day.condition.icon)
                    conditionValue.text = it.day.condition.text
                    dateTxtView.text = it.date.asLocalDate()
                    avgTempValue.text = it.day.avgtemp_c.toString() + "°"
                    maxTempValue.text = it.day.maxtemp_c.toString()
                    minTempValue.text = it.day.mintemp_c.toString()
                    maxWindSpeedValue.text = it.day.maxwind_kph.roundToInt().toString() + "км/ч"
                    totalPrecipValue.text = it.day.totalprecip_mm.toString() + "мм"
                    avgHumidityValue.text = it.day.avghumidity.toString() + "%"
                    avgvisValue.text = it.day.avgvis_km.toString() + "км"
                    uvIndexValue.text = it.day.uv.toString()
                    sunRiseValue.text = it.astro.sunrise
                    sunSetValue.text = it.astro.sunset
                    moonRiseValue.text = it.astro.moonrise
                    moonSetValue.text = it.astro.moonset


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

                //initForecastRecView()
            }
        }
    }

    private fun initHoursObserver() {
        viewModel.hoursData?.observe(viewLifecycleOwner){
            initForecastRecView()
        }
    }

    private fun initForecastRecView() {
        forecastAdapter = ForecastAdapter()
        //viewModel.chooseForecastData(Forecastday())

        binding.hoursRecView.adapter = forecastAdapter
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