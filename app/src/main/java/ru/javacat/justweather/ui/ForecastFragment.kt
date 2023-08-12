package ru.javacat.justweather.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import ru.javacat.justweather.databinding.FragmentForecastBinding

class ForecastFragment: Fragment() {
    private lateinit var binding: FragmentForecastBinding
    private val viewModel: MainViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentForecastBinding.inflate(inflater, container, false)


//        val forecastday = viewModel.forecastData.value
//        binding.dateTxtView.text = forecastday?.date

        viewModel.forecastData.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), "Llala", Toast.LENGTH_SHORT).show()
            binding.dateTxtView.text = it.date
        }

        return binding.root
    }


    companion object{
        fun newInstance(): ForecastFragment{
            return ForecastFragment()
        }
    }
}