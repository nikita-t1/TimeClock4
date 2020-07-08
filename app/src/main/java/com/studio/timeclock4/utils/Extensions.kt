package com.studio.timeclock4.utils

import org.threeten.bp.Duration
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.temporal.ChronoUnit
import kotlin.math.floor

fun Duration.format(pattern: DateTimeFormatter): String{
    val seconds = this.seconds
    val localTime = LocalTime.ofSecondOfDay(seconds)
    return localTime.format(pattern)
}

fun Long.format(pattern: DateTimeFormatter, unit: ChronoUnit): String{
    val seconds = when (unit){
        ChronoUnit.NANOS -> this * NUMBER_NANOSECONDS_IN_SECONDS
        ChronoUnit.MICROS -> this * NUMBER_MICROSECONDS_IN_SECONDS
        ChronoUnit.MILLIS -> this * NUMBER_MILLISECONDS_IN_SECOND
        ChronoUnit.SECONDS -> this
        ChronoUnit.MINUTES -> floor(this * NUMBER_MINUTES_IN_SECONDS).toLong()
        else -> 0
    }
    val localTime = LocalTime.ofSecondOfDay(seconds)
    return localTime.format(pattern)
}