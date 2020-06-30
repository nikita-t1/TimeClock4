package com.studio.timeclock4.database.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.studio.timeclock4.database.entity.WorkTimeEntity.Companion.TABLE_NAME
import org.threeten.bp.Duration
import org.threeten.bp.LocalDate
import org.threeten.bp.OffsetDateTime

@Entity(tableName = TABLE_NAME)
data class WorkTimeEntity(

    @PrimaryKey(autoGenerate = true)
    val workTimeID: Long,
    val workTimeOwnerDate: LocalDate,

    val timeClockIn: OffsetDateTime,
    val timeClockOut: OffsetDateTime,
    val userNote: String? = null
) {
    @Ignore
    val workTimeNet: Duration = Duration.between(timeClockIn, timeClockOut)

    companion object {
        const val TABLE_NAME = "worktime_table"
    }
}
