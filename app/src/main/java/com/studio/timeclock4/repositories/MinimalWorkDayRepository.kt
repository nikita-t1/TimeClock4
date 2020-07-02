package com.studio.timeclock4.repositories

import com.studio.timeclock4.database.dao.MinimalWorkDayDao
import com.studio.timeclock4.database.model.MinimalWorkDay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDate

class MinimalWorkDayRepository(private val minimalWorkDayDao: MinimalWorkDayDao) {

    val allWorkDays: Flow<List<MinimalWorkDay>> =
        minimalWorkDayDao.getMinimalWorkDaysObservable()

    suspend fun getWorkday(date: LocalDate): MinimalWorkDay? {
        return withContext(Dispatchers.IO) {
            minimalWorkDayDao.getWorkDayByDate(date)
        }
    }

    suspend fun getWorkDaysBetween(
        startDate: LocalDate,
        endDate: LocalDate
    ): List<MinimalWorkDay?> {
        return withContext(Dispatchers.IO) {
            minimalWorkDayDao.getMinimalWorkDaysBetween(startDate, endDate)
        }
    }

    suspend fun getWorkDaysBetweenObservable(startDate: LocalDate, endDate: LocalDate)
            : Flow<List<MinimalWorkDay?>> {
        return withContext(Dispatchers.IO) {
            minimalWorkDayDao.getMinimalWorkDaysObservableBetween(startDate, endDate)
        }
    }
}
