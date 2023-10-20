package ru.javacat.justweather.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.SearchView.OnQueryTextListener
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ru.javacat.justweather.base.BaseFragment
import ru.javacat.justweather.databinding.FragmentPlaceBinding
import ru.javacat.justweather.domain.models.SearchLocation
import ru.javacat.justweather.models.Place
import ru.javacat.justweather.ui.adapters.OnPlacesInteractionListener
import ru.javacat.justweather.ui.adapters.OnSearchPlacesInteractionListener
import ru.javacat.justweather.ui.adapters.PlacesAdapter
import ru.javacat.justweather.ui.adapters.SearchPlacesAdapter
import ru.javacat.justweather.ui.view_models.PlaceViewModel

@AndroidEntryPoint
class PlaceFragment : BaseFragment<FragmentPlaceBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentPlaceBinding ={
        inflater, container ->
        FragmentPlaceBinding.inflate(inflater, container, false)
    }
    private lateinit var adapter: PlacesAdapter
    private lateinit var searchAdapter: SearchPlacesAdapter
    private val viewModel: PlaceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i("PlaceFragment", "onCreate")
        super.onCreate(savedInstanceState)


        //val inflater = TransitionInflater.from(requireContext())
        //enterTransition = inflater.inflateTransition(R.transition.slide_right)
    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.i("PlaceFragment", "onViewCreated")
        initLoadingStateObserving()
        initObserver()
        initSearchBar()
        initSearchListObserver()

        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }

//        binding.placeInput.setOnEditorActionListener { _, actionId, _ ->
//            if (actionId == EditorInfo.IME_ACTION_DONE){
//                val placeName = binding.placeInput.text.toString()
//                AndroidUtils.hideKeyboard(requireView())
//                if (placeName.isNotEmpty()) {
//                    viewModel.findPlace(placeName)
//                    binding.placeInput.text?.clear()
//
//                } else {
//                    Toast.makeText(requireContext(), getString(R.string.enter_the_city), Toast.LENGTH_SHORT).show()
//                }
//            }
//            true
//        }

//        binding.addPlaceBtn.setOnClickListener {
//            val placeName = binding.placeInput.text.toString()
//            AndroidUtils.hideKeyboard(it)
//            if (placeName.isNotEmpty()) {
//                viewModel.findPlace(placeName)
//                binding.placeInput.text?.clear()
//                binding.placesList.smoothScrollToPosition(0)
//            } else {
//                Toast.makeText(requireContext(), getString(R.string.enter_the_city), Toast.LENGTH_SHORT).show()
//            }
//        }



        super.onViewCreated(view, savedInstanceState)
    }

    private fun initObserver() {
        Log.i("PlaceFragment", "initObserver")
        adapter = PlacesAdapter(object : OnPlacesInteractionListener {
            override fun onSetPlace(item: Place) {
                viewModel.setPlace(item.name, 3)
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
        Log.i("PlaceFragment", "initLoadingStateObserver")
        viewModel.loadingState.observe(viewLifecycleOwner){
            when (it) {
                is  LoadingState.NetworkError -> {
                    binding.progressBar.visibility = View.INVISIBLE
                    Toast.makeText(requireContext(), "Проблемы с соединением", Toast.LENGTH_SHORT)
                        .show()
                }
                is LoadingState.InputError -> {
                    binding.progressBar.visibility = View.INVISIBLE
                    Toast.makeText(requireContext(), "Город не найден", Toast.LENGTH_SHORT).show()
                }
                is LoadingState.Load -> binding.progressBar.visibility = View.VISIBLE
                is LoadingState.Success -> {
                    binding.progressBar.visibility = View.INVISIBLE
                    findNavController().navigateUp()
                }
                is LoadingState.Found -> {
                    binding.progressBar.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun initSearchBar() {
        val searchview: SearchView = binding.placeInput as SearchView
        searchAdapter = SearchPlacesAdapter(object : OnSearchPlacesInteractionListener{
            override fun onSetPlace(item: SearchLocation) {
                viewModel.savePlace(Place(0, item.name, item.region))
                viewModel.setPlace(item.url, 3)
            }

        })

        binding.searchList?.layoutManager = LinearLayoutManager(requireContext())
        binding.searchList?.adapter = searchAdapter

//        searchview.setOnCloseListener {
//            AndroidUtils.hideKeyboard(requireView())
//            false
//        }

        searchview.setOnQueryTextListener(object : OnQueryTextListener{
            override fun onQueryTextSubmit(location: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(location: String?): Boolean {
                if (location?.length!! >= 3) {
                    viewModel.getLocations(location)
                } else viewModel.clearPlace()
                return false
            }
        })
    }

    private fun initSearchListObserver() {
        viewModel.foundLocations.observe(viewLifecycleOwner){
            searchAdapter.submitList(it)
        }
    }

}