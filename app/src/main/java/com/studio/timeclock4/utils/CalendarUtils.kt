package com.studio.timeclock4.utils

import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.temporal.ChronoUnit
import org.threeten.bp.temporal.WeekFields
import timber.log.Timber
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

object CalendarUtils {
    private val dateStringFull: DateFormat = SimpleDateFormat("EEEE, dd.MM.yyyy", Locale.getDefault())

    val startDate: LocalDateTime get() {
        val startDatePref = PreferenceHelper.read("startDate", "0")
        return if (startDatePref != "0") {
            dateStringToLdt(startDatePref)
        } else {
            ErrorHandler.react(ErrorTypes.ERROR02)
            LocalDateTime.now()
        }
    }

    val endDate: LocalDateTime =
        LocalDateTime.of(2025, 12, 31, 12, 12, 12)

    fun getWeekOfYear(ldt: LocalDateTime): Int {
        val weekField: WeekFields = WeekFields.of(Locale.getDefault())
        Timber.i("${weekField.weekOfWeekBasedYear()}")
        return ldt.get(weekField.weekOfWeekBasedYear())
    }

    fun getWeeksBetween(date1: LocalDateTime, date2: LocalDateTime): Long {
//        return Weeks.between(date1.toLocalDate(), date2).amount.toLong()
//        Timber.i("PLUS 1 ${ChronoUnit.WEEKS.between(date1, date2.plusDays(1))}")
//        Timber.i("PLUS 2 ${ChronoUnit.WEEKS.between(date1, date2.plusDays(2))}")
//        Timber.i("PLUS 3 ${ChronoUnit.WEEKS.between(date1, date2.plusDays(3))}")
//        Timber.i("PLUS 4 ${ChronoUnit.WEEKS.between(date1, date2.plusDays(4))}")
//        Timber.i("PLUS 5 ${ChronoUnit.WEEKS.between(date1, date2.plusDays(5))}")
//        Timber.i("PLUS 6 ${ChronoUnit.WEEKS.between(date1, date2.plusDays(6))}")

        return ChronoUnit.WEEKS.between(date1, date2)
    }

    fun getWeeksBetween() = PreferenceHelper.DEV_DefaultAmountWeeks

    fun dateStringToLdt(dateString: String): LocalDateTime {
        val formatter: DateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME
        return LocalDateTime.parse(dateString, formatter)
    }

    fun ldtToDateString(ldt: LocalDateTime): String {
        val formatter = DateTimeFormatter.ISO_DATE_TIME
        return ldt.format(formatter)
    }

    fun getFullDateString(): String {
        return dateStringFull.format(Date())
    }
}
