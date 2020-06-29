package com.studio.timeclock4.database.model

import androidx.room.Ignore
import org.threeten.bp.Duration
import org.threeten.bp.LocalDate

data class MinimalWorkDay(

    val date: LocalDate,
    val baseTime: Duration,
    val pauseTime: Duration,
    val workTimeNet: Duration,
    val absenceID: Long?

) {
    @Ignore
    val workTimeGross: Duration = workTimeNet.plus(pauseTime)

    @Ignore
    val overtime: Duration = workTimeGross.minus(baseTime)
}
