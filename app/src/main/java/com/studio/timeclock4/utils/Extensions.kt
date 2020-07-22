package com.studio.timeclock4.utils

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import es.dmoral.toasty.Toasty
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.floor

fun Duration.format(pattern: DateTimeFormatter): String {
    val seconds = this.seconds
    val localTime = LocalTime.ofSecondOfDay(seconds)
    return localTime.format(pattern)
}

fun Long.format(pattern: DateTimeFormatter, unit: ChronoUnit): String {
    val seconds = when (unit) {
        ChronoUnit.NANOS -> this / NUMBER_NANOSECONDS_IN_SECONDS
        ChronoUnit.MICROS -> this / NUMBER_MICROSECONDS_IN_SECONDS
        ChronoUnit.MILLIS -> this / NUMBER_MILLISECONDS_IN_SECOND
        ChronoUnit.SECONDS -> this
        ChronoUnit.MINUTES -> floor(this * NUMBER_MINUTES_IN_SECONDS).toLong()
        else -> 0
    }
    val localTime = LocalTime.ofSecondOfDay(seconds)
    return localTime.format(pattern)
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun Fragment.toast(message: String, duration: Int = Toasty.LENGTH_SHORT) {
    Toasty.info(requireContext(), message, duration).show()
}

fun AppCompatActivity.toast(message: String, duration: Int = Toasty.LENGTH_SHORT) {
    Toasty.info(this, message, duration).show()
}
