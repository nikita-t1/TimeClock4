package com.studio.timeclock4.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.studio.timeclock4.R
import com.studio.timeclock4.utils.PreferenceHelper

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    var startButtonText = ""
    var startButtonColor = R.color.amber
    val TAG = this.javaClass.simpleName
    //    private var chronometerBase: Long = 0L
    var currentLayoutState: LayoutState
    private val layoutState = "layoutState"
    private val chronometerBasePref = "chronometerBase"

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
//        chronometerBase = PreferenceHelper.read(chronometerBasePref, 0L)
        currentLayoutState = LayoutState.values()[PreferenceHelper.read(layoutState, LayoutState.Ready.ordinal)]
        updateLayoutState()
    }

    fun startPressed() {
        currentLayoutState = LayoutState.next(LayoutState.getState(currentLayoutState.ordinal))
        updateLayoutState()
    }


    private fun updateLayoutState() {
        PreferenceHelper.write(layoutState, currentLayoutState.ordinal)
        Log.d(TAG, LayoutState.getState(currentLayoutState.ordinal).toString())

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
