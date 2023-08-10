package ru.javacat.justweather.ui

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.javacat.justweather.R
import ru.javacat.justweather.databinding.DayItemBinding
import ru.javacat.justweather.response_models.Forecastday
import ru.javacat.justweather.util.asDayOfWeek
import ru.javacat.justweather.util.load
import ru.javacat.justweather.util.toLocalDate
import kotlin.math.roundToInt

class ForecastAdapter: ListAdapter<Forecastday, ForecastAdapter.Holder>(Comparator()) {

    class Holder(view: View): RecyclerView.ViewHolder(view){
        private val binding = DayItemBinding.bind(view)

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(item: Forecastday)  {
            binding.dayOfWeek.text = item.date.toLocalDate().asDayOfWeek()
            binding.maxTempTxtView.text = item.day.maxtemp_c.roundToInt().toString()+ "\u00B0"
            binding.minTempTxtView.text = item.day.mintemp_c.roundToInt().toString()+ "°"

            val image = "https://${item.day.condition.icon}"
            binding.conditionImgView.load(image.toUri().toString())
        }
    }

    class Comparator: DiffUtil.ItemCallback<Forecastday>(){
        override fun areItemsTheSame(oldItem: Forecastday, newItem: Forecastday): Boolean {
            return oldItem.date == newItem.date
        }

        override fun areContentsTheSame(oldItem: Forecastday, newItem: Forecastday): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.day_item, parent, false)
        return Holder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }
}