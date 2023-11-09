package ru.javacat.justweather.ui

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.javacat.justweather.common.util.toLocalDateTime
import ru.javacat.justweather.common.util.toWindRus
import ru.javacat.justweather.ui.adapters.MainAdapter
import ru.javacat.justweather.ui.adapters.OnInteractionListener
import ru.javacat.justweather.ui.base.BaseFragment
import ru.javacat.justweather.ui.util.changeColorOnPush
import ru.javacat.justweather.ui.util.load
import ru.javacat.justweather.ui.util.refreshAnimation
import ru.javacat.justweather.ui.view_models.MainViewModel
import ru.javacat.ui.R
import ru.javacat.ui.databinding.FragmentMainBinding
import java.time.LocalTime
import kotlin.math.roundToInt


@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentMainBinding =
        { inflater, container ->
            FragmentMainBinding.inflate(inflater, container, false)
        }

    private lateinit var adapter: MainAdapter
    //private lateinit var currentTime: LocalTime
    private lateinit var fc: FragmentContainerView

    private lateinit var back5: Drawable
    private lateinit var back8: Drawable
    private lateinit var back12: Drawable
    private lateinit var back18: Drawable
    private lateinit var back20: Drawable
    private lateinit var back22: Drawable
    private lateinit var backRainy: Drawable



    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("MainFragment", "onCreateFragment")
        //currentTime = LocalTime.now()


        requireActivity().onBackPressedDispatcher.addCallback(this) {
            requireActivity().finish()
        }


        //backgrounds
        back5 = AppCompatResources.getDrawable(requireContext(), R.drawable.back_5)!!
        back8 = AppCompatResources.getDrawable(requireContext(), R.drawable.back_8)!!
        back12 = AppCompatResources.getDrawable(requireContext(), R.drawable.back_12)!!
        back18 = AppCompatResources.getDrawable(requireContext(), R.drawable.back_18)!!
        back20 = AppCompatResources.getDrawable(requireContext(), R.drawable.back_20)!!
        back22 = AppCompatResources.getDrawable(requireContext(), R.drawable.back_22)!!
        backRainy = AppCompatResources.getDrawable(requireContext(), R.drawable.back_rainy)!!

        //val inflater = TransitionInflater.from(requireContext())
        //exitTransition = inflater.inflateTransition(R.transition.fade)
        //enterTransition = inflater.inflateTransition(R.transition.slide_right)
    }

    override fun onStart() {
        super.onStart()
        Log.i("MainFragment", "onStart")
    }

    override fun onResume() {
        super.onResume()
//        val currentPlace = viewModel.weatherFlow
//        if (currentPlace.value == null) {
//            viewModel.updateWeather()
//        }

        Log.i("MainFragment", "onResume")
    }

    override fun onDestroy() {
        Log.i("MainFragment", "onDestroy")
        super.onDestroy()

    }

    override fun onDestroyView() {
        Log.i("MainFragment", "onDestroyView")
        super.onDestroyView()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val activity = requireActivity() as AppCompatActivity
        fc = activity.findViewById<FragmentContainerView>(R.id.fragmentContainer)

        Log.i("MainFragment", "onCreateView")

        initDataObserver()

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i("MainFragment", "onViewCreated")
        //updateDb()

        binding.placeLayout.setOnClickListener {
            it.changeColorOnPush(requireContext())
            //updateDb()
            findNavController().navigate(R.id.action_mainFragment_to_placeFragment)
//            parentFragmentManager
//                .beginTransaction()
//                .addToBackStack(null)
//                .replace(R.id.fragmentContainer, PlaceFragment.newInstance())
//                .commit()
        }

        binding.refresh.setOnClickListener {
            viewModel.updateWeather()
            it.refreshAnimation(requireContext())
        }
    }

    private fun initDataObserver() {
        Log.i("MainFragment", "observing data")

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.currentWeatherFlow.observe(viewLifecycleOwner) { weather ->
                    Log.i("MainFragment", "collecting")
                    updateWeather(weather)
                    weather?.forecasts?.let { updateForecast(forecastdays = it) }
                }
            }
        }

