package com.studio.timeclock4.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.studio.timeclock4.BuildConfig
import com.studio.timeclock4.R
import com.studio.timeclock4.database.WorkDayDatabase
import com.studio.timeclock4.database.dao.WorkDayDao
import com.studio.timeclock4.database.entity.WorkDay
import com.studio.timeclock4.repositories.WorkDayRepository
import com.studio.timeclock4.utils.CalendarUtils
import com.studio.timeclock4.utils.TimeCalculations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDateTime
import timber.log.Timber
import com.studio.timeclock4.utils.PreferenceHelper as Pref

class HomeViewModel(application: Application) : AndroidViewModel(application), LifecycleObserver {
    val app = application

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
    val pauseTimeString: LiveData<String> = _pauseTime
    private val _currentLayoutStateOrdinal = MutableLiveData<LayoutState>()
    val currentLayoutStateOrdinal: LiveData<LayoutState> = _currentLayoutStateOrdinal
    private val _startButtonColor = MutableLiveData<Int>()
    val startButtonColor: LiveData<Int> = _startButtonColor

    private val _chronometerFormat = MutableLiveData<String>()
    val chronometerFormat: LiveData<String> = _chronometerFormat
    private val _remainingText = MutableLiveData<String>()
    val remainingText: LiveData<String> = _remainingText
    private val _progressBarDay = MutableLiveData<Int>()
    val progressBarDay: LiveData<Int> = _progressBarDay
    private val _progressBarWeek = MutableLiveData<Int>()
    val progressBarWeek: LiveData<Int> = _progressBarWeek
    private var workTimeNetWeek: Int = 0
    private var meanWorkTimeSetPoint: Int = 0 // Mittelwert der Sollarbeitszeit

