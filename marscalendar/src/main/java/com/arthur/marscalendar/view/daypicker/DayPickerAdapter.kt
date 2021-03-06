package com.arthur.marscalendar.view.daypicker


import com.arthur.marscalendar.model.Day
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.arthur.marscalendar.R
import kotlinx.android.synthetic.main.day_row_view.view.*

class DayPickerAdapter(var days: List<Day>, private val size: Int, private val listener: OnDayPickerListener): RecyclerView.Adapter<DayPickerAdapter.DayPickerViewHolder>() {
    private var selectedPos: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayPickerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.day_row_view, parent, false)
        view.minimumHeight = size
        return DayPickerViewHolder(view)
    }

    override fun getItemCount(): Int {
        return days.size
    }

    override fun onBindViewHolder(holder: DayPickerViewHolder, position: Int) {
        holder.bindViewHolder(days[position], position)
    }

    override fun onBindViewHolder(holder: DayPickerViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isNotEmpty() && payloads[0] is Boolean) {
            holder.bindSelected(days[position])
        } else {
            onBindViewHolder(holder, position)
        }

    }

    fun update(days: List<Day>) {
        this.days = days
        notifyDataSetChanged()
    }

    inner class DayPickerViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bindViewHolder(day: Day, position: Int) {
            itemView.mTextViewDay.text = if(day.value == 0) "" else day.value.toString()
            itemView.mTextViewDay.setTextColor(if (day.disabled) ContextCompat.getColor(itemView.context, R.color.disabled_date)
                                                else ContextCompat.getColor(itemView.context, R.color.enbaled_date))

            itemView.mFrameSelected.layoutParams.height = size.div(1.5).toInt()
            itemView.mFrameSelected.layoutParams.width = size.div(1.5).toInt()

            itemView.isEnabled = !day.disabled
            if (day.selected) {
                itemView.mFrameSelected.background = ContextCompat.getDrawable(itemView.context, R.drawable.selected_date)
                selectedPos = position
            } else {
                itemView.mFrameSelected.background = null
            }

            itemView.setOnClickListener {
                if (selectedPos > -1) {
                    val pos = selectedPos
                    days[pos].selected = false
                    notifyItemChanged(pos,true)
                }
                selectedPos = position
                days[selectedPos].selected = true
                notifyItemChanged(selectedPos, true)

                listener.onClickDay(days[position])
            }
        }

        fun bindSelected(day: Day) {
            if (day.selected) {
                itemView.mFrameSelected.background = ContextCompat.getDrawable(itemView.context, R.drawable.selected_date)
            } else {
                itemView.mFrameSelected.background = null
            }
        }
    }
}