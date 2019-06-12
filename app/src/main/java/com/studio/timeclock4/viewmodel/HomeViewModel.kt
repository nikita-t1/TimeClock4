package com.studio.timeclock4.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.studio.timeclock4.R
import com.studio.timeclock4.utils.PreferenceHelper
import com.studio.timeclock4.utils.TimeCalculations

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    val TAG = this.javaClass.simpleName
    var currentLayoutState: LayoutState
    private val layoutStatePref = "layoutStatePref"

    var startTimeString = ""
    private var startTimeMin: Long
    private val startTimePref = "startTime"

    var endTimeString = ""
    private var endTimeMin: Long
    private val endTimeMinPref = "endTimeMin"

    private val workingTimeMinPref = "workingTimeMin"
    private var workingTimeMin: Long

    var startButtonText = ""
    var startButtonColor = R.color.amber

    enum class LayoutState() {
        Ready, Tracking, Saving;

        companion object {
            fun next(state: LayoutState): LayoutState {
                return when (state) {
                    Ready -> Tracking
                    Tracking -> Saving
                    Saving -> Ready
                }
            }

            fun getState(state: Int): LayoutState {
                return values()[state]
            }
        }
    }

    init {
        Log.d(TAG, "init")
        PreferenceHelper.init(application)
        currentLayoutState = LayoutState.values()[PreferenceHelper.read(layoutStatePref, LayoutState.Ready.ordinal)]
        startTimeMin = PreferenceHelper.read(startTimePref, 0L)
        startTimeString = TimeCalculations.convertMinutesToDateString(startTimeMin)
        endTimeMin = PreferenceHelper.read(endTimeMinPref, 0L)
        workingTimeMin = PreferenceHelper.read(workingTimeMinPref, 456L)
        updateLayoutState()
    }

    fun startPressed() {
        currentLayoutState = LayoutState.next(LayoutState.getState(currentLayoutState.ordinal))
        if (currentLayoutState == LayoutState.Tracking) {

            startTimeMin = TimeCalculations.loadStartTime()
            PreferenceHelper.write(startTimePref, startTimeMin)
            endTimeMin = TimeCalculations.loadEndTime(startTimeMin, workingTimeMin)
            PreferenceHelper.write(endTimeMinPref, endTimeMin)

            startTimeString = TimeCalculations.convertMinutesToDateString(startTimeMin)
            endTimeString = TimeCalculations.convertMinutesToDateString(endTimeMin)
        }
        updateLayoutState()
    }


    private fun updateLayoutState() {
        PreferenceHelper.write(layoutStatePref, currentLayoutState.ordinal)
        Log.d(TAG, LayoutState.getState(currentLayoutState.ordinal).toString())

        if (PreferenceHelper.read(startTimePref, 0L) == 0L) startTimeString = "00:00"
        if (PreferenceHelper.read(endTimeMinPref, 0L) == 0L) endTimeString = "00:00"

        when (currentLayoutState) {
            LayoutState.Ready -> {
                startButtonColor = R.color.green
                startButtonText = getApplication<Application>().resources.getString(R.string.start)
            }
            LayoutState.Tracking -> {
                startButtonColor = R.color.red
                startButtonText = getApplication<Application>().resources.getString(R.string.stop)
            }
            LayoutState.Saving -> {
                startButtonColor = R.color.red
                startButtonText = getApplication<Application>().resources.getString(R.string.stop)

            }
        }
    }

    fun dialogDismiss() {
        currentLayoutState = LayoutState.Tracking
        updateLayoutState()
    }

    fun dialogCancel() {
        currentLayoutState = LayoutState.Ready
        PreferenceHelper.remove(startTimePref)
        PreferenceHelper.remove(endTimeMinPref)
        updateLayoutState()
    }

    fun dialogSave() {
        currentLayoutState = LayoutState.Ready
        updateLayoutState()

        //TODO(Save to Room Database from here)
    }

    fun dialogResume() {
        currentLayoutState = LayoutState.Tracking
        updateLayoutState()
    }
}
