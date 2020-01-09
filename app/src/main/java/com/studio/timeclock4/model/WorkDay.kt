package com.studio.timeclock4.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workday_table")
data class WorkDay(

    @PrimaryKey(autoGenerate = true)
    var workDayId: Int = 0,

    var year: Int,
    var weekOfYear: Int,
    var dayOfWeek: Int,
    var dayOfMonth: Int,
    var month: Int,

    //Maybe Integer?
    var timeClockIn: String,
    var timeClockOut: String,
    var pauseTime: String,
    var workTimeGross: String,
    var workTimeNet: String,
    var overtime: String,

    var wasPresent: Boolean,
    var absenceType: String?,
    var userNote: String?,
    var furtherAddition: String?
)
