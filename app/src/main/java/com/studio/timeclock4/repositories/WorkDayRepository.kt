package com.studio.timeclock4.repositories

import com.studio.timeclock4.database.dao.WorkDayDao
import com.studio.timeclock4.database.entity.WorkDayEntity
import com.studio.timeclock4.model.WorkDay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.time.LocalDate

// This class is not intended for direct usage
class WorkDayRepository(private val workDayDao: WorkDayDao) {

    val allWorkDays: Flow<List<WorkDay>> = workDayDao.getWorkDaysObservable()

    suspend fun getWorkday(date: LocalDate): WorkDay? {
        return withContext(Dispatchers.IO) {
            workDayDao.getWorkDayByDate(date)
        }
    }

    suspend fun getWorkDaysBetween(startDate: LocalDate, endDate: LocalDate): List<WorkDay?> {
        return withContext(Dispatchers.IO) {
            workDayDao.getWorkDaysBetween(startDate, endDate)
        }
    }

    suspend fun getWorkDaysBetweenObservable(startDate: LocalDate, endDate: LocalDate):
            Flow<List<WorkDay?>> {
        return withContext(Dispatchers.IO) {
            workDayDao.getWorkDaysObservableBetween(startDate, endDate)
        }
    }

    fun insertWorkDay(workDay: WorkDayEntity) {
        workDayDao.insertWorkDay(workDay)
    }

    fun updateWorkDay(workDay: WorkDayEntity) {
        workDayDao.updateWorkDay(workDay)
    }

    fun deleteWorkDay(workDay: WorkDayEntity) {
        workDayDao.deleteWorkDay(workDay)
    }

    fun deleteAllWorkDays() {
        workDayDao.deleteAllWorkDays()
    }
}
