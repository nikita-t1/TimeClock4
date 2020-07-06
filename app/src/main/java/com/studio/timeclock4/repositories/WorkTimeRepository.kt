package com.studio.timeclock4.repositories

import com.studio.timeclock4.database.dao.WorkTimeDao
import com.studio.timeclock4.database.entity.WorkDayEntity
import com.studio.timeclock4.database.entity.WorkTimeEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.threeten.bp.Duration
import org.threeten.bp.LocalDate

class WorkTimeRepository(
    private val workTimeDao: WorkTimeDao,
    private val workDayRepository: WorkDayRepository,
    private val sharedPrefRepo: SharedPreferencesRepository
) {
    val allWorkTimes: Flow<List<WorkTimeEntity>> = workTimeDao.getWorkTimesObservable()

    suspend fun getWorkTime(id: Long): WorkTimeEntity? {
        return withContext(Dispatchers.IO) {
            workTimeDao.getWorkTimeByID(id)
        }
    }

    suspend fun getWorkTimeByOwnerDate(date: LocalDate): List<WorkTimeEntity?> {
        return withContext(Dispatchers.IO) {
            workTimeDao.getWorkTimeByOwnerDate(date)
        }
    }

    suspend fun getWorkTimeByOwnerDateObservable(date: LocalDate): Flow<List<WorkTimeEntity?>> {
        return withContext(Dispatchers.IO) {
            workTimeDao.getWorkTimeByOwnerDateObservable(date)
        }
    }

    suspend fun insertWorkTime(workTime: WorkTimeEntity) {
        withContext(Dispatchers.IO) {
            val date = workTime.workTimeOwnerDate
            var workDay = workDayRepository.getWorkday(date)?.workDayEntity
            when (workDay) {
                null -> {
                    workDay = WorkDayEntity(
                        date = date,
                        baseTime = sharedPrefRepo.baseTime,
                        pauseTime = sharedPrefRepo.pauseTime,
                        workTimeNet = workTime.workTimeNet,
                        absenceID = null
                    )
                    workDayRepository.insertWorkDay(workDay)
                }
                else -> {
                    workDay.workTimeNet += workTime.workTimeNet
                    workDayRepository.updateWorkDay(workDay)
                }
            }
            workTimeDao.insertWorkTime(workTime)
        }
    }


    suspend fun updateWorkTime(workTime: WorkTimeEntity) {
//        workTimeDao.updateWorkTime(workTime)
        deleteWorkTime(workTime)
        insertWorkTime(workTime)
    }

    suspend fun deleteWorkTime(workTime: WorkTimeEntity) {
        withContext(Dispatchers.IO) {
            val date = workTime.workTimeOwnerDate
            val workDay = workDayRepository.getWorkday(date)!!
            val workTimesList = workDay.workTimes
            val workDayEntity = workDay.workDayEntity
            when (workTimesList.size) {
                0 -> TODO("THROW ERROR MESSAGE")
                1 -> when (workDay.absence) {
                    null -> workDayRepository.deleteWorkDay(workDayEntity)
                    else -> {
                        workDayEntity.pauseTime = Duration.ZERO
                        workDayEntity.workTimeNet -= workTime.workTimeNet
                        //workDayEntity.workTimeNet = sharedPrefRepo.baseTime
                        workDayRepository.updateWorkDay(workDayEntity)
                    }
                }
                else -> {
                    workDayEntity.workTimeNet -= workTime.workTimeNet
                    workDayRepository.updateWorkDay(workDayEntity)
                }
            }
            workTimeDao.deleteWorkTime(workTime)
        }
    }
}
