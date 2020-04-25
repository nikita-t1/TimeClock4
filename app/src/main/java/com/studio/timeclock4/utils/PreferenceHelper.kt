package com.studio.timeclock4.utils

import android.content.Context
import android.content.SharedPreferences

object PreferenceHelper {

    private lateinit var prefs: SharedPreferences

    const val WORKING_TIME = "working_time"
    const val WORKING_TIME_WEEK = "working_time_week"
    const val PAUSE_TIME = "pause_time"
    const val FLEX_ACCOUNT = "flex_account"
    const val VACATION = "vacation"
    const val WORKING_DAYS_WEEK = "working_days_week"

    const val LAYOUT_STATE = "LAYOUT_STATE"
    const val CURRENT_START_TIME = "START_TIME"
    const val CURRENT_END_TIME = "END_TIME"
    const val CURRENT_PAUSE_TIME = "CURRENT_PAUSE_TIME"
    const val CURRENT_NOTE = "CURRENT_NOTE"

    const val Default_WORKING_TIME = 480L
    const val Default_PAUSE_TIME = 30L
    const val Default_START_TIME = 0L
    const val Default_END_TIME = 0L
    const val Default_WORKING_DAYS_WEEK = 5
    const val Default_FLEX_ACCOUNT = 1L
    const val Default_VACATION = 25L
    const val Default_WORKING_TIME_WEEK = 2400L
    const val Default_CURRENT_NOTE = ""

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
    const val DEV_TitleColor = "DEV_TitleColor"
    const val DEV_ColorTitle_U = "DEV_ColorTitle_U"
    const val DEV_MinFrames = 25
    const val DEV_DefaultDayButtonAnimationTime = 250L
    const val DEV_DefaultExtDayRevealDelay = 100L
    const val DEV_DefaultAmountWeeks = 313
    const val DEV_IconViewDp = 96f
    const val DEV_IconViewV26RoundPx = 0.16f
    const val DEV_IconVisibilityDelay = 100
    const val DEV_UpdateLink = "https://raw.githubusercontent.com/nikita-t1/TimeClock4/master/update.json"


    private const val PREFS_NAME = "params"

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
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