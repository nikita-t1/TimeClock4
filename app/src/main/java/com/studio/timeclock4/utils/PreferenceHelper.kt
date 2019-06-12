package com.studio.timeclock4.utils

import android.content.Context
import android.content.SharedPreferences

object PreferenceHelper {

    private lateinit var prefs: SharedPreferences

    private const val PREFS_NAME = "params"

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }


    fun read(key: String, value: String): String? {
        return prefs.getString(key, value)
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