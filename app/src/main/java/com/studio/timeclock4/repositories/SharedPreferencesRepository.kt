package com.studio.timeclock4.repositories

import com.studio.timeclock4.database.Converter
import com.studio.timeclock4.database.dao.SharedPreferencesDao
import org.threeten.bp.DayOfWeek
import org.threeten.bp.Duration
import java.util.*

class SharedPreferencesRepository(
    private val sharedPrefDao: SharedPreferencesDao,
    private val converter: Converter
) {
    var flexTimeAccount: Duration
        get() = converter.stringToDuration(sharedPrefDao.read(FLEX_TIME_ACCOUNT, DEFAULT_FLEX_TIME_ACCOUNT))
        set(value) = sharedPrefDao.write(FLEX_TIME_ACCOUNT, converter.durationToString(value))

    var baseTime: Duration
        get() = converter.stringToDuration(sharedPrefDao.read(BASE_TIME, DEFAULT_BASE_TIME))
        set(value) = sharedPrefDao.write(BASE_TIME, converter.durationToString(value))

    var pauseTime: Duration
        get() = converter.stringToDuration(sharedPrefDao.read(PAUSE_TIME, DEFAULT_PAUSE_TIME))
        set(value) = sharedPrefDao.write(PAUSE_TIME, converter.durationToString(value))
    
    val baseTimeWeek: Duration
        get() = baseTime.multipliedBy(workingDaysWeek.toLong())

    val workingDaysWeek: Int
        get() = workingDaysList.count{true}

    val workingDaysList = listOf(
        isMondaySelected,
        isTuesdaySelected,
        isWednesdaySelected,
        isThursdaySelected,
        isFridaySelected,
        isSaturdaySelected,
        isSundaySelected
    )

    val workingDaysActiveList = {
        val list = mutableListOf<DayOfWeek>()
        if (isMondaySelected) list.add(DayOfWeek.MONDAY)
        if (isTuesdaySelected) list.add(DayOfWeek.TUESDAY)
        if (isWednesdaySelected) list.add(DayOfWeek.WEDNESDAY)
        if (isThursdaySelected) list.add(DayOfWeek.THURSDAY)
        if (isFridaySelected) list.add(DayOfWeek.FRIDAY)
        if (isSaturdaySelected) list.add(DayOfWeek.SATURDAY)
        if (isSundaySelected) list.add(DayOfWeek.SUNDAY)
        list
    }

    var isMondaySelected: Boolean
    get() = sharedPrefDao.read(IS_MONDAY_SELECTED, DEFAULT_IS_DAY_SELECTED)
    set(value) = sharedPrefDao.write(IS_MONDAY_SELECTED, value)

    var isTuesdaySelected: Boolean
    get() = sharedPrefDao.read(IS_TUESDAY_SELECTED, DEFAULT_IS_DAY_SELECTED)
    set(value) = sharedPrefDao.write(IS_TUESDAY_SELECTED, value)

    var isWednesdaySelected: Boolean
    get() = sharedPrefDao.read(IS_WEDNESDAY_SELECTED, DEFAULT_IS_DAY_SELECTED)
    set(value) = sharedPrefDao.write(IS_WEDNESDAY_SELECTED, value)

    var isThursdaySelected: Boolean
    get() = sharedPrefDao.read(IS_THURSDAY_SELECTED, DEFAULT_IS_DAY_SELECTED)
    set(value) = sharedPrefDao.write(IS_THURSDAY_SELECTED, value)

    var isFridaySelected: Boolean
    get() = sharedPrefDao.read(IS_FRIDAY_SELECTED, DEFAULT_IS_DAY_SELECTED)
    set(value) = sharedPrefDao.write(IS_FRIDAY_SELECTED, value)

    var isSaturdaySelected: Boolean
    get() = sharedPrefDao.read(IS_SATURDAY_SELECTED, DEFAULT_IS_WEEKEND_SELECTED)
    set(value) = sharedPrefDao.write(IS_SATURDAY_SELECTED, value)

    var isSundaySelected: Boolean
    get() = sharedPrefDao.read(IS_SUNDAY_SELECTED, DEFAULT_IS_WEEKEND_SELECTED)
    set(value) = sharedPrefDao.write(IS_SUNDAY_SELECTED, value)

    companion object {
    const val FLEX_TIME_ACCOUNT = "FLEX_TIME_ACCOUNT"
    const val DEFAULT_FLEX_TIME_ACCOUNT = "PT0S"

    const val BASE_TIME = "BASE_TIME"
    const val DEFAULT_BASE_TIME = "PT8H"

    const val PAUSE_TIME = "PAUSE_TIME"
    const val DEFAULT_PAUSE_TIME = "PT1H"

    const val IS_MONDAY_SELECTED = "IS_MONDAY_SELECTED"
    const val IS_TUESDAY_SELECTED = "IS_TUESDAY_SELECTED"
    const val IS_WEDNESDAY_SELECTED = "IS_WEDNESDAY_SELECTED"
    const val IS_THURSDAY_SELECTED = "IS_THURSDAY_SELECTED"
    const val IS_FRIDAY_SELECTED = "IS_FRIDAY_SELECTED"
    const val IS_SATURDAY_SELECTED = "IS_SATURDAY_SELECTED"
    const val IS_SUNDAY_SELECTED = "IS_SUNDAY_SELECTED"
    const val DEFAULT_IS_DAY_SELECTED = true
    const val DEFAULT_IS_WEEKEND_SELECTED = false
    }
}
