package ru.javacat.justweather.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.javacat.justweather.domain.models.suggestModels.FoundLocation
import ru.javacat.justweather.ui.util.pushAnimation
import ru.javacat.ui.R
import ru.javacat.ui.databinding.FoundItemBinding

interface OnSearchPlacesInteractionListener{
    fun onSetPlace(item: FoundLocation)
}

class SearchPlacesAdapter(
    private val listener: OnSearchPlacesInteractionListener
): ListAdapter<FoundLocation, SearchPlacesAdapter.Holder>(Comparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchPlacesAdapter.Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.found_item, parent, false)
        return SearchPlacesAdapter.Holder(view, listener)
    }

    override fun onBindViewHolder(holder: SearchPlacesAdapter.Holder, position: Int) {
        holder.bind(getItem(position))
    }

    class Holder(view: View, private val listener: OnSearchPlacesInteractionListener): RecyclerView.ViewHolder(view){
        private val binding = FoundItemBinding.bind(view)

        fun bind(item: FoundLocation){
            binding.apply {
//                for (i in item.address.component){
//                    locationName.append(i.name + ", ")
//                }


                val country =  item.address.component.firstOrNull() {
                    it.kind[0] == "COUNTRY"
                }?.name
                val province =  item.address.component.lastOrNull() {
                    it.kind[0] == "PROVINCE"
                }?.name?:""
                val localTitle =  item.address.component.lastOrNull() {
                    it.kind[0] == "LOCALITY"
                }?.name

                nameValue.text = localTitle

                regionValue.text = "$country, $province"

//                regionValue.text = when  {
//                    item.address.component.size>=3 -> {
//                        item.address.component[0].name + ", " + item.address.component[2].name
//                    }
//                    item.address.component.size>=2 -> {
//                        item.address.component[0].name + ", " +item.address.component[1].name
//                    }
//                    item.address.component.isNotEmpty() -> {
//                        item.address.component[0].name
//                    }
//
//                    else -> {""}
//                }
                root.setOnClickListener {
                    it.pushAnimation(binding.root.context)
                    listener.onSetPlace(item)
                }
            }
        }
    }

    class Comparator: DiffUtil.ItemCallback<FoundLocation>(){
        override fun areItemsTheSame(oldItem: FoundLocation, newItem: FoundLocation): Boolean {
            return oldItem.uri == newItem.uri
        }

        override fun areContentsTheSame(oldItem: FoundLocation, newItem: FoundLocation): Boolean {
            return oldItem == newItem
        }
    }
}