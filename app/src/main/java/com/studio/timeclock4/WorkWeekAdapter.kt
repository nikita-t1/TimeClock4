package com.studio.timeclock4

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.studio.timeclock4.model.WorkDay

class WorkWeekAdapter internal constructor(
    context: Context
) : RecyclerView.Adapter<WorkWeekAdapter.WorkDayViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var workDays = emptyList<WorkDay>()

    inner class WorkDayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var dayString: TextView = itemView.findViewById(R.id.dayString)
        var dateDate: TextView = itemView.findViewById(R.id.dateDate)
        var timeElapsed: TextView = itemView.findViewById(R.id.timeElapsed)
        var timeRemaining: TextView = itemView.findViewById(R.id.timeRemaining)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkDayViewHolder {
        val itemView = inflater.inflate(R.layout.item_workday, parent, false)
        return WorkDayViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: WorkDayViewHolder, position: Int) {
        val current = workDays[position]
//        holder.dayString.text = DayOfWeek.of(current.dayOfWeek).toString()
//        holder.timeElapsed.text = "12"
//        holder.timeRemaining.text = current.month.toString()
    }

    internal fun setWorkDays(workdays: List<WorkDay>) {
        this.workDays = workdays
        notifyDataSetChanged()
    }

    override fun getItemCount() = workDays.size

}