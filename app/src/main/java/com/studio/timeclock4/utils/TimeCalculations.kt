package com.studio.timeclock4.utils

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*


object TimeCalculations {

    val TAG = this.javaClass.simpleName
    private var sdf_HHmm = SimpleDateFormat("HH:mm", Locale.getDefault())
    private var sdf_mm = SimpleDateFormat("mm", Locale.getDefault())


    fun loadStartTime(): Long {
        val calendar = Calendar.getInstance()
        val startHour = calendar.get(Calendar.HOUR_OF_DAY)
        val startMin = calendar.get(Calendar.MINUTE)
        Log.d(TAG, "startHour: $startHour")
        Log.d(TAG, "startMin: $startMin");
        return (startHour * 60 + startMin).toLong()
    }

    fun convertMinutesToDateString(min: Long): String {
        val date = sdf_mm.parse(min.toString())
        return sdf_HHmm.format(date)
    }

    fun convertDateStringToMinutes(dateString: String): Long {
        val date = sdf_HHmm.parse(dateString)
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR)
        val min = calendar.get(Calendar.MINUTE)
        return (hour * 60 + min).toLong()
    }

    fun loadEndTime(startTimeMin: Long, workingTimeMin: Long): Long {
        var endTime = startTimeMin + workingTimeMin
        return endTime
    }
}