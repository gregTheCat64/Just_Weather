package ru.javacat.justweather.ui

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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.javacat.justweather.ui.adapters.OnPlacesInteractionListener
import ru.javacat.justweather.ui.adapters.OnSearchPlacesInteractionListener
import ru.javacat.justweather.ui.adapters.PlacesAdapter
import ru.javacat.justweather.ui.adapters.SearchPlacesAdapter
import ru.javacat.justweather.ui.base.BaseFragment
import ru.javacat.justweather.ui.util.AndroidUtils
import ru.javacat.justweather.ui.view_models.PlaceViewModel
import ru.javacat.ui.databinding.FragmentPlaceBinding

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
        //updateDb()
        initLoadingStateObserving()
        initObserver()
        initSearchBar()
        initSearchListObserver()



        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.refreshBtn.setOnClickListener {
            updateDb()
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
                viewModel.setPlace(item.location.lat.toString()+","+item.location.lon.toString())

            }
            override fun onRemovePlace(item: ru.javacat.justweather.domain.models.Weather) {
                if (!item.isCurrent ){
                    viewModel.removePlace(item.id) 
                } else Toast.makeText(requireContext(), "Невозможно удалить текущий город", Toast.LENGTH_SHORT).show()
               
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
                //viewModel.removePlace(adapter.getItemId(viewHolder.adapterPosition))
                val item = adapter.getItemAt(viewHolder.adapterPosition)
                if (!item.isCurrent ){
                    viewModel.removePlace(item.id)
                }
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
            override fun onSetPlace(item: ru.javacat.justweather.domain.models.SearchLocation) {
                //viewModel.savePlace(Place(0, item.name, item.region))
                viewModel.setPlace(item.url)
            }

        })

        binding.searchList?.layoutManager = LinearLayoutManager(requireContext())
        binding.searchList?.adapter = searchAdapter

        searchview.setOnCloseListener {
            AndroidUtils.hideKeyboard(requireView())
            false
        }


        searchview.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener {
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