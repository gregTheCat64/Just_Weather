package ru.javacat.justweather.ui

import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import ru.javacat.justweather.NetworkError
import ru.javacat.justweather.R
import ru.javacat.justweather.databinding.FragmentPlaceBinding
import ru.javacat.justweather.models.Place
import ru.javacat.justweather.ui.adapters.OnPlacesInteractionListener
import ru.javacat.justweather.ui.adapters.PlacesAdapter
import ru.javacat.justweather.ui.view_models.MainViewModel
import ru.javacat.justweather.util.AndroidUtils


class PlaceFragment : Fragment() {
    private lateinit var binding: FragmentPlaceBinding
    private lateinit var adapter: PlacesAdapter
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.slide_right)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaceBinding.inflate(inflater, container, false)

        initLoadingStateObserving()
        initObserver()

        binding.addPlaceBtn.setOnClickListener {
            val placeName = binding.placeInput.text.toString()
            AndroidUtils.hideKeyboard(it)
            if (placeName.isNotEmpty()) {
                viewModel.loadWeatherByName(placeName, 3)
                //viewModel.savePlace(Place(0,placeName))
                binding.placeInput.text?.clear()

            } else {
                Toast.makeText(requireContext(), "Введите город", Toast.LENGTH_SHORT).show()
            }
        }
        return binding.root
    }

    private fun initObserver() {
        adapter = PlacesAdapter(object : OnPlacesInteractionListener {
            override fun onSetPlace(item: Place) {
                viewModel.loadWeatherByName(item.name, 3)
                parentFragmentManager
                    .popBackStack()
            }

            override fun onRemovePlace(item: Place) {
                viewModel.removePlace(item.id)
            }
        })
        binding.placesList.adapter = adapter
        viewModel.placeData.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            Log.i("MyLog", it.toString())
        }
    }

    private fun initLoadingStateObserving(){
        viewModel.loadingState.observe(viewLifecycleOwner){
            when (it) {
                is  LoadingState.NetworkError ->  Toast.makeText(requireContext(), "Проблемы с соединением", Toast.LENGTH_SHORT).show()
                is LoadingState.InputError -> Toast.makeText(requireContext(), "Город не найден", Toast.LENGTH_SHORT).show()
                else -> null
            }
        }
    }

    companion object {
        fun newInstance() = PlaceFragment()
    }
}