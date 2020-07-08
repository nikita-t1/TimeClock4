package com.studio.timeclock4.repositories

import com.studio.timeclock4.database.Converter
import com.studio.timeclock4.database.dao.SharedPreferencesDao
import org.threeten.bp.Duration
import org.threeten.bp.OffsetDateTime

class CurrentSessionRepository(
    private val sharedPrefDao: SharedPreferencesDao,
    private val converter: Converter
) {

    var layoutState: Int
        get() = sharedPrefDao.read(LAYOUT_STATE, DEFAULT_LAYOUT_STATE)
        set(value) = sharedPrefDao.write(LAYOUT_STATE, value)

    var startTime: OffsetDateTime
        get() = converter.stringToOffsetDateTime(sharedPrefDao.read(START_TIME, DEFAULT_START_TIME))
        set(value) = sharedPrefDao.write(START_TIME, converter.offsetDateTimeToString(value))

    var endTime: OffsetDateTime
        get() = converter.stringToOffsetDateTime(sharedPrefDao.read(END_TIME, DEFAULT_END_TIME))
        set(value) = sharedPrefDao.write(END_TIME, converter.offsetDateTimeToString(value))

    var currentPauseTime: Duration
        get() = converter.stringToDuration(sharedPrefDao.read(CURRENT_PAUSE_TIME, pauseTime))
        set(value) = sharedPrefDao.write(CURRENT_PAUSE_TIME, converter.durationToString(value))

    private val pauseTime: String
        get() = sharedPrefDao.read(PAUSE_TIME, DEFAULT_PAUSE_TIME)

    var currentNote: String?
        get() {
            return when (val note = sharedPrefDao.read(CURRENT_NOTE, DEFAULT_NOTE)) {
                DEFAULT_NOTE -> null
                else -> note
            }
        }
        set(value) = sharedPrefDao.write(CURRENT_NOTE, value!!)

    fun remove() {
        sharedPrefDao.remove(START_TIME)
        sharedPrefDao.remove(END_TIME)
        sharedPrefDao.remove(CURRENT_PAUSE_TIME)
        sharedPrefDao.remove(CURRENT_NOTE)
    }

    companion object {
        const val LAYOUT_STATE = "LAYOUT_STATE"
        const val DEFAULT_LAYOUT_STATE = 0

        const val START_TIME = "START_TIME"
        val DEFAULT_START_TIME = OffsetDateTime.now().toString()

        const val END_TIME = "END_TIME"
        val DEFAULT_END_TIME =
            OffsetDateTime.now().withHour(0).withMinute(0).toString()

        //CROSSOVER with SharedPreferencesRepository
        const val PAUSE_TIME = "PAUSE_TIME"
        const val DEFAULT_PAUSE_TIME = "PT1H"
        const val CURRENT_PAUSE_TIME = "PAUSE_TIME"

        const val CURRENT_NOTE = "CURRENT_NOTE"
        const val DEFAULT_NOTE = ""
    }
}
