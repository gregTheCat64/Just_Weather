package ru.javacat.justweather.ui

import android.app.Activity
import android.graphics.BitmapFactory
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.javacat.justweather.R
import ru.javacat.justweather.base.BaseFragment
import ru.javacat.justweather.databinding.FragmentMainBinding
import ru.javacat.justweather.response_models.Forecastday
import ru.javacat.justweather.ui.view_models.MainViewModel
import ru.javacat.justweather.util.changeColorOnPush
import ru.javacat.justweather.util.load
import ru.javacat.justweather.util.refreshAnimation
import ru.javacat.justweather.util.toWindRus
import kotlin.math.roundToInt


@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentMainBinding =
        { inflater, container ->
            FragmentMainBinding.inflate(inflater, container, false)
        }

    private lateinit var adapter: MainAdapter

    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("MyTag", "onCreateFragment")
        //requireActivity().setTheme(R.style.Base_Theme_RainWeather)
        //requireActivity().baseContext.theme.applyStyle(R.style.Base_Theme_RainWeather, true)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            requireActivity().finish()
        }

        //refreshAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate)
        //pushAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.push)

        //val inflater = TransitionInflater.from(requireContext())
        //exitTransition = inflater.inflateTransition(R.transition.fade)
        //enterTransition = inflater.inflateTransition(R.transition.slide_right)
    }

    override fun onResume() {
        super.onResume()
        val currentPlace = viewModel.weatherFlow
        if (currentPlace.value == null) {
            viewModel.updateWeather()
        }

        Log.i("MyTag","onResume MainFragment")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.i("MyTag", "onCreateMainView")
        viewModel.saveCurrentPlace()

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initDataObserver()

        binding.placeLayout.setOnClickListener {
            it.changeColorOnPush(requireContext())
            findNavController().navigate(R.id.action_mainFragment_to_placeFragment)
//            parentFragmentManager
//                .beginTransaction()
//                .addToBackStack(null)
//                .replace(R.id.fragmentContainer, PlaceFragment.newInstance())
//                .commit()
        }

        binding.refresh.setOnClickListener {
            //val currentPlace = viewModel.data.value?.location?.name

//            println(currentPlace)
//            currentPlace?.let { place ->
//                viewModel.findPlaceByLocation(place, 3) }
            viewModel.updateWeather()
            it.refreshAnimation(requireContext())
        }
    }


    private fun initDataObserver() {
        Log.i("MyTag", "observing data")

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.weatherFlow.collectLatest{weather->

//        }
//        lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.STARTED){
//                viewModel.weatherFlow.collect {weather->
                    Log.i("MyTag", "weather: ${weather?.location}")
                    binding.apply {
                        alarmCard.visibility = View.INVISIBLE

                        weather?.let {

                            if (true) {
                                val activity = requireActivity() as AppCompatActivity
                                val back =
                                    BitmapFactory.decodeResource(resources, R.drawable.back_rainy)
                                val bg = AppCompatResources.getDrawable(
                                    requireContext(),
                                    R.drawable.back_rainy
                                )
                                //activity.window.setBackgroundDrawable(bg)
                                val fc = activity.findViewById<FragmentContainerView>(R.id.fragmentContainer)
                                fc.background = resources.getDrawable(R.drawable.back_rainy)

                            }
                            tempTxtView.text =
                                it.current.temp_c.roundToInt().toString() + getString(R.string.celcius)
                            cityTxtView.text = it.location.name
                            //conditionTxtView.text = it.current.condition.text
                            realFeelTxtView.text =
                                it.current.feelslike_c.roundToInt().toString() + getString(
                                    R.string.celcius
                                )
                            it.current.condition.icon.let { it1 -> imageView.load(it1) }
                            detailsLayout.cloud.text = it.current.cloud.toString() + "%"
                            detailsLayout.windSpeed.text =
                                it.current.wind_kph.roundToInt().toString() + getString(
                                    R.string.km_h
                                )
                            detailsLayout.windDir.text = it.current.wind_dir.toWindRus()
                            detailsLayout.precipation.text =
                                it.current.precip_mm.toString() + getString(R.string.mm)
                            detailsLayout.humidity.text =
                                it.current.humidity.toString() + getString(R.string.percent)
                            detailsLayout.uvIndex.text = it.current.uv.toString()
                            val alerts = it.alerts.alert
                            for (element in alerts) {
                                if (element.desc.isNotEmpty()) {
                                    //Toast.makeText(requireContext(), element.desc, Toast.LENGTH_LONG).show()
                                    //Snackbar.make(requireView(),element.desc,Snackbar.LENGTH_LONG).show()
                                    alarmCard.visibility = View.VISIBLE
                                    alarmMsg.text = element.desc
                                } else {
                                    alarmCard.visibility = View.INVISIBLE
                                }
                            }

                            adapter = MainAdapter(object : OnInteractionListener {
                                override fun onForecastItem(item: Forecastday, view: View) {
                                    //val color = context!!.resources.getColor(R.color.md_theme_light_primary)
                                    view.changeColorOnPush(requireContext())
                                    viewModel.chooseForecastDay(item)

                                    findNavController().navigate(R.id.action_mainFragment_to_forecastFragment)

                                }
                            })
                            binding.daysRecView.adapter = adapter
                            val list = weather.forecast.forecastday
                            //Log.i("MyLog", "${list?.size}")
                            adapter.submitList(list)
                        }
                        //it.forecast.forecastday.get(0).astro.is_moon_up

                    }
                }
            }
        }

            }

        }
        //initRecView()