//        viewLifecycleOwner.lifecycleScope.launch {
//            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
//                viewModel.forecastFlow.observe(viewLifecycleOwner) {
//                    updateForecast(it)
//                }
//            }
//        }

    }


    private fun updateWeather(weather: ru.javacat.justweather.domain.models.Weather?){
        Log.i("MainFragment", "updateUI")
        binding.apply {
            alarmCard.visibility = View.INVISIBLE

            weather?.let {
                val currentTime = it.location.localtime.toLocalDateTime()
                    .toLocalTime()

                locatedMarker.isVisible = it.isLocated

                updateTime.text = currentTime.toString()

                //val whiteColor = R.color.white
                when{
                    currentTime.isAfter(LocalTime.of(6,0)) && currentTime.isBefore(LocalTime.of(12,0)) -> {
                        fc.background = back5
                        setWhiteTheme()
                        println("back: back5")
                    }

                    currentTime.isAfter(LocalTime.of(12,0)) && currentTime.isBefore(LocalTime.of(18,0)) -> {
                        setWhiteTheme()
                        fc.background = back12
                        println("back: back12")
                    }
                    currentTime.isAfter(LocalTime.of(18,0)) && currentTime.isBefore(LocalTime.of(20,0)) -> {
                        fc.background = back18
                        setWhiteTheme()
                        println("back: back18")
                    }
//                    currentTime.isAfter(LocalTime.of(20,0)) && currentTime.isBefore(LocalTime.of(22,0)) -> {
//                        fc.background = back20
//                        println("back: back20")
//                    }
                    currentTime.isAfter(LocalTime.of(20,0)) || currentTime.isBefore(LocalTime.of(6,0)) -> {
                        setDarkTheme()
                        fc.background = back22
                        println("back: back22")
                    }
                    currentTime.isAfter(LocalTime.of(9,0)) &&  currentTime.isBefore(LocalTime.of(19,0))
                            && it.current.condition.code in (1150..1201) -> {
                        setWhiteTheme()
                        fc.background = backRainy
                    }
                }
                val tempText =  it.current.temp_c.roundToInt()
                    .toString() + getString(R.string.celcius)
                tempTxtView.text = tempText

                cityTxtView.text = it.location.localTitle
                //conditionTxtView.text = it.current.condition.text
                val reelFeelText =  it.current.feelslike_c.roundToInt().toString() + getString(
                    R.string.celcius
                )
                realFeelTxtView.text =reelFeelText

                it.current.condition.icon.let { it1 -> imageView.load(it1) }

                val cloudText = it.current.cloud.toString() + "%"
                detailsLayout.cloud.text = cloudText

                val speedText =  it.current.wind_kph.roundToInt().toString() + getString(
                    R.string.km_h
                )
                detailsLayout.windSpeed.text = speedText

                detailsLayout.windDir.text = it.current.wind_dir.toWindRus()

                val precipText = it.current.precip_mm.toString() + getString(R.string.mm)
                detailsLayout.precipation.text =precipText

                val humidityText =  it.current.humidity.toString() + getString(R.string.percent)
                detailsLayout.humidity.text =humidityText

                detailsLayout.uvIndex.text = it.current.uv.toString()
                val alerts = it.alerts
                val alertMsgBuffer = StringBuilder()
                for (element in alerts) {
                    if (element.desc.isNotEmpty()) {
                        alarmCard.visibility = View.VISIBLE
                        alertMsgBuffer.append(element.desc)
                        alarmMsg.isSelected = true
                    } else {
                        alarmCard.visibility = View.INVISIBLE
                    }
                }
                alarmMsg.text = alertMsgBuffer
            }
            //it.forecast.forecastday.get(0).astro.is_moon_up

        }
    }

    private fun updateForecast(forecastdays: List<ru.javacat.justweather.domain.models.Forecastday>){
        adapter = MainAdapter(object : OnInteractionListener {
            override fun onForecastItem(item: ru.javacat.justweather.domain.models.Forecastday, view: View) {
                //val color = context!!.resources.getColor(R.color.md_theme_light_primary)
                view.changeColorOnPush(requireContext())
                findNavController().navigate(R.id.action_mainFragment_to_forecastFragment)
                viewModel.getHours(item.weatherId, item.date.toString())
                viewModel.chooseForecastDay(item)

            }
        })
        binding.daysRecView.adapter = adapter
        val list = forecastdays
        adapter.submitList(list)
    }

    private fun setDarkTheme(){
        val color = R.color.white
        binding.apply {
            tempTxtView.setTextColor(AppCompatResources.getColorStateList(requireContext(), color))
            realfeelTxt.setTextColor(AppCompatResources.getColorStateList(requireContext(), color))
            realFeelTxtView.setTextColor(AppCompatResources.getColorStateList(requireContext(), color))
            //imageView.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
//            DrawableCompat.setTint(
//                DrawableCompat.wrap(imageView.drawable),
//                ContextCompat.getColor(requireContext(), color)
//            )

        }
    }

    private fun setWhiteTheme(){
        val color = R.color.grey
        binding.apply {
            tempTxtView.setTextColor(AppCompatResources.getColorStateList(requireContext(), color))
            realfeelTxt.setTextColor(AppCompatResources.getColorStateList(requireContext(), color))
            realFeelTxtView.setTextColor(AppCompatResources.getColorStateList(requireContext(), color))
            //imageView.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
//            DrawableCompat.setTint(
//                DrawableCompat.wrap(imageView.drawable),
//                ContextCompat.getColor(requireContext(), color)
//            )

        }
    }

}
//initRecView()
