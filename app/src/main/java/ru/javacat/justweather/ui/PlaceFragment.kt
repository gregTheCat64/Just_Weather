package ru.javacat.justweather.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import ru.javacat.justweather.databinding.FragmentPlaceBinding
import ru.javacat.justweather.models.Place
import ru.javacat.justweather.ui.adapters.PlacesAdapter
import ru.javacat.justweather.ui.view_models.PlacesViewModel

class PlaceFragment: Fragment() {
    private lateinit var binding: FragmentPlaceBinding
    private lateinit var adapter: PlacesAdapter
    private val viewModel: PlacesViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaceBinding.inflate(inflater, container, false)

        initObserver()

        binding.addPlace.setOnClickListener {
            val placeName = binding.placeInput.text.toString()
            if (placeName.isNotEmpty()){
                viewModel.savePlace(Place(0,placeName))
            }
        }

        return binding.root
    }

    private fun initObserver() {
        viewModel.data.observe(viewLifecycleOwner){
            initPlacesList()
        }
    }

    private fun initPlacesList() {
        adapter = PlacesAdapter()
        binding.placesList.adapter = adapter
        val list = viewModel.data.value

        adapter.submitList(list)
    }

    companion object{
        fun newInstance() = PlaceFragment()
    }
}