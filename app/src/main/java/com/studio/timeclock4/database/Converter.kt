package com.studio.timeclock4.database

import androidx.room.TypeConverter
import java.time.Duration
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class Converter {

    private val offsetDateTimeFormat = DateTimeFormatter.ISO_OFFSET_DATE_TIME
    private val localDateFormat = DateTimeFormatter.ISO_LOCAL_DATE

    @TypeConverter
    fun offsetDateTimeToString(value: OffsetDateTime): String {
        return value.format(offsetDateTimeFormat)
    }

    @TypeConverter
    fun stringToOffsetDateTime(value: String): OffsetDateTime {
        return OffsetDateTime.parse(value)
    }

    @TypeConverter
    fun localDateToString(value: LocalDate): String {
        return value.format(localDateFormat)
    }

    @TypeConverter
    fun stringToLocalDate(value: String): LocalDate {
        return LocalDate.parse(value)
    }

    @TypeConverter
    fun durationToString(value: Duration): String {
        return value.toString()
    }

    @TypeConverter
    fun stringToDuration(value: String): Duration {
        return Duration.parse(value)
    }
}
