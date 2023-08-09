package ru.javacat.justweather.ui

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import ru.javacat.justweather.databinding.FragmentMainBinding
import ru.javacat.justweather.util.isPermissionGranted
import ru.javacat.justweather.util.load
import kotlin.math.roundToInt


class MainFragment: Fragment() {
    private lateinit var pLauncher: ActivityResultLauncher<String>
    private lateinit var binding: FragmentMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container,false)

        viewModel.loadWeatherByName("London")

        viewModel.data.observe(viewLifecycleOwner){
            val image = "https://koshka.top/uploads/posts/2021-11/1637342603_11-koshka-top-p-kotik-risovat-legko-16.jpg"
            binding.apply {
                tempTxtView.text = it.current.temp_c.roundToInt().toString() + " \u2103"
                cityTxtView.text = it.location.name
                conditionTxtView.text = it.current.condition.text
                realFeelTxtView.text = it.current.feelslike_c.toString() + "℃"
                //imageView.load(image)
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermission()
    }

    private fun permissionListener(){
        pLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ){
            Toast.makeText(requireContext(), "Permission is $it", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkPermission(){
        if (!isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)){
            permissionListener()
            pLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    companion object {
        fun newInstance(): MainFragment {
            return MainFragment()
        }
    }
}