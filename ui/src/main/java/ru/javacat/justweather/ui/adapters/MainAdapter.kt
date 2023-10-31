package ru.javacat.justweather.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.javacat.justweather.common.util.asDayOfWeek
import ru.javacat.justweather.ui.util.load
import ru.javacat.ui.R
import ru.javacat.ui.databinding.DayItemBinding
import kotlin.math.roundToInt

interface OnInteractionListener {
    fun onForecastItem(item: ru.javacat.justweather.domain.models.Forecastday, view: View)
}

class MainAdapter(
    private val onInteractionListener: OnInteractionListener
): ListAdapter<ru.javacat.justweather.domain.models.Forecastday, MainAdapter.Holder>(Comparator()) {

    class Holder(view: View, private val onInteractionListener: OnInteractionListener): RecyclerView.ViewHolder(view){
        private val binding = DayItemBinding.bind(view)


        fun bind(item: ru.javacat.justweather.domain.models.Forecastday)  {
            binding.apply {
                val image = item.day.condition.icon
                dayOfWeek.text = item.date.asDayOfWeek()
                maxTempTxtView.text = item.day.avgtemp_c.roundToInt().toString()+ "\u00B0"
                //minTempTxtView.text = item.day.mintemp_c.roundToInt().toString()+ "°"
                root.setOnClickListener {
                    onInteractionListener.onForecastItem(item, binding.forecastLayout)
                }

                conditionImgView.load(image.toUri().toString())

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.day_item, parent, false)
        return Holder(view, onInteractionListener)
    }


    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }

    class Comparator: DiffUtil.ItemCallback<ru.javacat.justweather.domain.models.Forecastday>(){
        override fun areItemsTheSame(oldItem: ru.javacat.justweather.domain.models.Forecastday, newItem: ru.javacat.justweather.domain.models.Forecastday): Boolean {
            return oldItem.date == newItem.date
        }

        override fun areContentsTheSame(oldItem: ru.javacat.justweather.domain.models.Forecastday, newItem: ru.javacat.justweather.domain.models.Forecastday): Boolean {
            return oldItem == newItem
        }
    }
}