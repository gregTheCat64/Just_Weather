package ru.javacat.justweather.ui

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionInflater
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.javacat.justweather.domain.models.suggestModels.FoundLocation
import ru.javacat.justweather.ui.adapters.OnPlacesInteractionListener
import ru.javacat.justweather.ui.adapters.OnSearchPlacesInteractionListener
import ru.javacat.justweather.ui.adapters.PlacesAdapter
import ru.javacat.justweather.ui.adapters.SearchPlacesAdapter
import ru.javacat.justweather.ui.util.AndroidUtils
import ru.javacat.justweather.ui.util.LocationListenerImplFragment
import ru.javacat.justweather.ui.util.coordsFlow
import ru.javacat.justweather.ui.util.getLocationOverGps
import ru.javacat.justweather.ui.util.getLocationOverNetwork
import ru.javacat.justweather.ui.util.snack
import ru.javacat.justweather.ui.view_models.PlaceViewModel
import ru.javacat.ui.R
import ru.javacat.ui.databinding.FragmentPlaceBinding

@AndroidEntryPoint
class PlaceFragment : LocationListenerImplFragment<FragmentPlaceBinding>() {
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

        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.slide_right)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.i("PlaceFragment", "onViewCreated")
        initLoadingStateObserving()
        initObserver()
        initSearchBar()
        initSearchListObserver()

        binding.placeInput



        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.refreshBtn.setOnClickListener {
            updateDb()
        }

        binding.locateBtn.setOnClickListener {
            when (Build.VERSION.SDK_INT) {
                in 1..29 -> getLocationOverGps()
                else -> getLocationOverNetwork()
            }
            //snack("coords: $coords")

        }

        lifecycleScope.launch{
            coordsFlow.collect  {
                //snack("coordsFlow: $it")
                viewModel.getLocationByCoords("${it.first}, ${it.second}")
                Log.i("MainFrag","$it")
            }
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
            override fun onSetPlace(item: ru.javacat.justweather.domain.models.Weather) {
                viewModel.setLocation(item.id)
            }
            override fun onRemovePlace(item: ru.javacat.justweather.domain.models.Weather) {
                if (!item.isLocated ){
                    viewModel.removeLocation(item.id)
                }
            }
        })
        binding.placesList.adapter = adapter
        viewModel.allWeathersFlow.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            Log.i("MyLog", it.toString())
        }

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT
        ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                TODO("Not yet implemented")
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val item = adapter.getItemAt(viewHolder.adapterPosition)
                if (!item.isLocated ){
                    viewModel.removeLocation(item.id)
                }
            }

            override fun getSwipeDirs(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                if (viewHolder.adapterPosition == 0) return 0
                return super.getSwipeDirs(recyclerView, viewHolder)
            }
        }
        ).attachToRecyclerView(binding.placesList)
    }

    private fun initLoadingStateObserving(){
        Log.i("PlaceFragment", "initLoadingStateObserver")
        viewModel.loadingState.observe(viewLifecycleOwner){
            when (it) {
                is LoadingState.NetworkError -> {
                    binding.progressBar.visibility = View.INVISIBLE
                    Toast.makeText(requireContext(),
                        getString(R.string.Connection_problem), Toast.LENGTH_SHORT)
                        .show()
                }
                is LoadingState.InputError -> {
                    binding.progressBar.visibility = View.INVISIBLE
                    Toast.makeText(requireContext(),
                        getString(R.string.city_not_found), Toast.LENGTH_SHORT).show()
                }
                is LoadingState.Load -> binding.progressBar.visibility = View.VISIBLE
                is LoadingState.Success -> {
                    binding.progressBar.visibility = View.INVISIBLE
                    findNavController().navigateUp()
                }
                is LoadingState.Found -> {
                    binding.progressBar.visibility = View.INVISIBLE
                }
                is LoadingState.Updated -> {
                    binding.progressBar.visibility = View.INVISIBLE
                    snack(getString(R.string.locations_updated))
                }
                is LoadingState.LocationIsUnabled -> {
                    snack(getString(R.string.location_disabled))
                }
            }
        }
    }

    private fun initSearchBar() {
        val searchView: SearchView = binding.placeInput
        searchAdapter = SearchPlacesAdapter(object : OnSearchPlacesInteractionListener{
            override fun onSetPlace(item: FoundLocation) {
                val country = item.address.component.firstOrNull{
                    it.kind[0] == "COUNTRY"
                }?.name?:""
                val province =  item.address.component.lastOrNull{
                    it.kind[0] == "PROVINCE"
                }?.name?:""
                val localTitle =  item.address.component.lastOrNull{
                    it.kind[0] == "LOCALITY"
                }?.name?:""

                val subtitle = "$country, $province"

                viewModel.getCoordinates(item.uri, localTitle, subtitle)
            }
        })

        binding.searchList.layoutManager = LinearLayoutManager(requireContext())
        binding.searchList.adapter = searchAdapter

        searchView.setOnCloseListener {
            AndroidUtils.hideKeyboard(requireView())
            false
        }


        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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

    private fun updateDb(){
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO){
            viewModel.updateDb()
        }
    }

}