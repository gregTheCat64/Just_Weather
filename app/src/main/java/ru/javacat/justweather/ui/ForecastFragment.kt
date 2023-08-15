package ru.javacat.justweather.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import ru.javacat.justweather.databinding.FragmentForecastBinding

class ForecastFragment: Fragment() {
    private lateinit var binding: FragmentForecastBinding
    private val viewModel: MainViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentForecastBinding.inflate(inflater, container, false)


//        val forecastday = viewModel.forecastData.value
//        binding.dateTxtView.text = forecastday?.date
        initForecastObserver()


        return binding.root
    }

    private fun initForecastObserver(){
        lifecycleScope.launch {
            viewModel.forecastData.observe(viewLifecycleOwner) {
                Toast.makeText(requireContext(), "Llala", Toast.LENGTH_SHORT).show()
                binding.dateTxtView.text = it.date
            }
        }
    }


    companion object{
        fun newInstance(): ForecastFragment{
            return ForecastFragment()
        }
    }
}