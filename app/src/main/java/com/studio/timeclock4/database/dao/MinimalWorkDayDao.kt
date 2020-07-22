package com.studio.timeclock4.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.studio.timeclock4.database.entity.WorkDayEntity
import com.studio.timeclock4.database.model.MinimalWorkDay
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface MinimalWorkDayDao {

    @Transaction
    @Query("SELECT * FROM ${WorkDayEntity.TABLE_NAME}")
    fun getMinimalWorkDaysObservable(): Flow<List<MinimalWorkDay>>

    @Transaction
    @Query("SELECT * FROM ${WorkDayEntity.TABLE_NAME}")
    fun getMinimalWorkDays(): List<MinimalWorkDay?>

    @Transaction
    @Query("SELECT * FROM ${WorkDayEntity.TABLE_NAME} WHERE datetime(date) = datetime(:date)")
    fun getWorkDayByDate(date: LocalDate): MinimalWorkDay?

    @Transaction
    @Query(
        """SELECT * FROM ${WorkDayEntity.TABLE_NAME} WHERE 
        datetime(date) BETWEEN datetime(:startDate) AND datetime(:endDate)"""
    )
    fun getMinimalWorkDaysBetween(startDate: LocalDate, endDate: LocalDate): List<MinimalWorkDay?>

    @Transaction
    @Query(
        """SELECT * FROM ${WorkDayEntity.TABLE_NAME} WHERE 
        datetime(date) BETWEEN datetime(:startDate) AND datetime(:endDate)"""
    )
    fun getMinimalWorkDaysObservableBetween(
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<List<MinimalWorkDay?>>
}
