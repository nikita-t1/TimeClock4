package com.studio.timeclock4.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.studio.timeclock4.R
import com.studio.timeclock4.model.WorkDay
import com.studio.timeclock4.model.WorkDayDao
import com.studio.timeclock4.model.WorkDayDatabase
import com.studio.timeclock4.model.WorkDayRepository
import com.studio.timeclock4.utils.CalendarUtils
import com.studio.timeclock4.utils.PreferenceHelper as Pref
import com.studio.timeclock4.utils.TimeCalculations
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDateTime
import timber.log.Timber

class HomeViewModel(application: Application) : AndroidViewModel(application), LifecycleObserver {

    private var workDayDao: WorkDayDao =
        WorkDayDatabase.getWorkDayDatabase(application, viewModelScope).workDayDao()
    private val repository: WorkDayRepository

    private val _startButtonText = MutableLiveData<String>()
    val startButtonText: LiveData<String> = _startButtonText
    private val _startTime = MutableLiveData<String>()
    val startTimeString: LiveData<String> = _startTime
    private val _endTime = MutableLiveData<String>()
    val endTimeString: LiveData<String> = _endTime
    private val _pauseTime = MutableLiveData<String>()
    val _pauseTimeString: LiveData<String> = _pauseTime
    private val _currentLayoutStateOrdinal = MutableLiveData<LayoutState>()
    val currentLayoutStateOrdinal: LiveData<LayoutState> = _currentLayoutStateOrdinal
    private val _startButtonColor = MutableLiveData<Int>()
    val startButtonColor: LiveData<Int> = _startButtonColor


    private var workingTimeWeekMin: Long
    private val workingDaysAmountPref = "workingDaysAmountPref"

    private val layoutStatePref = "layoutStatePref"

    private var startTimeMin: Long
    private val startTimePref = "startTime"

    private var endTimeMin: Long
    private val endTimeMinPref = "endTimeMin"

    private var pauseTimeMin: Long
    private val pauseTimeMinPref = "pauseTimeMin"

    private val workingTimeMinPref = "workingTimeMin"
    private var workingTimeMin: Long

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun sexy() {
        var x = 1
        while (x < 15) {
//            Log.i("TAG", "HEYYY")
            x++ // Same as x += 1
        }
    }



    enum class LayoutState{
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
        repository = WorkDayRepository(workDayDao)

        Timber.d( "init")
        _currentLayoutStateOrdinal.apply {
            value = LayoutState.values()[Pref.read(
                layoutStatePref,
                LayoutState.Ready.ordinal
            )]
        }

        startTimeMin = Pref.read(startTimePref, 0L)
        _startTime.apply {
            value = TimeCalculations.convertMinutesToDateString(startTimeMin)
        }

        endTimeMin = Pref.read(endTimeMinPref, 0L)
        _endTime.apply {
            value = TimeCalculations.convertMinutesToDateString(endTimeMin)
        }

        //TODO: Needs to be set somewhere
        pauseTimeMin = Pref.read(Pref.pause_time, 5L)
        _pauseTime.apply {
            value = TimeCalculations.convertMinutesToDateString(pauseTimeMin)
        }

        //TODO: Needs to be set somewhere
//        workingTimeMin = PreferenceHelper.read(workingTimeMinPref, 456L)    //7,6h
        workingTimeMin = Pref.read(Pref.working_time, 480L)    //7,6h
        //TODO: Needs to be set somewhere
        workingTimeWeekMin = workingTimeMin * Pref.read(workingDaysAmountPref, 5)
        updateLayoutState()
    }


    fun startPressed() {
        _currentLayoutStateOrdinal.apply {
            value =
                LayoutState.next(LayoutState.getState(currentLayoutStateOrdinal.value!!.ordinal))
        }
        if (currentLayoutStateOrdinal.value == LayoutState.Tracking) {
            Timber.i("TIME ${LocalDateTime.now().second}")

            startTimeMin = TimeCalculations.loadStartTime()

            Pref.write(startTimePref, startTimeMin)
            endTimeMin = TimeCalculations.loadEndTime(startTimeMin, workingTimeMin, pauseTimeMin, true)
            Pref.write(endTimeMinPref, endTimeMin)

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
        Pref.write(layoutStatePref, currentLayoutStateOrdinal.value!!.ordinal)
        Timber.d(LayoutState.getState(currentLayoutStateOrdinal.value!!.ordinal).toString())

        if (Pref.read(startTimePref, 0L) == 0L) _startTime.apply { value = "00:00" }
        if (Pref.read(endTimeMinPref, 0L) == 0L) _endTime.apply { value = "00:00" }

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

    fun dialogCancel() {
        _currentLayoutStateOrdinal.apply { value = LayoutState.Ready }
        Pref.remove(startTimePref)
        Pref.remove(endTimeMinPref)
        updateLayoutState()
    }

    fun dialogSave(endTime: Long) {
        viewModelScope.launch {
            val ldt = LocalDateTime.now()
            Timber.e("WorkDay:\n" +
                    "workDayId = 0\n" +
                    "year = ${ldt.year}\n" +
                    "weekOfYear = ${CalendarUtils.getWeekOfYear(ldt)}\n" +
                    "dayOfWeek = ${ldt.dayOfWeek.value}\n" +
                    "dayOfMonth = ${ldt.dayOfMonth}\n" +
                    "month = ${ldt.month.value}\n" +
                    "timeClockIn = ${startTimeMin}\n" +
                    "timeClockOut = ${startTimeMin + endTime}\n" +
                    "pauseTime = ${pauseTimeMin}\n" +
                    "workTimeGross = ${endTime}\n" +
                    "wornTimeNet = ${endTime - pauseTimeMin}\n" +
                    "overtime = ${(endTime - pauseTimeMin) - workingTimeMin}\n" +
                    "wasPresent = true\n" +
                    "absenceType = null\n" +
                    "userNote = null\n" +   //Todo Let user add Note on LayoutState.Saving ??
                    "furtherAddition = null")
            val workDay = WorkDay(0,
                ldt.year,
                CalendarUtils.getWeekOfYear(ldt),
                ldt.dayOfWeek.value,
                ldt.dayOfMonth,
                ldt.month.value,
                startTimeMin.toInt(),
                (startTimeMin + endTime).toInt(),
                pauseTimeMin.toInt(),
                endTime.toInt(),
                (endTime - pauseTimeMin).toInt(),
                (endTime - pauseTimeMin - workingTimeMin).toInt(),
                true,
                null,
                "",
                null)
            if (Pref.read("enable saving", false)){
                repository.insertWorkDay(workDay)
            }
        }
        //Basically the same from here on
        dialogCancel()
    }

    fun dialogResume() {
        _currentLayoutStateOrdinal.apply { value = LayoutState.Tracking }
        updateLayoutState()
    }

    fun getArcProgress(workedTime: Long): Float {
        return (workedTime / workingTimeMin).toFloat()
    }
}
