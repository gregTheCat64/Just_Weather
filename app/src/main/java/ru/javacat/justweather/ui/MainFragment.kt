package ru.javacat.justweather.ui

import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.gms.location.FusedLocationProviderClient
import ru.javacat.justweather.R
import ru.javacat.justweather.databinding.FragmentMainBinding
import ru.javacat.justweather.response_models.Forecastday
import ru.javacat.justweather.util.load
import kotlin.math.roundToInt


class MainFragment: Fragment() {

    private lateinit var binding: FragmentMainBinding
    private lateinit var adapter: MainAdapter


    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        exitTransition = inflater.inflateTransition(R.transition.fade)
        enterTransition = inflater.inflateTransition(R.transition.slide_right)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container,false)

        initDataObserver()

        Log.i("MyTag", "mainVM: $viewModel")
        binding.refresh.setOnClickListener {

        }



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //initRecView()

        //getLocation()


    }

    private fun initDataObserver(){
        Log.i("MyLog", "observing data")
            viewModel.data.observe(viewLifecycleOwner){
                val image = "https://${it.current.condition.icon}"
                binding.apply {
                    //it.forecast.forecastday.get(0).astro.is_moon_up
                    tempTxtView.text = it.current.temp_c.roundToInt().toString() + "°"
                    cityTxtView.text = it.location.name
                    //conditionTxtView.text = it.current.condition.text
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
                            //Snackbar.make(requireView(),element.desc,Snackbar.LENGTH_LONG).show()
                            alarmMsg.text = element.desc

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
        Log.i("MyLog", "${list?.size}")
        adapter.submitList(list)
    }




    companion object {
        fun newInstance(): MainFragment {
            return MainFragment()
        }
    }
}