package ru.javacat.justweather.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.javacat.justweather.domain.models.SearchLocation
import ru.javacat.justweather.ui.util.pushAnimation
import ru.javacat.ui.R
import ru.javacat.ui.databinding.FoundItemBinding

interface OnSearchPlacesInteractionListener{
    fun onSetPlace(item: ru.javacat.justweather.domain.models.SearchLocation)
}

class SearchPlacesAdapter(
    private val listener: OnSearchPlacesInteractionListener
): ListAdapter<SearchLocation, SearchPlacesAdapter.Holder>(Comparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchPlacesAdapter.Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.found_item, parent, false)
        return SearchPlacesAdapter.Holder(view, listener)
    }

    override fun onBindViewHolder(holder: SearchPlacesAdapter.Holder, position: Int) {
        holder.bind(getItem(position))
    }

    class Holder(view: View, private val listener: OnSearchPlacesInteractionListener): RecyclerView.ViewHolder(view){
        private val binding = FoundItemBinding.bind(view)

        fun bind(item: SearchLocation){
            binding.apply {
                nameValue.text = item.name
                regionValue.text = item.region + ", " + item.country
                root.setOnClickListener {
                    it.pushAnimation(binding.root.context)
                    listener.onSetPlace(item)
                }
            }
        }
    }

    class Comparator: DiffUtil.ItemCallback<SearchLocation>(){
        override fun areItemsTheSame(oldItem: SearchLocation, newItem: SearchLocation): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SearchLocation, newItem: SearchLocation): Boolean {
            return oldItem == newItem
        }
    }
}