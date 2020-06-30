package com.studio.timeclock4.database.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.studio.timeclock4.database.entity.AbsenceEntity.Companion.TABLE_NAME
import org.threeten.bp.Duration
import org.threeten.bp.LocalDate

@Entity(tableName = TABLE_NAME)
data class AbsenceEntity(

    @PrimaryKey(autoGenerate = true)
    val absenceID: Long,

    val title: String,
    val startDate: LocalDate,
    val endDate: LocalDate
) {
    @Ignore
    val absenceDuration: Int = Duration.between(startDate, endDate).toDays().toInt()

    companion object {
        const val TABLE_NAME = "absence_table"
    }
}
