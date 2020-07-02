package com.studio.timeclock4.database.dao

import androidx.room.*
import com.studio.timeclock4.database.entity.WorkDayEntity
import com.studio.timeclock4.database.model.WorkDay
import kotlinx.coroutines.flow.Flow
import org.threeten.bp.LocalDate

@Dao
interface WorkDayDao {

    @Transaction
    @Query("SELECT * FROM ${WorkDayEntity.TABLE_NAME}")
    fun getWorkDaysObservable(): Flow<List<WorkDay>>

    @Transaction
    @Query("SELECT * FROM ${WorkDayEntity.TABLE_NAME}")
    fun getWorkDays(): List<WorkDay?>

    @Transaction
    @Query("SELECT * FROM ${WorkDayEntity.TABLE_NAME} WHERE datetime(date) = datetime(:date)")
    fun getWorkDayByDate(date: LocalDate): WorkDay?

    @Transaction
    @Query(
        """SELECT * FROM ${WorkDayEntity.TABLE_NAME} WHERE
        datetime(date) BETWEEN datetime(:startDate) AND datetime(:endDate)"""
    )
    fun getWorkDaysBetween(startDate: LocalDate, endDate: LocalDate): List<WorkDay?>

    @Transaction
    @Query(
        """SELECT * FROM ${WorkDayEntity.TABLE_NAME} WHERE 
        datetime(date) BETWEEN datetime(:startDate) AND datetime(:endDate)"""
    )
    fun getWorkDaysObservableBetween(startDate: LocalDate, endDate: LocalDate): Flow<List<WorkDay?>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertWorkDay(workDay: WorkDayEntity): Long

    @Update
    fun updateWorkDay(workDay: WorkDayEntity)

    @Delete
    fun deleteWorkDay(workDay: WorkDayEntity)

    @Query("DELETE FROM ${WorkDayEntity.TABLE_NAME}")
    fun deleteAllWorkDays()
}
