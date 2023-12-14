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
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.javacat.justweather.common.util.toLocalDateTime
import ru.javacat.justweather.common.util.toWindRus
import ru.javacat.justweather.domain.models.Forecastday
import ru.javacat.justweather.domain.models.Weather
import ru.javacat.justweather.ui.adapters.MainAdapter
import ru.javacat.justweather.ui.adapters.OnInteractionListener
import ru.javacat.justweather.ui.util.LocationListenerImplFragment
import ru.javacat.justweather.ui.util.load
import ru.javacat.justweather.ui.util.pushAnimation
import ru.javacat.justweather.ui.util.refreshAnimation
import ru.javacat.justweather.ui.util.snack
import ru.javacat.justweather.ui.view_models.MainViewModel
import ru.javacat.ui.R
import ru.javacat.ui.databinding.FragmentMainBinding
import java.time.LocalTime
import kotlin.math.roundToInt


@AndroidEntryPoint
class MainFragment : LocationListenerImplFragment<FragmentMainBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentMainBinding =
        { inflater, container ->
            FragmentMainBinding.inflate(inflater, container, false)
        }

    private lateinit var adapter: MainAdapter
    //private lateinit var currentTime: LocalTime
    private lateinit var fc: FragmentContainerView

    private lateinit var back5: Drawable
    private lateinit var back12: Drawable
    private lateinit var back18: Drawable
    private lateinit var back22: Drawable
    private lateinit var backRainy: Drawable

    private var locName = ""
    val bundle = Bundle()



    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("MainFragment", "onCreateFragment")

        //val inflater = TransitionInflater.from(requireContext())
        //exitTransition = inflater.inflateTransition(R.transition.fade)
        //currentTime = LocalTime.now()
        //enterTransition = inflater.inflateTransition(R.transition.slide_right)

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            requireActivity().finish()
        }

        //backgrounds
        back5 = AppCompatResources.getDrawable(requireContext(), R.drawable.back_5)!!
        //back8 = AppCompatResources.getDrawable(requireContext(), R.drawable.back_8)!!
        back12 = AppCompatResources.getDrawable(requireContext(), R.drawable.back_12)!!
        back18 = AppCompatResources.getDrawable(requireContext(), R.drawable.back_evening)!!
        //back20 = AppCompatResources.getDrawable(requireContext(), R.drawable.back_20)!!
        back22 = AppCompatResources.getDrawable(requireContext(), R.drawable.back_22)!!
        backRainy = AppCompatResources.getDrawable(requireContext(), R.drawable.back_rainy)!!

        viewModel.updateCurrentWeather()
    }

    override fun onStart() {
        super.onStart()
        Log.i("MainFragment", "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.i("MainFragment", "onResume")
        //val currentPlace = viewModel.currentWeatherFlow
        //TODO разобраться почему иногда значение NULL

    }

    override fun onPause() {
        Log.i("MainFragment", "onPause")
        super.onPause()
    }

    override fun onStop() {
        Log.i("MainFragment", "onStop")
        super.onStop()
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


        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i("MainFragment", "onViewCreated")

        adapter = MainAdapter(object : OnInteractionListener {
            override fun onForecastItem(item: Forecastday, view: View) {
                //val color = context!!.resources.getColor(R.color.md_theme_light_primary)
                //view.changeColorOnPush(requireContext())
                findNavController().navigate(R.id.forecastFragment, bundle)
                viewModel.chooseForecastDay(item)
            }
        })

        binding.daysRecView.adapter = adapter

        initStateObserver()
        initDataObserver()

        binding.infoBtn.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_infoFragment)
        }

        binding.placeLayout.setOnClickListener {
            it.pushAnimation(requireContext())
            findNavController().navigate(R.id.placeFragment)
//            parentFragmentManager
//                .beginTransaction()
//                .addToBackStack(null)
//                .replace(R.id.fragmentContainer, PlaceFragment.newInstance())
//                .commit()
        }

        binding.refresh.setOnClickListener {
            viewModel.updateCurrentWeather()
            it.refreshAnimation(requireContext())
        }
    }

    private fun initDataObserver() {
        Log.i("MainFragment", "observing data")

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getCurrentWeatherFlow().collectLatest { weather ->
                    Log.i("MainFragment", "collecting")
                    Log.i("MainFragment", "current weather: ${weather?.location?.name}")
                    //if (weather == null) viewModel.updateCurrentWeather()
                    updateUI(weather)
                    locName = weather?.location?.localTitle.toString()
                    bundle.putString("LOC_NAME", locName)

                    weather?.forecasts?.let { updateForecast(forecastdays = it) }
                }
            }
        }
    }

    private fun initStateObserver(){
        viewModel.loadingState.observe(viewLifecycleOwner) {
            when (it) {
                is LoadingState.NetworkError -> {
                    Snackbar.make(
                        requireView(),
                        getString(R.string.network_error),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                is LoadingState.Updated -> {
                    snack(getString(R.string.updated))
                }
                else -> {}
            }
        }
    }


    private fun updateUI(weather: Weather?){
        Log.i("MainFragment", "updateUI")
        binding.apply {
            alarmCard.visibility = View.INVISIBLE

            weather?.let {
                val currentTime = it.current.last_updated.toLocalDateTime()
                    .toLocalTime()

                locatedMarker.isVisible = it.isLocated

                updateTime.text = currentTime.toString()

                when{
                    currentTime.isAfter(LocalTime.of(6,0)) && currentTime.isBefore(LocalTime.of(12,0)) -> {
                        fc.background = back5
                        setLightTheme()
                        //println("back: back5")
                    }

                    currentTime.isAfter(LocalTime.of(12,0)) && currentTime.isBefore(LocalTime.of(18,0)) -> {
                        setLightTheme()
                        fc.background = back12
                        //println("back: back12")
                    }
                    currentTime.isAfter(LocalTime.of(18,0)) && currentTime.isBefore(LocalTime.of(19,0)) -> {
                        fc.background = back18
                        setDarkTheme()
                        //println("back: back18")
                    }
//                    currentTime.isAfter(LocalTime.of(20,0)) && currentTime.isBefore(LocalTime.of(22,0)) -> {
//                        fc.background = back20
//                        println("back: back20")
//                    }
                    currentTime.isAfter(LocalTime.of(19,0)) || currentTime.isBefore(LocalTime.of(6,0)) -> {
                        setDarkTheme()
                        fc.background = back22
                        //println("back: back22")
                    }
                    currentTime.isAfter(LocalTime.of(9,0)) &&  currentTime.isBefore(LocalTime.of(19,0))
                            && it.current.condition.code in (1150..1201) -> {
                        setLightTheme()
                        fc.background = backRainy
                    }
                }
                val tempText =  it.current.temp_c.roundToInt()
                    .toString() + getString(R.string.celcius)
                tempTxtView.text = tempText

                cityTxtView.text = it.location.localTitle
                val reelFeelText =  it.current.feelslike_c.roundToInt().toString() + getString(
                    R.string.celcius
                )
                realFeelTxtView.text =reelFeelText

                it.current.condition.icon.let { it1 -> imageView.load(it1) }

                val speedInmS = (it.current.wind_kph*0.28).roundToInt().toString()
                val speedText =  "$speedInmS ${getString(R.string.m_s)}"

                detailsLayout.windSpeed.text = speedText

                detailsLayout.windDir.text = it.current.wind_dir.toWindRus()

                val humidityText =  it.current.humidity.toString() + getString(R.string.percent)
                detailsLayout.humidity.text = humidityText

                val pressureText = (it.current.pressure_mb*0.75).roundToInt().toString()
                detailsLayout.pressureTextValue.text = pressureText
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
        }
    }

    private fun updateForecast(forecastdays: List<Forecastday>){
//        val bundle = Bundle()
//        bundle.putString("LOC_NAME", locName)
//        adapter = MainAdapter(object : OnInteractionListener {
//            override fun onForecastItem(item: Forecastday, view: View) {
//                //val color = context!!.resources.getColor(R.color.md_theme_light_primary)
//                //view.changeColorOnPush(requireContext())
//                findNavController().navigate(R.id.action_mainFragment_to_forecastFragment, bundle)
//                viewModel.getHours(item.weatherId, item.date.toString())
//                viewModel.chooseForecastDay(item)
//
//            }
//        })

        adapter.submitList(forecastdays)
    }

    private fun setDarkTheme(){
        val color = R.color.white
        binding.apply {
            tempTxtView.setTextColor(AppCompatResources.getColorStateList(requireContext(), color))
            realfeelTxt.setTextColor(AppCompatResources.getColorStateList(requireContext(), color))
            realFeelTxtView.setTextColor(AppCompatResources.getColorStateList(requireContext(), color))

        }
    }

    private fun setLightTheme(){
        val color = R.color.grey
        binding.apply {
            tempTxtView.setTextColor(AppCompatResources.getColorStateList(requireContext(), color))
            realfeelTxt.setTextColor(AppCompatResources.getColorStateList(requireContext(), color))
            realFeelTxtView.setTextColor(AppCompatResources.getColorStateList(requireContext(), color))

        }
    }

}
