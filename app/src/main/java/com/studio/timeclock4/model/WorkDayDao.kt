package com.studio.timeclock4.model

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface WorkDayDao {

    @Query("SELECT * FROM workday_table")
    fun getAllWorkDays(): LiveData<List<WorkDay>>

    @Query("SELECT * FROM workday_table where weekOfYear = :weekOfYear")
    fun findByWeekOfYear(weekOfYear: Int): LiveData<List<WorkDay>>

    @Query("SELECT year FROM workday_table GROUP BY year")
    fun groupYears(): LiveData<List<Int>>

    @Query("SELECT weekOfYear FROM workday_table where year = :year GROUP BY weekOfYear")
    fun groupWeeks(year: Int): LiveData<List<Int>>

    @Insert
    suspend fun insertWorkDay(workDay: WorkDay)

    @Update
    suspend fun updateWorkDay(workDay: WorkDay)

    @Query("DELETE FROM workday_table")
    suspend fun deleteAllWorkDays()

    @Delete
    suspend fun deleteWorkDay(workDay: WorkDay)
}