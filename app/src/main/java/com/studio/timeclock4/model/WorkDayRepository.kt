package com.studio.timeclock4.model

import androidx.lifecycle.LiveData

class WorkDayRepository(private val workDayDao: WorkDayDao) {

    val allWorkDays: LiveData<List<WorkDay>> = workDayDao.getAllWorkDays()

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