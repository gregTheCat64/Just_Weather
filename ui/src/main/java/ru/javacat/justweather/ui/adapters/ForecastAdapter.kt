package ru.javacat.justweather.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.javacat.justweather.common.util.asHour
import ru.javacat.justweather.ui.util.load
import ru.javacat.ui.R
import ru.javacat.ui.databinding.HourItemBinding
import kotlin.math.roundToInt


class ForecastAdapter(): ListAdapter<ru.javacat.justweather.domain.models.Hour, ForecastAdapter.Holder>(Comparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.hour_item, parent, false)
        return Holder(view)
    }


    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }

    class Holder(view: View): RecyclerView.ViewHolder(view){
        private val binding = HourItemBinding.bind(view)


        fun bind(item: ru.javacat.justweather.domain.models.Hour) {
            binding.apply {
                conditionImgView.load("${item.condition.icon}")
                hourValue.text = item.time.asHour()
                tempTxtView.text = item.temp_c.roundToInt().toString() + "\u00B0"
            }
        }
    }

    class Comparator: DiffUtil.ItemCallback<ru.javacat.justweather.domain.models.Hour>(){
        override fun areItemsTheSame(oldItem: ru.javacat.justweather.domain.models.Hour, newItem: ru.javacat.justweather.domain.models.Hour): Boolean {
            return oldItem.time_epoch == newItem.time_epoch
        }

        override fun areContentsTheSame(oldItem: ru.javacat.justweather.domain.models.Hour, newItem: ru.javacat.justweather.domain.models.Hour): Boolean {
            return oldItem == newItem
        }
    }
}