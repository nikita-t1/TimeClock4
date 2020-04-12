package com.studio.timeclock4.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.studio.timeclock4.R
import com.studio.timeclock4.model.WorkDay
import com.studio.timeclock4.model.WorkDayDao
import com.studio.timeclock4.model.WorkDayDatabase
import com.studio.timeclock4.model.WorkDayRepository
import com.studio.timeclock4.utils.CalendarUtils
import com.studio.timeclock4.utils.TimeCalculations
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDateTime
import timber.log.Timber
import com.studio.timeclock4.utils.PreferenceHelper as Pref

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
    private var startTimeMin: Long
    private var endTimeMin: Long
    private var pauseTimeMin: Long
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
                Pref.LAYOUT_STATE,
                LayoutState.Ready.ordinal
            )]
        }

        startTimeMin = Pref.read(Pref.START_TIME, Pref.Default_START_TIME)
        _startTime.apply {
            value = TimeCalculations.convertMinutesToDateString(startTimeMin)
        }

        endTimeMin = Pref.read(Pref.END_TIME, Pref.Default_END_TIME)
        _endTime.apply {
            value = TimeCalculations.convertMinutesToDateString(endTimeMin)
        }

        pauseTimeMin = Pref.read(Pref.PAUSE_TIME, Pref.Default_PAUSE_TIME)
        _pauseTime.apply {
            value = TimeCalculations.convertMinutesToDateString(pauseTimeMin)
        }

        workingTimeMin = Pref.read(Pref.WORKING_TIME, Pref.Default_WORKING_TIME)
        workingTimeWeekMin = workingTimeMin * Pref.read(Pref.WORKING_DAYS_WEEK, Pref.Default_WORKING_DAYS_WEEK)
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

            Pref.write(Pref.START_TIME, startTimeMin)
            endTimeMin = TimeCalculations.loadEndTime(startTimeMin, workingTimeMin, pauseTimeMin, true)
            Pref.write(Pref.END_TIME, endTimeMin)

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
        Pref.write(Pref.LAYOUT_STATE, currentLayoutStateOrdinal.value!!.ordinal)
        Timber.d(LayoutState.getState(currentLayoutStateOrdinal.value!!.ordinal).toString())

        if (Pref.read(Pref.START_TIME, 0L) == 0L) _startTime.apply { value = "00:00" }
        if (Pref.read(Pref.END_TIME, 0L) == 0L) _endTime.apply { value = "00:00" }

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
        Pref.remove(Pref.START_TIME)
        Pref.remove(Pref.END_TIME)
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
