package com.studio.timeclock4.model

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class WorkDayRepository(private val workDayDao: WorkDayDao) {

    val allWorkDays: LiveData<List<WorkDay>> = workDayDao.getAllWorkDays()

    suspend fun getWorkday(day : Int, month : Int, year :Int) : WorkDay{
        return withContext(Dispatchers.IO){
            Timber.i("${Thread.currentThread().name} $day $month $year")
            workDayDao.getWorkday(day, month, year)
        }
    }

    suspend fun getWorkday(id: Int) : WorkDay{
        return withContext(Dispatchers.IO){
            workDayDao.getWorkday(id)
        }
    }

    suspend fun getMinimalWorkday(year: Int, weekOfYear: Int): List<MinimalWorkDay>{
        return withContext(Dispatchers.IO){
            workDayDao.getMinimalWorkday(year, weekOfYear)
        }
    }

    suspend fun insertWorkDay(workDay: WorkDay) {
        workDayDao.insertWorkDay(workDay)
    }

    suspend fun updateWorkDay(workDay: WorkDay) {
        workDayDao.updateWorkDay(workDay)
    }

    suspend fun deleteWorkDay(workDay: WorkDay) {
        workDayDao.deleteWorkDay(workDay)
    }

    suspend fun deleteAllWorkDays() {
        workDayDao.deleteAllWorkDays()
    }
}