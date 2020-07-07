package com.studio.timeclock4.utils

import org.threeten.bp.Duration
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter

fun Duration.format(pattern: DateTimeFormatter): String{
    val seconds = this.seconds
    val localTime = LocalTime.ofSecondOfDay(seconds)
    return localTime.format(pattern)
}