package com.studio.timeclock4.database.dao

import androidx.room.*
import com.studio.timeclock4.database.entity.WorkTimeEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface WorkTimeDao {

    @Query("SELECT * FROM ${WorkTimeEntity.TABLE_NAME}")
    fun getWorkTimesObservable(): Flow<List<WorkTimeEntity>>

    @Query("SELECT * FROM ${WorkTimeEntity.TABLE_NAME}")
    fun getWorkTimes(): List<WorkTimeEntity?>

    @Query("SELECT * FROM ${WorkTimeEntity.TABLE_NAME} WHERE workTimeID = :id")
    fun getWorkTimeByID(id: Long): WorkTimeEntity?

    @Query("SELECT * FROM ${WorkTimeEntity.TABLE_NAME} WHERE workTimeOwnerDate = :ownerDate")
    fun getWorkTimeByOwnerDate(ownerDate: LocalDate): List<WorkTimeEntity?>

    @Query("SELECT * FROM ${WorkTimeEntity.TABLE_NAME} WHERE workTimeOwnerDate = :ownerDate")
    fun getWorkTimeByOwnerDateObservable(ownerDate: LocalDate): Flow<List<WorkTimeEntity?>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertWorkTime(workTime: WorkTimeEntity): Long

    @Update
    fun updateWorkTime(workTime: WorkTimeEntity)

    @Delete
    fun deleteWorkTime(workTime: WorkTimeEntity)
}
