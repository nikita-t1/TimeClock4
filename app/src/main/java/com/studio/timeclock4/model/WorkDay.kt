package com.studio.timeclock4.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workday_table")
data class WorkDay(

    @PrimaryKey(autoGenerate = true)
    var workDayId: Int = 0,

    var year: Int,
    var weekOfYear: Int,
    var dayOfWeek: Int, // <-- can be omitted
    var dayOfMonth: Int,
    var month: Int,

    var timeClockIn: Int,
    var timeClockOut: Int,
    var pauseTime: Int,
    var workTimeGross: Int,
    var workTimeNet: Int,
    var overtime: Int,

    var wasPresent: Boolean,
    var absenceType: String?,
    var userNote: String?,
    var furtherAddition: String?
)