package com.studio.timeclock4.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workday_table")
data class WorkDay(

    @PrimaryKey(autoGenerate = true)
    var workDayId: Int,

    var year: Int,
    var weekOfYear: Int,
    var dayOfWeek: Int,
    var dayOfMonth: Int,
    var month: Int,
    var timeClockIn: String,
    var timeClockOut: String,

    //Maybe Integer?
    var pauseTime: String,
    var workTimeGross: String,
    var workTimeNet: String,
    var overtime: String,

    var wasPresent: Boolean,
    var absenceType: String,
    var userNote: String,
    var furtherAddition: String
)
