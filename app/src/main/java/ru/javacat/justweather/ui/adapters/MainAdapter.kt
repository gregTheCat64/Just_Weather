package ru.javacat.justweather.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.javacat.justweather.R
import ru.javacat.justweather.databinding.DayItemBinding
import ru.javacat.justweather.domain.models.Forecastday
import ru.javacat.justweather.domain.models.ForecastdayWithHours
import ru.javacat.justweather.util.asLocalDate
import ru.javacat.justweather.util.load
import kotlin.math.roundToInt

interface OnInteractionListener {
    fun onForecastItem(item: Forecastday, view: View)
}

class MainAdapter(
    private val onInteractionListener: OnInteractionListener
): ListAdapter<Forecastday, MainAdapter.Holder>(Comparator()) {

    class Holder(view: View, private val onInteractionListener: OnInteractionListener): RecyclerView.ViewHolder(view){
        private val binding = DayItemBinding.bind(view)


        fun bind(item: Forecastday)  {
            binding.apply {
                val image = item.day.condition.icon
                dayOfWeek.text = item.date.asLocalDate()
                maxTempTxtView.text = item.day.maxtemp_c.roundToInt().toString()+ "\u00B0"
                minTempTxtView.text = item.day.mintemp_c.roundToInt().toString()+ "°"
                root.setOnClickListener {
                    onInteractionListener.onForecastItem(item, binding.underLine)
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

    class Comparator: DiffUtil.ItemCallback<Forecastday>(){
        override fun areItemsTheSame(oldItem: Forecastday, newItem: Forecastday): Boolean {
            return oldItem.date == newItem.date
        }

        override fun areContentsTheSame(oldItem: Forecastday, newItem: Forecastday): Boolean {
            return oldItem == newItem
        }
    }
}