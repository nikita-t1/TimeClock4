package com.studio.timeclock4.model

import androidx.room.Embedded
import androidx.room.Relation
import com.studio.timeclock4.database.entity.AbsenceEntity
import com.studio.timeclock4.database.entity.WorkDayEntity
import com.studio.timeclock4.database.entity.WorkTimeEntity

data class WorkDay(

    @Embedded val workDayEntity: WorkDayEntity,

    @Relation(
        parentColumn = "absenceID",
        entityColumn = "absenceID"
    )
    val absence: AbsenceEntity?,

    @Relation(
        parentColumn = "date",
        entityColumn = "workTimeOwnerDate"
    )
    val workTimes: List<WorkTimeEntity>
)
