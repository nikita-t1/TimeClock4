package com.studio.timeclock4.utils

import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

object TimeCalculations {

    private var sdf_HHmm = SimpleDateFormat("HH:mm", Locale.getDefault())
    private var sdf_mm = SimpleDateFormat("mm", Locale.getDefault())

    fun loadStartTime(): Long {
        val calendar = Calendar.getInstance()
        val startHour = calendar.get(Calendar.HOUR_OF_DAY)
        val startMin = calendar.get(Calendar.MINUTE)
        Timber.d("startHour: $startHour")
        Timber.d("startMin: $startMin")
        return (startHour * 60 + startMin).toLong()
    }

    fun convertMinutesToDateString(min: Long): String {
        var min = min
        if (min < 0) {
            min *= -1
        }
        val date = sdf_mm.parse(min.toString())
        return sdf_HHmm.format(date)
    }

    fun convertMinutesToNegativeDateString(min: Long): String {
        Timber.e("$min")
        var minutes = if (min >= 0) min else (min * -1)

        Timber.e("$minutes")
        val date = sdf_mm.parse(minutes.toString())
        val HHmm = sdf_HHmm.format(date)
        return if (min >= 0) HHmm else "-$HHmm"
    }

    fun convertDateStringToMinutes(dateString: String): Long {
//        Timber.i("DAT $dateString")
//        sdf_HHmm.timeZone = TimeZone.getTimeZone("GMT");
//        val date = sdf_HHmm.parse(dateString)
//        Timber.i("DATE $date")
//        Timber.i("DATEE ${date.time}")
//        Timber.i("DATEE ${date.time/ 1000 / 60}")
//
//        val min = date.time / 1000 / 60
//        val hour = min / 60
//        Timber.i("DATE ${(hour / 60 + min)}")
//        return (hour / 60 + min)
        val parts = dateString.split(":").toTypedArray()
        val calc = (parts[0].toLong() * 60 + parts[1].toLong())
        Timber.i("DAT $calc   -> ${parts[0]}  -> ${parts[1]}")
        return calc
    }

    fun loadEndTime(startTimeMin: Long, workingTimeMin: Long, pauseTimeMin: Long, addPause: Boolean): Long {
        return if (addPause) {
            startTimeMin + workingTimeMin + pauseTimeMin
        } else {
            startTimeMin + workingTimeMin
        }
    }
}
