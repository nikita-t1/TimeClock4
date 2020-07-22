package com.studio.timeclock4.repositories

import com.studio.timeclock4.database.dao.AbsenceDao
import com.studio.timeclock4.database.entity.AbsenceEntity
import com.studio.timeclock4.database.entity.WorkDayEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.time.Duration
import java.time.LocalDate
import java.time.Year

class AbsenceRepository(
    private val absenceDao: AbsenceDao,
    private val workDayRepository: WorkDayRepository,
    private val sharedPrefRepo: SharedPreferencesRepository
) {

    private val firstDayOfYear = 1
    private val lastDayOfYear = 366

    val allAbsences: Flow<List<AbsenceEntity>> = absenceDao.getAbsencesObservable()

    suspend fun getAbsence(id: Long): AbsenceEntity? {
        return withContext(Dispatchers.IO) {
            absenceDao.getAbsenceByID(id)
        }
    }

    suspend fun getAbsenceByDate(date: LocalDate): AbsenceEntity? {
        return withContext(Dispatchers.IO) {
            absenceDao.getAbsenceByDate(date)
        }
    }

    suspend fun getAbsencesByYearObservable(year: Year): Flow<List<AbsenceEntity?>> {
        return withContext(Dispatchers.IO) {
            val startDate: LocalDate = LocalDate.ofYearDay(year.value, firstDayOfYear)
            val endDate: LocalDate = LocalDate.ofYearDay(year.value, lastDayOfYear)
            absenceDao.getAbsencesByYearObservable(startDate, endDate)
        }
    }

    suspend fun getAbsencesByYear(year: Year): List<AbsenceEntity?> {
        return withContext(Dispatchers.IO) {
            val startDate: LocalDate = LocalDate.ofYearDay(year.value, firstDayOfYear)
            val endDate: LocalDate = LocalDate.ofYearDay(year.value, lastDayOfYear)
            absenceDao.getAbsencesByYear(startDate, endDate)
        }
    }

    suspend fun insertAbsence(absence: AbsenceEntity) {
        withContext(Dispatchers.IO) {
            val absenceID = absenceDao.insertAbsence(absence)
            val list = sharedPrefRepo.workingDaysActiveList.invoke()

            for (iterator in 0L until absence.absenceDuration) {
                val date = absence.startDate.plusDays(iterator)
                if (list.contains(date.dayOfWeek)) {
                    var workDayEntity = workDayRepository.getWorkday(date)?.workDayEntity
                    when (workDayEntity) {
                        null -> {
                            workDayEntity = WorkDayEntity(
                                date = date,
                                baseTime = sharedPrefRepo.baseTime,
                                pauseTime = Duration.ZERO,
                                workTimeNet = sharedPrefRepo.baseTime,
                                absenceID = absenceID
                            )
                            workDayRepository.insertWorkDay(workDayEntity)
                        }
                        else -> {
                            when (workDayEntity.absenceID) {
                                null -> {
                                    workDayEntity.absenceID = absenceID
                                    workDayEntity.workTimeNet += sharedPrefRepo.baseTime
                                }
                                else -> TODO("THROW ERROR MESSAGE")
                            }
                            workDayRepository.updateWorkDay(workDayEntity)
                        }
                    }
                }
            }
        }
    }

    suspend fun updateAbsence(absence: AbsenceEntity) {
//        absenceDao.updateAbsence(absence)
        deleteAbsence(absence)
        insertAbsence(absence)
    }

    suspend fun deleteAbsence(absence: AbsenceEntity) {
        withContext(Dispatchers.IO) {
            for (iterator in 0L until absence.absenceDuration) {
                val day = absence.startDate.plusDays(iterator)
                val workDay = workDayRepository.getWorkday(day)!!
                when (workDay.workTimes.isEmpty()) {
                    true -> workDayRepository.deleteWorkDay(workDay.workDayEntity)
                    false -> {
                        workDay.workDayEntity.workTimeNet -= workDay.workDayEntity.baseTime
                        workDay.workDayEntity.absenceID = null
                        workDayRepository.updateWorkDay(workDay.workDayEntity)
                    }
                }
            }
            absenceDao.deleteAbsence(absence)
        }
    }

//    fun deleteAllAbsences() {
//        absenceDao.deleteAllAbsences()
//    }
}
