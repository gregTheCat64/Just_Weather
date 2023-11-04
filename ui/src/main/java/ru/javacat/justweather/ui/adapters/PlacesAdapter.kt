package ru.javacat.justweather.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.javacat.justweather.common.util.toLocalDateTime
import ru.javacat.justweather.ui.util.load
import ru.javacat.justweather.ui.util.pushAnimation
import ru.javacat.ui.R
import ru.javacat.ui.databinding.PlaceItemBinding
import kotlin.math.roundToInt

interface OnPlacesInteractionListener {
    fun onSetPlace(item: ru.javacat.justweather.domain.models.Weather)

    fun onRemovePlace(item: ru.javacat.justweather.domain.models.Weather)

}
class PlacesAdapter(
    private val onInteractionListener: OnPlacesInteractionListener
): ListAdapter<ru.javacat.justweather.domain.models.Weather, PlacesAdapter.Holder>(Comparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.place_item, parent, false)
        return Holder(view, onInteractionListener)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }


    fun getItemAt(position: Int): ru.javacat.justweather.domain.models.Weather {
        return super.getItem(position)
    }

    class Holder(view: View, private val onInteractionListener: OnPlacesInteractionListener): RecyclerView.ViewHolder(view){
        private val binding = PlaceItemBinding.bind(view)

        fun bind(item: ru.javacat.justweather.domain.models.Weather) {
            val image = item.current.condition.icon
            binding.apply {
                conditionValue.text = item.current.condition.text
                nameValue.text = item.location.name
                regionValue.text = item.location.region + ", "+ item.location.country
                tempValue.text = item.current.temp_c.roundToInt().toString() + "°"
                updateTime.text = item.location.localtime.toLocalDateTime().toLocalTime().toString()
                conditionImage.load(image)
                root.setOnClickListener {
                    it.pushAnimation(binding.root.context)
                    onInteractionListener.onSetPlace(item)

                }
                locatedMarker.isVisible = item.isLocated
            }

        }
    }

    class Comparator: DiffUtil.ItemCallback<ru.javacat.justweather.domain.models.Weather>(){
        override fun areItemsTheSame(oldItem: ru.javacat.justweather.domain.models.Weather, newItem: ru.javacat.justweather.domain.models.Weather): Boolean {
            return oldItem.location == newItem.location
        }

        override fun areContentsTheSame(oldItem: ru.javacat.justweather.domain.models.Weather, newItem: ru.javacat.justweather.domain.models.Weather): Boolean {
            return oldItem == newItem
        }
    }
}