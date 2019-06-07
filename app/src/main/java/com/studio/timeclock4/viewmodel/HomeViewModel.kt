package com.studio.timeclock4.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.studio.timeclock4.R
import com.studio.timeclock4.utils.PreferenceHelper

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    var startButtonText = ""
    var startButtonColor = R.color.green
    val TAG = this.javaClass.simpleName
    private var chronometerBase: Long = 0L
    var isStartPressed = false
    private val isStartPressedPref = "isStartPressed"
    private val chronometerBasePref = "chronometerBase"

    init {
        PreferenceHelper.init(application)
        isStartPressed = PreferenceHelper.read(isStartPressedPref, false)
        chronometerBase = PreferenceHelper.read(chronometerBasePref, 0L)
        Log.i(TAG, "isStartPressed: $isStartPressed")
        updateUiVariables()
    }

    fun startPressed() {
        isStartPressed = !isStartPressed
        updateUiVariables()
        Log.i(TAG, "STart: $isStartPressed")
        PreferenceHelper.write(isStartPressedPref, isStartPressed)
    }


    private fun updateUiVariables() {
        when (isStartPressed) {
            true -> {
                Log.i(TAG, "IF")
                startButtonColor = R.color.red
                startButtonText = getApplication<Application>().resources.getString(R.string.stop)
            }
            false -> {
                Log.i(TAG, "ELSE")
                startButtonColor = R.color.green
                startButtonText = getApplication<Application>().resources.getString(R.string.start)
            }
        }
    }
}