    private var workingTimeWeekMin: Long
    private var startTimeMin: Long
    private var endTimeMin: Long
    private var pauseTimeMin: Long
    private var workingTimeMin: Long
    private var workingDaysWeek: Int
    var noteString: String

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        Timber.i("RESUME")
        viewModelScope.launch(Dispatchers.IO) {

            val ldt = LocalDateTime.now()
            workTimeNetWeek = 0
            var workDaySetPoint = 0
            val daysInDatabaseWeek = arrayListOf<Int>()
            val minimalWorkdayList =
                repository.getMinimalWorkday(ldt.year, CalendarUtils.getWeekOfYear(ldt))

            Timber.i("minimalWorkdayList ${minimalWorkdayList.size}")
            for (workday in minimalWorkdayList) {
                workTimeNetWeek += workday.workTimeNet
                if (workday.dayOfMonth !in daysInDatabaseWeek) { // !in == not in
                    daysInDatabaseWeek.add(workday.dayOfMonth)
                    workDaySetPoint += calcWorkTimeSetPoint(workday.workTimeNet, workday.overtime)
                }
            }

            val missingDays =
                when {
                    workingDaysWeek - daysInDatabaseWeek.size >= 0 -> workingDaysWeek - daysInDatabaseWeek.size
                    workingDaysWeek - daysInDatabaseWeek.size < 0 -> 0
                    else -> 0
                }

            if (workingDaysWeek > daysInDatabaseWeek.size) {
                workDaySetPoint += missingDays * workingTimeMin.toInt()
                meanWorkTimeSetPoint = workDaySetPoint / (daysInDatabaseWeek.size + missingDays) * workingDaysWeek
            } else {
                meanWorkTimeSetPoint = workDaySetPoint / daysInDatabaseWeek.size * workingDaysWeek
            }

            withContext(Dispatchers.Main) {
                _progressBarWeek.value = kotlin.math.floor(
                    ((workTimeNetWeek) / meanWorkTimeSetPoint.toFloat()) * 100).toInt()
            }
        }
    }

    enum class LayoutState {
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
        repository =
            WorkDayRepository(workDayDao)

        Timber.w("init")
        _currentLayoutStateOrdinal.apply {
            value = LayoutState.values()[Pref.read(
                Pref.LAYOUT_STATE,
                LayoutState.Ready.ordinal
            )]
        }

        startTimeMin = Pref.read(Pref.CURRENT_START_TIME, Pref.Default_START_TIME)
        _startTime.apply {
            value = TimeCalculations.convertMinutesToDateString(startTimeMin)
        }

        endTimeMin = Pref.read(Pref.CURRENT_END_TIME, Pref.Default_END_TIME)
        _endTime.apply {
            value = TimeCalculations.convertMinutesToDateString(endTimeMin)
        }

        pauseTimeMin = Pref.read(Pref.CURRENT_PAUSE_TIME, Pref.read(Pref.PAUSE_TIME, Pref.Default_PAUSE_TIME))
        _pauseTime.apply {
            value = TimeCalculations.convertMinutesToDateString(pauseTimeMin)
        }

        workingTimeMin = Pref.read(Pref.WORKING_TIME, Pref.Default_WORKING_TIME)
        Timber.w("$workingTimeMin")
        workingDaysWeek = Pref.read(Pref.WORKING_DAYS_WEEK, Pref.Default_WORKING_DAYS_WEEK)
        workingTimeWeekMin = workingTimeMin * workingDaysWeek

        noteString = Pref.read(Pref.CURRENT_NOTE, Pref.Default_CURRENT_NOTE)
        updateLayoutState()
    }

    fun startPressed() {

        _currentLayoutStateOrdinal.apply {
            value = LayoutState.next(LayoutState.getState(currentLayoutStateOrdinal.value!!.ordinal))
        }
        if (currentLayoutStateOrdinal.value == LayoutState.Tracking) {
            Timber.i("TIME ${LocalDateTime.now().second}")
            startTimeMin = TimeCalculations.loadStartTime()
            Pref.write(Pref.CURRENT_START_TIME, startTimeMin)
        }
        updateLayoutState()
    }

    private fun updateLayoutState() {
        Pref.write(Pref.LAYOUT_STATE, currentLayoutStateOrdinal.value!!.ordinal)
        Timber.d(LayoutState.getState(currentLayoutStateOrdinal.value!!.ordinal).toString())

        if (Pref.read(Pref.CURRENT_START_TIME, 0L) == 0L) _startTime.value = "00:00"
        if (Pref.read(Pref.CURRENT_END_TIME, 0L) == 0L) _endTime.value = "00:00"
        Timber.e(startTimeString.value)

        when (currentLayoutStateOrdinal.value) {
            LayoutState.Ready -> {
                _startButtonColor.apply { value = R.color.green }
                _startButtonText.apply { value = app.resources.getString(R.string.start) }
            }
            LayoutState.Tracking -> {
                _startButtonColor.apply { value = R.color.red }
                _startButtonText.apply { value = app.resources.getString(R.string.stop) }

                startTimeMin = Pref.read(Pref.CURRENT_START_TIME, Pref.Default_START_TIME)
                pauseTimeMin = Pref.read(Pref.CURRENT_PAUSE_TIME, Pref.read(Pref.PAUSE_TIME, Pref.Default_PAUSE_TIME))
                noteString = Pref.read(Pref.CURRENT_NOTE, Pref.Default_CURRENT_NOTE)

                endTimeMin = TimeCalculations.loadEndTime(startTimeMin, workingTimeMin, pauseTimeMin, true)
                Pref.write(Pref.CURRENT_END_TIME, endTimeMin)

                _startTime.apply {
                    value = TimeCalculations.convertMinutesToDateString(startTimeMin)
                }
                _endTime.apply {
                    value = TimeCalculations.convertMinutesToDateString(endTimeMin)
                }
                _pauseTime.apply {
                    value = TimeCalculations.convertMinutesToDateString(pauseTimeMin)
                }
            }
            LayoutState.Saving -> {
                _startButtonColor.apply { value = R.color.red }
                _startButtonText.apply { value = app.resources.getString(R.string.stop) }
            }
        }
    }

    fun onChronometerClick(elapsedMillis: Long, isHourFormat: Boolean) {
        val elapsedMin = elapsedMillis / 1000 / 60.0
        if (isHourFormat) {
            when {
                elapsedMillis < 3600000L -> _chronometerFormat.value = "00:%s"
                elapsedMillis < 3.6e+7 -> _chronometerFormat.value = "0%s"
                else -> _chronometerFormat.value = "%s"
            }
        } else {
            _chronometerFormat.value = "%s"
        }
        _progressBarDay.value = kotlin.math.floor((elapsedMin / (workingTimeMin + pauseTimeMin) * 100)).toInt()
        _remainingText.value =
            TimeCalculations.convertMinutesToDateString(((workingTimeMin + pauseTimeMin) - elapsedMin).toLong())
        Timber.i("$workTimeNetWeek + $elapsedMin")
        _progressBarWeek.value = kotlin.math.floor(((workTimeNetWeek + elapsedMin.toInt()) / meanWorkTimeSetPoint.toFloat()) * 100).toInt()
    }

    fun dialogCancel() {
        _currentLayoutStateOrdinal.apply { value = LayoutState.Ready }
        Pref.remove(Pref.CURRENT_START_TIME)
        Pref.remove(Pref.CURRENT_END_TIME)
        Pref.remove(Pref.CURRENT_PAUSE_TIME)
        Pref.remove(Pref.CURRENT_NOTE)
        updateLayoutState()
    }

    fun dialogSave(endTime: Long) {
        viewModelScope.launch {
            val ldt = LocalDateTime.now()
            val workDay = WorkDay(
                0,
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
                noteString,
                null
            )
            if (Pref.read(Pref.DEV_EnableSaving, true) or !BuildConfig.DEBUG) {
                val existingWorkDay = repository.getWorkday(ldt.dayOfMonth, ldt.monthValue, ldt.year)
                if (existingWorkDay != null) {
                    repository.deleteWorkDay(existingWorkDay)
                }
                repository.insertWorkDay(workDay)
            }
        }
        dialogCancel()
    }

    fun dialogResume() {
        _currentLayoutStateOrdinal.apply { value = LayoutState.Tracking }
        updateLayoutState()
    }

    fun getArcProgress(workedTime: Long): Float {
        return (workedTime / workingTimeMin).toFloat()
    }

    fun createTemporaryWorkDay(): WorkDay {
        val ldt = LocalDateTime.now()
        return WorkDay(
            0,
            ldt.year,
            CalendarUtils.getWeekOfYear(ldt),
            ldt.dayOfWeek.value,
            ldt.dayOfMonth,
            ldt.month.value,
            TimeCalculations.convertDateStringToMinutes(startTimeString.value.toString()).toInt(),
            ldt.hour * 60 + ldt.minute,
            TimeCalculations.convertDateStringToMinutes(pauseTimeString.value.toString()).toInt(),
            0,
            0,
            0,
            true,
            null,
            noteString,
            null
        )
    }

    fun setNewWorkDayValues(startTimeMin: Int, pauseTime: Int, noteString: String) {
        Pref.write(Pref.CURRENT_START_TIME, startTimeMin.toLong())
        Pref.write(Pref.CURRENT_PAUSE_TIME, pauseTime.toLong())
        Pref.write(Pref.CURRENT_NOTE, noteString)
        updateLayoutState()
    }

    private fun calcWorkTimeSetPoint(workTimeNet: Int, overtime: Int): Int {
        return workTimeNet - overtime
    }
}
