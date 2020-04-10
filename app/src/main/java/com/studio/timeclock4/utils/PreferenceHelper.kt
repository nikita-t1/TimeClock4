package com.studio.timeclock4.utils

import android.content.Context
import android.content.SharedPreferences
import timber.log.Timber

object PreferenceHelper {

    private lateinit var prefs: SharedPreferences

    const val updateLink = "updateLink"
    const val working_time = "working_time"
    const val working_time_week = "working_time_week"
    const val pause_time = "pause_time"
    const val flex_account = "flex_account"
    const val vacation = "vacation"
    const val working_days_week = "working_days_week"

    const val MONDAY_CHIP = "MONDAY_CHIP"
    const val TUESDAY_CHIP = "TUESDAY_CHIP"
    const val WEDNESDAY_CHIP = "WEDNESDAY_CHIP"
    const val THURSDAY_CHIP = "THURSDAY_CHIP"
    const val FRIDAY_CHIP = "FRIDAY_CHIP"
    const val SATURDAY_CHIP = "SATURDAY_CHIP"
    const val SUNDAY_CHIP = "SUNDAY_CHIP"

    const val DEV_EnableSaving = "DEV_EnableSaving"
    const val DEV_EnableFrames = "DEV_EnableFrames"
    const val DEV_EnableDayButtonAnimation = "DEV_EnableDayButtonAnimation"



    private const val PREFS_NAME = "params"

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        //Default Values
        write(working_time, 480L)
        write(working_time_week, 2400L)
        write(pause_time, 30L)
        write(flex_account, 1L)
        write(vacation, 1L)
        write(working_days_week, 5)
    }


    fun read(key: String, value: String): String {
        return prefs.getString(key, value) ?: ""
    }

    fun read(key: String, value: Boolean): Boolean {
        return prefs.getBoolean(key, value)
    }

    fun read(key: String, value: Int): Int {
        return prefs.getInt(key, value)
    }

    fun read(key: String, value: Long): Long {
        return prefs.getLong(key, value)
    }

    fun read(key: String, value: Float): Float {
        return prefs.getFloat(key, value)
    }

    fun write(key: String, value: String) {
        val prefsEditor: SharedPreferences.Editor = prefs.edit()
        prefsEditor.putString(key, value)
        prefsEditor.apply()
    }

    fun write(key: String, value: Boolean) {
        val prefsEditor: SharedPreferences.Editor = prefs.edit()
        prefsEditor.putBoolean(key, value)
        prefsEditor.apply()
    }

    fun write(key: String, value: Int) {
        val prefsEditor: SharedPreferences.Editor = prefs.edit()
        prefsEditor.putInt(key, value)
        prefsEditor.apply()
    }

    fun write(key: String, value: Long) {
        val prefsEditor: SharedPreferences.Editor = prefs.edit()
        prefsEditor.putLong(key, value)
        prefsEditor.apply()
    }

    fun write(key: String, value: Float) {
        val prefsEditor: SharedPreferences.Editor = prefs.edit()
        prefsEditor.putFloat(key, value)
        prefsEditor.apply()
    }

    fun remove(key: String) {
        val prefsEditor: SharedPreferences.Editor = prefs.edit()
        prefsEditor.remove(key)
        prefsEditor.apply()
    }
}