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
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.javacat.justweather.R
import ru.javacat.justweather.databinding.FragmentMainBinding
import ru.javacat.justweather.response_models.Forecastday
import ru.javacat.justweather.ui.view_models.MainViewModel
import ru.javacat.justweather.util.load
import ru.javacat.justweather.util.toWindRus
import kotlin.math.roundToInt


class MainFragment: Fragment() {

    private lateinit var binding: FragmentMainBinding
    private lateinit var adapter: MainAdapter


    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("MyTag", "onCreate")

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            requireActivity().finish()
        }

        val inflater = TransitionInflater.from(requireContext())
        //exitTransition = inflater.inflateTransition(R.transition.fade)
        enterTransition = inflater.inflateTransition(R.transition.slide_right)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container,false)
        Log.i("MyTag", "onCreateView")

        val refreshAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate)

        initDataObserver()

        binding.placeLayout.setOnClickListener {
            findNavController().navigate(R.id.placeFragment)
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



        return binding.root
    }


    private fun initDataObserver(){
        Log.i("MyLog", "observing data")
            viewModel.data.observe(viewLifecycleOwner){
                binding.apply {
                    alarmCard.visibility = View.INVISIBLE
                    //it.forecast.forecastday.get(0).astro.is_moon_up
                    tempTxtView.text = it.current.temp_c.roundToInt().toString() + "°"
                    cityTxtView.text = it.location.name
                    //conditionTxtView.text = it.current.condition.text
                    realFeelTxtView.text = it.current.feelslike_c.roundToInt().toString() + "°"
                    imageView.load(it.current.condition.icon)
                    detailsLayout.cloud.text = it.current.cloud.toString()+"%"
                    detailsLayout.windSpeed.text = it.current.wind_kph.roundToInt().toString()+"км/ч"
                    detailsLayout.windDir.text = it.current.wind_dir.toWindRus()
                    detailsLayout.precipation.text = it.current.precip_mm.toString()+"мм"
                    detailsLayout.humidity.text = it.current.humidity.toString()+"%"
                    detailsLayout.uvIndex.text = it.current.uv.toString()
                    val alerts = it.alerts.alert


                    for (element in alerts){
                        if (element.desc.isNotEmpty()){
                            //Toast.makeText(requireContext(), element.desc, Toast.LENGTH_LONG).show()
                            //Snackbar.make(requireView(),element.desc,Snackbar.LENGTH_LONG).show()
                            alarmCard.visibility = View.VISIBLE
                            alarmMsg.text = element.desc
                        } else {
                            alarmCard.visibility = View.INVISIBLE
                        }
                    }
                }
                initRecView()
            }
    }


    private fun initRecView() {
        Log.i("MyLog", "Init RecView")

        adapter = MainAdapter(object : OnInteractionListener{
            override fun onForecastItem(item: Forecastday) {
                viewModel.chooseForecastDay(item)

                findNavController().navigate(R.id.forecastFragment)
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