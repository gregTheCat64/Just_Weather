package ru.javacat.justweather.ui

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
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.javacat.justweather.R
import ru.javacat.justweather.base.BaseFragment
import ru.javacat.justweather.databinding.FragmentMainBinding
import ru.javacat.justweather.response_models.Forecastday
import ru.javacat.justweather.ui.view_models.MainViewModel
import ru.javacat.justweather.util.load
import ru.javacat.justweather.util.toWindRus
import kotlin.math.roundToInt


class MainFragment : BaseFragment<FragmentMainBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentMainBinding =
        { inflater, container ->
            FragmentMainBinding.inflate(inflater, container, false)
        }


    private lateinit var adapter: MainAdapter
    private lateinit var refreshAnimation: Animation
    private lateinit var pushAnimation: Animation

    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("MyTag", "onCreate")
        //requireActivity().setTheme(R.style.Base_Theme_RainWeather)
        //requireActivity().baseContext.theme.applyStyle(R.style.Base_Theme_RainWeather, true)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            requireActivity().finish()
        }

        refreshAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate)
        pushAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.push)

        //val inflater = TransitionInflater.from(requireContext())
        //exitTransition = inflater.inflateTransition(R.transition.fade)
        //enterTransition = inflater.inflateTransition(R.transition.slide_right)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.i("MyTag", "onCreateView")



        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        initDataObserver()

        binding.placeLayout.setOnClickListener {
            it.setBackgroundColor(resources.getColor(R.color.md_theme_light_primary))
            findNavController().navigate(R.id.action_mainFragment_to_placeFragment)
//            parentFragmentManager
//                .beginTransaction()
//                .addToBackStack(null)
//                .replace(R.id.fragmentContainer, PlaceFragment.newInstance())
//                .commit()
        }

        binding.refresh.setOnClickListener {
            println(viewModel.currentPlace.toString())
            viewModel.setPlace(viewModel.currentPlace.value.toString(), 3)
            it.startAnimation(refreshAnimation)
        }
    }


    private fun initDataObserver() {
        Log.i("MyLog", "observing data")


        lifecycleScope.launch {
            viewModel.data.collectLatest {weather->
                binding.apply {
                    alarmCard.visibility = View.INVISIBLE

                    weather?.let {
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
                    }
                    //it.forecast.forecastday.get(0).astro.is_moon_up

                }
            }
        }
        initRecView()

}


private fun initRecView() {
    Log.i("MyLog", "Init RecView")

    adapter = MainAdapter(object : OnInteractionListener {
        override fun onForecastItem(item: Forecastday, view: View) {
            val color = context!!.resources.getColor(R.color.md_theme_light_primary)
            view.setBackgroundColor(color)
            viewModel.chooseForecastDay(item)

            findNavController().navigate(R.id.action_mainFragment_to_forecastFragment)
//                parentFragmentManager
//                    .beginTransaction()
//                    .addToBackStack(null)
//                    .replace(R.id.fragmentContainer, ForecastFragment.newInstance())
//                    .commit()
        }
    })
    binding.daysRecView.adapter = adapter
    val list = viewModel.data.value?.forecast?.forecastday
    Log.i("MyLog", "${list?.size}")
    adapter.submitList(list)
}


companion object {
    fun newInstance(): MainFragment {
        return MainFragment()
    }
}
}