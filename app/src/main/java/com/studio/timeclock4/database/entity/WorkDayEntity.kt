package com.studio.timeclock4.database.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.studio.timeclock4.database.entity.WorkDayEntity.Companion.TABLE_NAME
import java.time.Duration
import java.time.LocalDate

@Entity(tableName = TABLE_NAME)
data class WorkDayEntity(

    @PrimaryKey
    val date: LocalDate,

    val baseTime: Duration,
    var pauseTime: Duration,
    var workTimeNet: Duration,
    var absenceID: Long? = null

) {
    @Ignore
    val workTimeGross: Duration = workTimeNet.plus(pauseTime)

    @Ignore
    val overtime: Duration = workTimeGross.minus(baseTime)

    companion object {
        const val TABLE_NAME = "workday_table"
    }
}
