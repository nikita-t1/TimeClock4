package com.studio.timeclock4.database.dao

import androidx.room.*
import com.studio.timeclock4.database.entity.AbsenceEntity
import kotlinx.coroutines.flow.Flow
import org.threeten.bp.LocalDate

@Dao
interface AbsenceDao {

    @Query("SELECT * FROM ${AbsenceEntity.TABLE_NAME}")
    fun getAbsencesObservable(): Flow<List<AbsenceEntity>>

    @Query("SELECT * FROM ${AbsenceEntity.TABLE_NAME}")
    fun getAbsences(): List<AbsenceEntity?>

    @Query("SELECT * FROM ${AbsenceEntity.TABLE_NAME} WHERE absenceID = :id")
    fun getAbsenceByID(id: Long): AbsenceEntity?

    // TODO: Does this work ?!?!?!?!?!?!?!?!?!?!?!?!?!?!?!?!?
    @Query(
        """SELECT * FROM ${AbsenceEntity.TABLE_NAME} WHERE
            datetime(:date) BETWEEN datetime(startDate) AND datetime(endDate)"""
    )
    fun getAbsenceByDate(date: LocalDate): AbsenceEntity?

    @Query(
        """SELECT * FROM ${AbsenceEntity.TABLE_NAME} WHERE
        datetime(:startDate) >= datetime(startDate) AND datetime(:endDate) <= datetime(endDate)"""
    )
    fun getAbsencesByYearObservable(
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<List<AbsenceEntity?>>

    @Query(
        """SELECT * FROM ${AbsenceEntity.TABLE_NAME} WHERE
        datetime(:startDate) >= datetime(startDate) AND datetime(:endDate) <= datetime(endDate)"""
    )
    fun getAbsencesByYear(startDate: LocalDate, endDate: LocalDate): List<AbsenceEntity?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAbsence(absence: AbsenceEntity): Long

    @Update
    fun updateAbsence(absence: AbsenceEntity)

    @Delete
    fun deleteAbsence(absence: AbsenceEntity)

//    @Query("DELETE FROM ${AbsenceEntity.TABLE_NAME}")
//    abstract fun deleteAllAbsences()
}
