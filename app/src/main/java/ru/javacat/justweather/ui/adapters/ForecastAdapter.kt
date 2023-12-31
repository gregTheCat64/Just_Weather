package ru.javacat.justweather.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.javacat.justweather.R
import ru.javacat.justweather.databinding.HourItemBinding
import ru.javacat.justweather.domain.models.Hour
import ru.javacat.justweather.util.asHour
import ru.javacat.justweather.util.load
import kotlin.math.roundToInt


class ForecastAdapter(): ListAdapter<Hour, ForecastAdapter.Holder>(Comparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.hour_item, parent, false)
        return Holder(view)
    }


    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }

    class Holder(view: View): RecyclerView.ViewHolder(view){
        private val binding = HourItemBinding.bind(view)


        fun bind(item: Hour) {
            binding.apply {
                conditionImgView.load("${item.condition.icon}")
                hourValue.text = item.time.asHour()
                tempTxtView.text = item.temp_c.roundToInt().toString() + "\u00B0"
            }
        }
    }

    class Comparator: DiffUtil.ItemCallback<Hour>(){
        override fun areItemsTheSame(oldItem: Hour, newItem: Hour): Boolean {
            return oldItem.time_epoch == newItem.time_epoch
        }

        override fun areContentsTheSame(oldItem: Hour, newItem: Hour): Boolean {
            return oldItem == newItem
        }
    }
}