package com.studio.timeclock4.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.studio.timeclock4.R
import com.studio.timeclock4.model.WorkDayDao
import com.studio.timeclock4.model.WorkDayDatabase
import com.studio.timeclock4.model.WorkDayRepository
import com.studio.timeclock4.utils.PreferenceHelper
import com.studio.timeclock4.utils.TimeCalculations

class HomeViewModel(application: Application) : AndroidViewModel(application), LifecycleObserver {

    private var workDayDao: WorkDayDao
    val repository: WorkDayRepository

    private val _startButtonText = MutableLiveData<String>()
    val startButtonText: LiveData<String> = _startButtonText
    private val _startTime = MutableLiveData<String>()
    val startTimeString: LiveData<String> = _startTime
    private val _endTime = MutableLiveData<String>()
    val endTimeString: LiveData<String> = _endTime
    private val _currentLayoutStateOrdinal = MutableLiveData<LayoutState>()
    val currentLayoutStateOrdinal: LiveData<LayoutState> = _currentLayoutStateOrdinal
    private val _startButtonColor = MutableLiveData<Int>()
    val startButtonColor: LiveData<Int> = _startButtonColor


    private var workingTimeWeekMin: Long
    private val workingDaysAmountPref = "workingDaysAmountPref"

    val TAG = this.javaClass.simpleName
    private val layoutStatePref = "layoutStatePref"

    private var startTimeMin: Long
    private val startTimePref = "startTime"

    private var endTimeMin: Long
    private val endTimeMinPref = "endTimeMin"

    private val workingTimeMinPref = "workingTimeMin"
    private var workingTimeMin: Long

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun sex() {
        var x = 1
        while (x < 15) {
//            Log.i("TAG", "HEYYY")
            x++ // Same as x += 1
        }
    }



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
        workDayDao = WorkDayDatabase.getWorkDayDatabase(application, viewModelScope).workDayDao()
        repository = WorkDayRepository(workDayDao)

        Log.d(TAG, "init")
        PreferenceHelper.init(application)
        _currentLayoutStateOrdinal.apply {
            value = LayoutState.values()[PreferenceHelper.read(
                layoutStatePref,
                LayoutState.Ready.ordinal
            )]
        }

        startTimeMin = PreferenceHelper.read(startTimePref, 0L)
        _startTime.apply {
            value = TimeCalculations.convertMinutesToDateString(startTimeMin)
        }

        endTimeMin = PreferenceHelper.read(endTimeMinPref, 0L)
        _endTime.apply {
            value = TimeCalculations.convertMinutesToDateString(endTimeMin)
        }

        workingTimeMin = PreferenceHelper.read(workingTimeMinPref, 120L)
        workingTimeWeekMin = workingTimeMin * PreferenceHelper.read(workingDaysAmountPref, 5)
        updateLayoutState()
    }


    fun startPressed() {
        _currentLayoutStateOrdinal.apply {
            value =
                LayoutState.next(LayoutState.getState(currentLayoutStateOrdinal.value!!.ordinal))
        }
        if (currentLayoutStateOrdinal.value == LayoutState.Tracking) {

            startTimeMin = TimeCalculations.loadStartTime()
            PreferenceHelper.write(startTimePref, startTimeMin)
            endTimeMin = TimeCalculations.loadEndTime(startTimeMin, workingTimeMin)
            PreferenceHelper.write(endTimeMinPref, endTimeMin)

            _startTime.apply {
                value = TimeCalculations.convertMinutesToDateString(startTimeMin)
            }
            _endTime.apply {
                value = TimeCalculations.convertMinutesToDateString(endTimeMin)
            }
        }
        updateLayoutState()
    }


    private fun updateLayoutState() {
        PreferenceHelper.write(layoutStatePref, currentLayoutStateOrdinal.value!!.ordinal)
        Log.d(TAG, LayoutState.getState(currentLayoutStateOrdinal.value!!.ordinal).toString())

        if (PreferenceHelper.read(startTimePref, 0L) == 0L) _startTime.apply { value = "00:00" }
        if (PreferenceHelper.read(endTimeMinPref, 0L) == 0L) _endTime.apply { value = "00:00" }

        when (currentLayoutStateOrdinal.value) {
            LayoutState.Ready -> {
                _startButtonColor.apply { value = R.color.green }
                _startButtonText.apply {
                    value = getApplication<Application>().resources.getString(R.string.start)
                }
            }
            LayoutState.Tracking -> {
                _startButtonColor.apply { value = R.color.red }
                _startButtonText.apply {
                    value = getApplication<Application>().resources.getString(R.string.stop)
                }
            }
            LayoutState.Saving -> {
                _startButtonColor.apply { value = R.color.red }
                _startButtonText.apply {
                    value = getApplication<Application>().resources.getString(R.string.stop)
                }

            }
        }
    }

    fun dialogDismiss() {
        _currentLayoutStateOrdinal.apply { value = LayoutState.Tracking }
        updateLayoutState()
    }

    fun dialogCancel() {
        _currentLayoutStateOrdinal.apply { value = LayoutState.Ready }
        PreferenceHelper.remove(startTimePref)
        PreferenceHelper.remove(endTimeMinPref)
        updateLayoutState()
    }

    fun dialogSave() {
        _currentLayoutStateOrdinal.apply { value = LayoutState.Ready }
        updateLayoutState()

        //TODO(Save to Room Database from here)
    }

    fun dialogResume() {
        _currentLayoutStateOrdinal.apply { value = LayoutState.Tracking }
        updateLayoutState()
    }

    fun getArcProgress(workedTime: Long): Float {
        return (workedTime / workingTimeMin).toFloat()
    }
}
