package com.studio.timeclock4.model

data class MinimalWorkDay(
//    val workDayId: Int,
    val year : Int,
    val month: Int,
    val dayOfMonth: Int,
    val workTimeNet: Int,
    val overtime: Int
)