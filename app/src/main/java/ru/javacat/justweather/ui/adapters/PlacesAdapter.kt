package ru.javacat.justweather.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.javacat.justweather.R
import ru.javacat.justweather.databinding.PlaceItemBinding
import ru.javacat.justweather.domain.models.Weather
import ru.javacat.justweather.models.Place
import ru.javacat.justweather.util.changeColorOnPush
import ru.javacat.justweather.util.load
import ru.javacat.justweather.util.pushAnimation
import kotlin.math.roundToInt

interface OnPlacesInteractionListener {
    fun onSetPlace(item: Weather)

    fun onRemovePlace(item: Weather)

}
class PlacesAdapter(
    private val onInteractionListener: OnPlacesInteractionListener
): ListAdapter<Weather, PlacesAdapter.Holder>(Comparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.place_item, parent, false)
        return Holder(view, onInteractionListener)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }

    class Holder(view: View, private val onInteractionListener: OnPlacesInteractionListener): RecyclerView.ViewHolder(view){
        private val binding = PlaceItemBinding.bind(view)

        fun bind(item: Weather) {
            val image = item.current.condition.icon
            binding.apply {
                conditionValue.text = item.current.condition.text
                nameValue.text = item.location.name +", "+item.location.region
                tempValue.text = item.current.temp_c.roundToInt().toString() + "°"
                conditionImage.load(image)
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

    class Comparator: DiffUtil.ItemCallback<Weather>(){
        override fun areItemsTheSame(oldItem: Weather, newItem: Weather): Boolean {
            return oldItem.location == newItem.location
        }

        override fun areContentsTheSame(oldItem: Weather, newItem: Weather): Boolean {
            return oldItem == newItem
        }
    }
}