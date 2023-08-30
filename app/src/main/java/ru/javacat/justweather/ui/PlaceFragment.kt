package ru.javacat.justweather.ui

import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.javacat.justweather.NetworkError
import ru.javacat.justweather.R
import ru.javacat.justweather.base.BaseFragment
import ru.javacat.justweather.databinding.FragmentPlaceBinding
import ru.javacat.justweather.models.Place
import ru.javacat.justweather.ui.adapters.OnPlacesInteractionListener
import ru.javacat.justweather.ui.adapters.PlacesAdapter
import ru.javacat.justweather.ui.view_models.MainViewModel
import ru.javacat.justweather.util.AndroidUtils


class PlaceFragment : BaseFragment<FragmentPlaceBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentPlaceBinding ={
        inflater, container ->
        FragmentPlaceBinding.inflate(inflater, container, false)
    }
    private lateinit var adapter: PlacesAdapter
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //val inflater = TransitionInflater.from(requireContext())
        //enterTransition = inflater.inflateTransition(R.transition.slide_right)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        initLoadingStateObserving()
        initObserver()

        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.placeInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE){
                val placeName = binding.placeInput.text.toString()
                AndroidUtils.hideKeyboard(requireView())
                if (placeName.isNotEmpty()) {
                    viewModel.findPlace(placeName, 3)
                    binding.placeInput.text?.clear()

                } else {
                    Toast.makeText(requireContext(), getString(R.string.enter_the_city), Toast.LENGTH_SHORT).show()
                }
            }
            true
        }

        binding.addPlaceBtn.setOnClickListener {
            val placeName = binding.placeInput.text.toString()
            AndroidUtils.hideKeyboard(it)
            if (placeName.isNotEmpty()) {
                viewModel.findPlace(placeName, 3)
                binding.placeInput.text?.clear()

            } else {
                Toast.makeText(requireContext(), getString(R.string.enter_the_city), Toast.LENGTH_SHORT).show()
            }
        }

        super.onViewCreated(view, savedInstanceState)
    }

    private fun initObserver() {
        adapter = PlacesAdapter(object : OnPlacesInteractionListener {
            override fun onSetPlace(item: Place) {
                viewModel.setPlace(item.name, 3)
                findNavController().navigateUp()
//                parentFragmentManager
//                    .popBackStack()
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