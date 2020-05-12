package com.studio.timeclock4.model

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface WorkDayDao {

    @Query("SELECT * FROM workday_table")
    fun getAllWorkDays(): LiveData<List<WorkDay>>

    @Query("SELECT * FROM workday_table WHERE year = :year AND month = :month AND dayOfMonth = :day")
    fun getWorkday(day: Int, month: Int, year: Int): WorkDay

    @Query("SELECT year, month, dayOfMonth, workTimeNet, overtime FROM workday_table WHERE year = :year AND weekOfYear = :weekOfYear")
    fun getMinimalWorkday(year: Int, weekOfYear: Int): List<MinimalWorkDay>

    @Query("SELECT * FROM workday_table WHERE workDayId = :id")
    fun getWorkday(id: Int): WorkDay

    @Insert
    suspend fun insertWorkDay(workDay: WorkDay)

    @Update
    suspend fun updateWorkDay(workDay: WorkDay)

    @Query("DELETE FROM workday_table")
    suspend fun deleteAllWorkDays()

    @Delete
    suspend fun deleteWorkDay(workDay: WorkDay)
}
