package ru.javacat.justweather.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.javacat.justweather.R
import ru.javacat.justweather.databinding.PlaceItemBinding
import ru.javacat.justweather.models.Place
import ru.javacat.justweather.util.changeColorOnPush
import ru.javacat.justweather.util.pushAnimation

interface OnPlacesInteractionListener {
    fun onSetPlace(item: Place)

    fun onRemovePlace(item: Place)

}
class PlacesAdapter(
    private val onInteractionListener: OnPlacesInteractionListener
): ListAdapter<Place, PlacesAdapter.Holder>(Comparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.place_item, parent, false)
        return Holder(view, onInteractionListener)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }

    class Holder(view: View, private val onInteractionListener: OnPlacesInteractionListener): RecyclerView.ViewHolder(view){
        private val binding = PlaceItemBinding.bind(view)

        fun bind(item: Place) {
            binding.apply {
                placeId.text = item.id.toString()
                nameValue.text = item.region +", "+item.name
                root.setOnClickListener {
                    it.pushAnimation(binding.root.context)
                    onInteractionListener.onSetPlace(item)

                }
               removeBtn.setOnClickListener {
                    onInteractionListener.onRemovePlace(item)
                }
            }

        }
    }

    class Comparator: DiffUtil.ItemCallback<Place>(){
        override fun areItemsTheSame(oldItem: Place, newItem: Place): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Place, newItem: Place): Boolean {
            return oldItem == newItem
        }
    }
}