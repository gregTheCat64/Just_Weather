package ru.javacat.justweather.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.javacat.justweather.domain.models.suggestModels.Result
import ru.javacat.justweather.ui.util.pushAnimation
import ru.javacat.ui.R
import ru.javacat.ui.databinding.FoundItemBinding

interface OnSearchPlacesInteractionListener{
    fun onSetPlace(item: ru.javacat.justweather.domain.models.suggestModels.Result)
}

class SearchPlacesAdapter(
    private val listener: OnSearchPlacesInteractionListener
): ListAdapter<ru.javacat.justweather.domain.models.suggestModels.Result, SearchPlacesAdapter.Holder>(Comparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchPlacesAdapter.Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.found_item, parent, false)
        return SearchPlacesAdapter.Holder(view, listener)
    }

    override fun onBindViewHolder(holder: SearchPlacesAdapter.Holder, position: Int) {
        holder.bind(getItem(position))
    }

    class Holder(view: View, private val listener: OnSearchPlacesInteractionListener): RecyclerView.ViewHolder(view){
        private val binding = FoundItemBinding.bind(view)

        fun bind(item: ru.javacat.justweather.domain.models.suggestModels.Result){
            println("RESULT in adapter: $item")
            binding.apply {
                nameValue.text = item.title?.text.toString()
                //regionValue.text = item.address.component[0].name
                root.setOnClickListener {
                    it.pushAnimation(binding.root.context)
                    listener.onSetPlace(item)
                }
            }
        }
    }

    class Comparator: DiffUtil.ItemCallback<ru.javacat.justweather.domain.models.suggestModels.Result>(){
        override fun areItemsTheSame(oldItem: ru.javacat.justweather.domain.models.suggestModels.Result, newItem: ru.javacat.justweather.domain.models.suggestModels.Result): Boolean {
            return oldItem.subtitle?.text == newItem.subtitle?.text
        }

        override fun areContentsTheSame(oldItem: ru.javacat.justweather.domain.models.suggestModels.Result, newItem: Result): Boolean {
            return oldItem == newItem
        }
    }
}